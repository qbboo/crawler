package com.github.qbbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class News {
    public void insert (Connection connection, String title, String content, String link) {
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
}
