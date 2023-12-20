package com.github.qbbo;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.mysql.cj.jdbc.Driver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Crawler {
    private LinkPool linkPool;
    private FilterPool filterPool;
    private News news;
    Connection connection;

    public static void start() {
        ReadContext dbConfig = JsonPath.parse(readDBConfig("./db.config.json"));
        Properties properties = new Properties();
        properties.put("user", dbConfig.read("$.user"));
        properties.put("password", dbConfig.read("$.password"));
        try (Connection connection = new Driver().connect(dbConfig.read("$.jdbc"), properties);) {
            Crawler crawler = new Crawler();
            crawler.connection = connection;
            crawler.linkPool = new LinkPool();
            crawler.filterPool = new FilterPool();
            crawler.news = new News();

            crawler.crawlerWebsite();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readDBConfig(String file) {
        try {
            return  new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void crawlerWebsite() {
        String link;
        boolean isIndexLink = isFirstStart();
        while ((link = linkPool.removeFirst(connection)) != null) {
            if (hasFilterLinkPool(link)) {
                System.out.printf("已访问: %s%n", link);
                continue;
            }
            filterPool.insert(connection, link);
            if (!isNewsLink(link) && !isIndexLink) {
                System.out.printf("不符合要求的链接: %s%n", link);
                continue;
            }
            link = getCorrectSpellLink(link);
            Document html = getHtmlDocument(link);
            storeInsertDatabaseIfLinkIsNews(html, link);
            storeInsertLinkPoolIfLinkIsWant(html.select("a[href]"));
            awaitTime();
        }
    }

    private boolean isFirstStart() {
        return linkPool.count(connection) == 1 && filterPool.count(connection) == 0;
    }

    private static void awaitTime() {
        try {
            System.out.print("歇息3s \n");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Document getHtmlDocument(String link) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet require = new HttpGet(new URIBuilder(link).build().toString());
            require.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
            System.out.printf("开始访问link: %s %n", link);
            try (CloseableHttpResponse response = httpclient.execute(require);) {
                HttpEntity entity = response.getEntity();
                return Jsoup.parse(EntityUtils.toString(entity, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeInsertDatabaseIfLinkIsNews(Document html, String link) {
        if (html.selectFirst("#content > article .entry-title") != null) {
            String title = html.selectFirst("#content > article .entry-title").text();
            String content = html.selectFirst("#content > article .entry-content").text();

            news.insert(connection, title, content, link);

            System.out.printf("新闻标题：%s %n", title);
            System.out.printf("新闻内容：%s %n", content);
        }
    }

    private void storeInsertLinkPoolIfLinkIsWant(Elements aTags) {
         aTags.stream().map(aTag -> aTag.attr("href"))
                .filter(link -> isNewsLink(link) && !hasFilterLinkPool(link) && !hasLinkPool(link))
                .forEach(link -> linkPool.insert(connection, link));
    }

    private String getCorrectSpellLink(String link) {
        if (link.startsWith("//")) {
            link = "https:" + link;
        }
        return link;
    }

    private boolean isNewsLink(String link) {
        return link.contains("sina.com.hk") && link.contains("news");
    }

    private boolean hasFilterLinkPool(String link) {
        return filterPool.has(connection, link);
    }
    private boolean hasLinkPool(String link) {
        return linkPool.has(connection, link);
    }

}
