package dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {
    private final String jdbcUrl;

    private static final List<Path> SCHEMA_CANDIDATES = List.of(
            Path.of("src/schema.sql"),
            Path.of("schema.sql"),
            Path.of("src/main/resources/schema.sql"),
            Path.of("resources/schema.sql")
    );

    public DatabaseManager(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("JDBC URL cannot be null or blank.");
        }
        this.jdbcUrl = jdbcUrl.trim();
    }

    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public void initializeSchema() throws Exception {
        Path foundSchema = null;
        for (Path candidate : SCHEMA_CANDIDATES) {
            if (Files.exists(candidate)) {
                foundSchema = candidate;
                break;
            }
        }

        String sql = (foundSchema != null) ? Files.readString(foundSchema) : defaultSchemaSql();
        executeSqlScript(sql);
        ensureMissingColumns();
    }

    private void executeSqlScript(String script) throws SQLException {
        String cleaned = script.replaceAll("(?m)--.*$", "");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String statement : cleaned.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    private void ensureMissingColumns() throws SQLException {
        try (Connection conn = getConnection()) {
            addColumnIfMissing(conn, "customers", "first_name", "TEXT");
            addColumnIfMissing(conn, "customers", "last_name", "TEXT");
            addColumnIfMissing(conn, "customers", "address", "TEXT");
            addColumnIfMissing(conn, "customers", "phone", "TEXT");
            addColumnIfMissing(conn, "accounts", "date_opened", "TEXT");
            addColumnIfMissing(conn, "accounts", "interest_rate", "REAL DEFAULT 0.0");
            addColumnIfMissing(conn, "accounts", "company", "TEXT");
            addColumnIfMissing(conn, "accounts", "company_address", "TEXT");
            addColumnIfMissing(conn, "transactions", "balance_after", "REAL NOT NULL DEFAULT 0.0");
        }
    }

    private void addColumnIfMissing(Connection conn, String tableName, String columnName, String columnDef) throws SQLException {
        if (!columnExists(conn, tableName, columnName)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDef + ";");
            }
        }
    }

    private boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ");")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String defaultSchemaSql() {
        return """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id TEXT PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    address TEXT,
                    phone TEXT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );

                CREATE TABLE IF NOT EXISTS accounts (
                    account_number TEXT PRIMARY KEY,
                    customer_id TEXT NOT NULL,
                    account_type TEXT NOT NULL,
                    balance REAL NOT NULL DEFAULT 0.0,
                    branch TEXT,
                    date_opened TEXT,
                    interest_rate REAL DEFAULT 0.0,
                    company TEXT,
                    company_address TEXT,
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                );

                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id TEXT PRIMARY KEY,
                    account_number TEXT NOT NULL,
                    transaction_type TEXT NOT NULL,
                    amount REAL NOT NULL,
                    balance_after REAL NOT NULL DEFAULT 0.0,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                );
                """;
    }
}