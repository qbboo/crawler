package com.github.qbbo;

import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ElasticsearchDataGenerator {
    public static void main(String[] args) {
        new ElasticsearchDataGenerator().migrateDataFromMySqlToES();
    }

    private void migrateDataFromMySqlToES() {
        SqlSessionFactory sqlSessionFactory;
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder()
                    .build(Resources.getResourceAsStream("db/mybatis/config.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<News> newsFromMySql = getNewsFromMySql(sqlSessionFactory);
        for (int i = 0; i < 6; i++) {
            new Thread(() -> writeSingleThead(newsFromMySql)).start();
        }
    }

    private static List<News> getNewsFromMySql(SqlSessionFactory sqlSessionFactory) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
            return sqlSession.selectList("com.github.qbbo.MockMapper.selectNews");
        }
    }

    private void writeSingleThead(List<News> newsList){
        try (RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));){
            BulkRequest bulkRequest = new BulkRequest();
            IndexRequest request = new IndexRequest("news");
            Integer count = 1;
            for (News news: newsList) {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("title", news.getTitle());
                jsonMap.put("content", news.getContent());
                jsonMap.put("link", news.getLink());
                jsonMap.put("createdAt", news.getCreatedAt());
                jsonMap.put("updatedAt", news.getUpdatedAt());
                request.source(jsonMap);
                bulkRequest.add(request);
                System.out.println("news title: " + news.getTitle());
                count++;
                if (count % 1000 == 0) {
                    BulkResponse bulkResponse = restClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    System.out.println("Current thead: " + Thread.currentThread().getName() + " finish. \n Response: " + bulkResponse.status());
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
