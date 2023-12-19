package com.github.qbbo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Crawler {
    private final LinkedList<String> linkPool = new LinkedList<>();
    private final Set<String> filterLinkPool = new HashSet<>();
    private final String indexLink;

    private Crawler(String indexLink) {
        this.indexLink = indexLink;
    }
    public static void start(String indexLink) {
        Crawler crawler = new Crawler(indexLink);
        crawler.linkPool.add(indexLink);
        crawler.crawlerWebsite();
    }

    private void crawlerWebsite() {
        while (!linkPool.isEmpty()) {
            String link = linkPool.removeFirst();
            if (hasFilterLinkPool(link)) {
                System.out.printf("已访问: %s\n", link);
                continue;
            }
            filterLinkPool.add(link);
            if (!isNewsLink(link) && !isIndexLink(link)) {
                System.out.printf("不符合要求的链接: %s\n", link);
                continue;
            }
            link = getCorrectSpellLink(link);
            Document html = getHtmlDocument(link);
            storeInsertDatabaseIfLinkIsNews(html, link);
            storeInsertLinkPoolIfLinkIsWant(html.select("a[href]"));
            awaitTime();
        }
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
        HttpGet require = new HttpGet(link);
        require.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        System.out.printf("开始访问link: %s \n", link);
        try (CloseableHttpResponse response = httpclient.execute(require);) {
            HttpEntity entity = response.getEntity();
            return Jsoup.parse(EntityUtils.toString(entity, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeInsertDatabaseIfLinkIsNews(Document html, String link) {
        if (html.selectFirst("#content > article .entry-title") != null) {
            String title = html.selectFirst("#content > article .entry-title").text();
            String content = html.selectFirst("#content > article .entry-content").text();
            System.out.printf("新闻标题：%s \n", title);
            System.out.printf("新闻内容：%s \n", content);
        }
    }

    private void storeInsertLinkPoolIfLinkIsWant(Elements aTags) {
        aTags.stream().map(aTag -> aTag.attr("href"))
                .filter(link -> isNewsLink(link) && !hasFilterLinkPool(link) && !hasLinkPool(link))
                .forEach(linkPool::add);
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

    private boolean isIndexLink(String link) {
        return indexLink.equals(link);
    }

    private boolean hasFilterLinkPool(String link) {
        return filterLinkPool.contains(link);
    }
    private boolean hasLinkPool(String link) {
        return linkPool.contains(link);
    }

}
