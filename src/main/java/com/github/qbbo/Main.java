package com.github.qbbo;

public class Main {
    public static void main(String[] args) {
        CrawlerDAO dao = new MyBatisCrawlerDAO();

        for (int i = 0; i < 6; i++) {
            new Thread(new Crawler(dao)).start();
        }
    }
}
