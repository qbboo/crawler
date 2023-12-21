package com.github.qbbo;

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

public class Crawler {
    CrawlerDAO dao = new MyBatisCrawlerDAO();

    public static void start() {
        Crawler crawler = new Crawler();
        crawler.crawlerWebsite();
    }

    private void crawlerWebsite() {
        String link;
        boolean isIndexLink = isFirstStart();
        while ((link = dao.removeFirst()) != null) {
            if (dao.hasFilterPool(link)) {
                System.out.printf("已访问: %s%n", link);
                continue;
            }
            dao.insertFilterPool(link);
            if (!isNewsLink(link) && !isIndexLink) {
                System.out.printf("不符合要求的链接: %s%n", link);
                continue;
            }
            Document html = getHtmlDocument(link);
            storeInsertDatabaseIfLinkIsNews(html, link);
            storeInsertLinkPoolIfLinkIsWant(html.select("a[href]"));
            awaitTime();
        }
    }

    private boolean isFirstStart() {
        return dao.countLinkPool() == 1 && dao.countFilterPool() == 0;
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
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
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
            if ("".equals(content)) {
                content = html.selectFirst("#content > article .entry-content").html();
            }

            dao.insertNews(title, content, link);

            System.out.printf("新闻标题：%s %n", title);
            System.out.printf("新闻内容：%s %n", content);
        }
    }

    private void storeInsertLinkPoolIfLinkIsWant(Elements aTags) {
         aTags.stream().map(aTag -> aTag.attr("href"))
                 .filter(link -> isNewsLink(link) && !dao.hasFilterPool(link) && !dao.hasLinkPool(link))
                 .map(this::getCorrectSpellLink)
                 .forEach(link -> dao.insertLinkPool(link));
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
}
