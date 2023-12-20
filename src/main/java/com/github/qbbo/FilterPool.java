package com.github.qbbo;

import java.sql.Connection;

public class FilterPool extends OperateDB{

    public void insert(Connection connection, String link) {
        String sql = "insert into filter_pool (link) values (?);";
        super.update(connection, sql, link);
    }
    public boolean has(Connection connection, String link) {
        String sql = "select link from filter_pool where link = ?";
        return super.has(connection, sql, link);
    }
}
