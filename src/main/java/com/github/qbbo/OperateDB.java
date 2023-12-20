package com.github.qbbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OperateDB {
    public void update(Connection connection, String sql, String link) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean has(Connection connection, String sql, String link) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, link);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String select(Connection connection, String sql) {
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

}
