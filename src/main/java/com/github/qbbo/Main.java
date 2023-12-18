package com.github.qbbo;

public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler("https://portal.sina.com.hk/");
        crawler.start();
    }
}
