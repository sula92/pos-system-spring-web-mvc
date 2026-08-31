package com.pos.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IdGenerator {

    private IdGenerator() {}

    public static String nextCustomerId(Connection connection) throws SQLException {
        return nextId(connection, "customer_seq", "C");
    }

    public static String nextItemCode(Connection connection) throws SQLException {
        return nextId(connection, "item_seq", "I");
    }

    public static String nextOrderId(Connection connection) throws SQLException {
        return nextId(connection, "order_seq", "O");
    }

    private static String nextId(Connection connection, String sequenceName, String prefix) throws SQLException {
        String sql = "SELECT nextval('" + sequenceName + "')";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return String.format("%s%03d", prefix, rs.getInt(1));
        }
    }
}

