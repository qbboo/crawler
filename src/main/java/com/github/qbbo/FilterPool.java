package com.github.qbbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FilterPool {
    Connection connection;

    public FilterPool(Connection connection) {
        this.connection = connection;
    }

    public void insert(String link) {
        String sql = "insert into filter_pool (link) values (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean has(String link) {
        String sql = "select link from filter_pool where link = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
