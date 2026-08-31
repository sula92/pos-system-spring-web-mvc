package com.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = System.getenv().getOrDefault(
            "POS_DB_URL",
            "jdbc:postgresql://localhost:5432/pos_system"
    );
    private static final String USER = System.getenv().getOrDefault("POS_DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("POS_DB_PASSWORD", "postgres");

    static {
        try {
            // Explicit driver loading avoids service-loader edge cases in some container boots.
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("PostgreSQL JDBC driver not found: " + e.getMessage());
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static final String[] SCHEMA_SQL = {
        "CREATE TABLE IF NOT EXISTS customers (" +
        "    id          VARCHAR(10)  PRIMARY KEY," +
        "    name        VARCHAR(100) NOT NULL," +
        "    address     VARCHAR(255)," +
        "    email       VARCHAR(150)" +
        ")",

        "CREATE TABLE IF NOT EXISTS items (" +
        "    code        VARCHAR(10)   PRIMARY KEY," +
        "    description VARCHAR(255)  NOT NULL," +
        "    unit_price  NUMERIC(10,2) NOT NULL CHECK (unit_price  >= 0)," +
        "    qty_on_hand INTEGER       NOT NULL CHECK (qty_on_hand >= 0)" +
        ")",

        "CREATE TABLE IF NOT EXISTS orders (" +
        "    order_id    VARCHAR(10) PRIMARY KEY," +
        "    date        DATE        NOT NULL," +
        "    customer_id VARCHAR(10) NOT NULL," +
        "    CONSTRAINT fk_orders_customer" +
        "        FOREIGN KEY (customer_id) REFERENCES customers(id)" +
        ")",

        "CREATE TABLE IF NOT EXISTS order_details (" +
        "    order_id   VARCHAR(10)   NOT NULL," +
        "    item_code  VARCHAR(10)   NOT NULL," +
        "    qty        INTEGER       NOT NULL CHECK (qty > 0)," +
        "    unit_price NUMERIC(10,2) NOT NULL CHECK (unit_price >= 0)," +
        "    PRIMARY KEY (order_id, item_code)," +
        "    CONSTRAINT fk_order_details_order" +
        "        FOREIGN KEY (order_id)  REFERENCES orders(order_id) ON DELETE CASCADE," +
        "    CONSTRAINT fk_order_details_item" +
        "        FOREIGN KEY (item_code) REFERENCES items(code)" +
        ")",

        "CREATE SEQUENCE IF NOT EXISTS customer_seq START 3",
        "CREATE SEQUENCE IF NOT EXISTS item_seq     START 3",
        "CREATE SEQUENCE IF NOT EXISTS order_seq    START 1",

        "INSERT INTO customers (id, name, address, email) VALUES " +
        "('C001', 'Nimal', 'Colombo', 'nimal@example.com')," +
        "('C002', 'Kamal', 'Galle',   'kamal@example.com') " +
        "ON CONFLICT (id) DO NOTHING",

        "INSERT INTO items (code, description, unit_price, qty_on_hand) VALUES " +
        "('I001', 'Lux Soap', 120.00, 100)," +
        "('I002', 'Signal',   150.00,  50) " +
        "ON CONFLICT (code) DO NOTHING"
    };

    public static void initializeSchema() {
        System.out.println("[POS-Spring] Initializing database schema...");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : SCHEMA_SQL) {
                stmt.execute(sql);
            }
            System.out.println("[POS-Spring] Database schema ready.");
        } catch (SQLException e) {
            throw new RuntimeException(
                    "[POS-Spring] Schema initialization failed. " +
                    "Make sure PostgreSQL is running and the database/user exist.\n" +
                    "  DB URL  : " + URL + "\n" +
                    "  DB User : " + USER, e);
        }
    }
}
