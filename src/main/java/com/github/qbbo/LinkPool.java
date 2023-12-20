package com.github.qbbo;

import java.sql.Connection;

public class LinkPool extends OperateDB {

    public String getFirst(Connection connection) {
        String sql = "select id, link from link_pool limit 1;";
        return super.select(connection, sql);
    }
    public String removeFirst(Connection connection) {
        String first = getFirst(connection);
        if (first != null) {
            remove(connection, first);
            return first;
        }
        return null;
    }
    public boolean has(Connection connection, String link) {
        String sql = "select link from link_pool where link = ?";
        return super.has(connection, sql, link);
    }
    public void insert(Connection connection, String link) {
        String sql = "insert into link_pool (link) values (?);";
        super.update(connection, sql, link);
    }

    public void remove(Connection connection, String link) {
        String sql = "delete from link_pool where link = ?";
        super.update(connection, sql, link);
    }
    public Integer count(Connection connection) {
        String sql = "select count(link) as count from link_pool";
        return super.count(connection, sql);
    }

}
