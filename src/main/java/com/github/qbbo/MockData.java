package com.github.qbbo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class MockData {
    public void mockData(SqlSessionFactory sqlSessionFactory, Integer howMuch) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            List<News> newsList = session.selectList("com.github.qbbo.MockMapper.selectNews");
            int count = howMuch - newsList.size();
            Random random = new Random();
            try {
                while (count-- > 0) {
                    System.out.println("count: " + count);
                    News news = newsList.get(random.nextInt(newsList.size()));
                    Instant createdTime = news.getCreatedAt().minusSeconds(60 * 60 * 24 * random.nextInt(365));
                    news.setCreatedAt(createdTime);
                    news.setUpdatedAt(createdTime.plusSeconds(60 * 60 * 24 * random.nextInt(365)));
                    news.setTitle(news.getTitle() + random.nextInt(36090));
                    session.insert("com.github.qbbo.MockMapper.insertNews", news);
                    if (count % 2000 == 0) {
                        session.flushStatements();
                    }
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) {
        try {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                    .build(Resources.getResourceAsStream("db/mybatis/config.xml"));
            new MockData().mockData(sqlSessionFactory, 40_0000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
