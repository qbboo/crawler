package com.github.qbbo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public final class MyBatisCrawlerDAO implements CrawlerDAO{
    SqlSessionFactory sqlSessionFactory;

    public MyBatisCrawlerDAO() {
        String resource = "db/mybatis/config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource);) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(String statement, String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.update(statement, link);
        }

    }

    @Override
    public String select(String statement) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectOne(statement);
        }
    }

    @Override
    public Integer count(String statement) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectOne(statement);
        }
    }

    @Override
    public Integer count(String statement, String link) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            return session.selectOne(statement, link);
        }
    }

    @Override
    public void insertNews(String title, String content, String link) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.update("com.github.qbbo.MyMapper.insertNews", new News(title, content, link));
        }

    }

    @Override
    public void insertFilterPool(String link) {
        update("com.github.qbbo.MyMapper.insertFilterPool", link);
    }

    @Override
    public boolean hasFilterPool(String link) {
        return count("com.github.qbbo.MyMapper.countFilterPoolHasLink", link) > 0;
    }

    @Override
    public Integer countFilterPool() {
        return count("com.github.qbbo.MyMapper.countFilterPool");
    }

    @Override
    public boolean hasLinkPool(String link) {
        return count("com.github.qbbo.MyMapper.countLinkPoolHasLink", link) > 0;
    }

    @Override
    public void insertLinkPool(String link) {
        update("com.github.qbbo.MyMapper.insertLinkPool", link);
    }

    @Override
    public void removeLinkPool(String link) {
        update("com.github.qbbo.MyMapper.deleteLinkPool", link);
    }

    @Override
    public Integer countLinkPool() {
        return count("com.github.qbbo.MyMapper.countLinkPool");
    }

    @Override
    public synchronized String removeFirst() {
        String link = select("com.github.qbbo.MyMapper.selectFirstLink");
        if (link != null) {
            removeLinkPool(link);
        }
        return link;
    }
}
