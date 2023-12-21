package com.github.qbbo;

public interface CrawlerDAO {
    void update(String sql, String link);
    String select(String sql);
    Integer count(String sql);
    Integer count(String sql, String link);

    void insertNews (String title, String content, String link);
    void insertFilterPool(String link);
    boolean hasFilterPool(String link);
    Integer countFilterPool();
    boolean hasLinkPool(String link);
    void insertLinkPool(String link);
    void removeLinkPool(String link);
    Integer countLinkPool();
    String removeFirst();
}
