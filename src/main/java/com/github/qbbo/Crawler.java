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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Crawler {
    private final LinkedList<String> linkPool = new LinkedList<>();
    private final Set<String> visitedLinkPool = new HashSet<>();
    private final String entryLink;

    public Crawler(String entryLink) {
        this.entryLink = entryLink;
    }

    public void start() {
        linkPool.add(entryLink);
        crawlerWebsite();
    }

    private void crawlerWebsite() {
        while (!linkPool.isEmpty()) {
            String link = linkPool.removeFirst();
            if (isVisitedContainLink(link)) {
                System.out.printf("已访问: %s\n", link);
                continue;
            }
            visitedLinkPool.add(link);
            if (!isNewsLink(link) && !isEntryLink(link)) {
                System.out.printf("不符合要求的链接: %s\n", link);
                continue;
            }
            link = checkLinkAndBack(link);
            visitLinkThenSaveOtherLinkOrSaveContent(link);
        }
    }

    private void visitLinkThenSaveOtherLinkOrSaveContent(String link) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet require = new HttpGet(link);
        require.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        System.out.printf("开始访问link: %s \n", link);
        try (CloseableHttpResponse response = httpclient.execute(require);) {
            HttpEntity entity = response.getEntity();
            Document html = Jsoup.parse(EntityUtils.toString(entity, StandardCharsets.UTF_8));
            saveContentIfPageIsNews(html, link);
            appendLinkToLinkPool(html.select("a"));
            System.out.print("歇息3s \n");
            Thread.sleep(3000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveContentIfPageIsNews(Document html, String link) {
        if (html.selectFirst("#content > article .entry-title") != null) {
            String title = html.selectFirst("#content > article .entry-title").text();
            String content = html.selectFirst("#content > article .entry-content").text();
            System.out.printf("新闻标题：%s \n", title);
            System.out.printf("新闻内容：%s \n", content);
        }
    }

    private void appendLinkToLinkPool(Elements aTags) {
        for (Element aTag : aTags) {
            String link = aTag.attr("href");
            if (isNewsLink(link) && !isVisitedContainLink(link) && !isPoolContainLink(link)) {
                linkPool.add(link);
            }
        }
    }

    private String checkLinkAndBack(String link) {
        if (link.startsWith("//")) {
            link = "https:" + link;
        }
        return link;
    }

    private boolean isNewsLink(String link) {
        return link.contains("sina.com.hk") && link.contains("news");
    }

    private boolean isEntryLink(String link) {
        return entryLink.equals(link);
    }

    private boolean isVisitedContainLink(String link) {
        return visitedLinkPool.contains(link);
    }
    private boolean isPoolContainLink(String link) {
        return linkPool.contains(link);
    }

}
