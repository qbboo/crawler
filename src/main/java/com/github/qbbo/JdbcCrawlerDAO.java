package com.github.qbbo;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.mysql.cj.jdbc.Driver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public final class JdbcCrawlerDAO implements CrawlerDAO {
    Connection connection;

    public JdbcCrawlerDAO() {
        ReadContext dbConfig = JsonPath.parse(readDBConfig("./db.config.json"));
        Properties properties = new Properties();
        properties.put("user", dbConfig.read("$.user"));
        properties.put("password", dbConfig.read("$.password"));
        try {
            this.connection = new Driver().connect(dbConfig.read("$.jdbc"), properties);
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
    @Override
    public void update(String sql, String link) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String select(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("link");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Integer count(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer count(String sql, String link) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertNews (String title, String content, String link) {
        String sql = "insert into news (title, content, link) values ( ?, ?, ? )";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setString(3, link);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertFilterPool(String link) {
        String sql = "insert into filter_pool (link) values (?);";
        update(sql, link);
    }
    @Override
    public boolean hasFilterPool(String link) {
        String sql = "select count(link) as count from filter_pool where link = ?";
        return count(sql, link) > 0;
    }
    @Override
    public Integer countFilterPool() {
        String sql = "select count(link) as count from filter_pool";
        return count(sql);
    }


    @Override
    public boolean hasLinkPool(String link) {
        String sql = "select count(link) as count from link_pool where link = ?";
        return count(sql, link) > 0;
    }
    @Override
    public void insertLinkPool(String link) {
        String sql = "insert into link_pool (link) values (?);";
        update(sql, link);
    }
    @Override
    public void removeLinkPool(String link) {
        String sql = "delete from link_pool where link = ?";
        update(sql, link);
    }
    @Override
    public Integer countLinkPool() {
        String sql = "select count(link) as count from link_pool";
        return count(sql);
    }

    private String getFirstLinkPool() {
        String sql = "select id, link from link_pool limit 1;";
        return select(sql);
    }
    @Override
    public String removeFirst() {
        String link = getFirstLinkPool();
        if (link != null) {
            removeLinkPool(link);
            return link;
        }
        return null;
    }
}
