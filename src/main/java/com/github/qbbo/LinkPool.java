package com.github.qbbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkPool {
    Connection connection;

    public LinkPool(Connection connection) {
        this.connection = connection;
    }

    public String removeFirst() {
        String sql = "select id, link from link_pool limit 1;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                remove(resultSet.getInt("id"));
                return resultSet.getString("link");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isEmpty() {
        String sql = "select link from link_pool;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return !statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean has(String link) {
        String sql = "select link from link_pool where link = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insert(String link) {
        String sql = "insert into link_pool (link) values (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Integer id) {
        String sql = "delete from link_pool where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
