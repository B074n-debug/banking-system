package dao;

import model.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final DatabaseManager dbManager;

    public TransactionDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void save(Transaction tx) throws SQLException {
        String sql = """
                INSERT INTO transactions
                    (transaction_id, account_number, transaction_type, amount, balance_after, timestamp)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getTransactionId());
            ps.setString(2, tx.getAccountNumber());
            ps.setString(3, tx.getTransactionType());
            ps.setDouble(4, tx.getAmount());
            ps.setDouble(5, tx.getBalanceAfter());
            ps.setString(6, tx.getTimestamp().toString());
            ps.executeUpdate();
        }
    }

    public List<Transaction> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = """
                SELECT transaction_id, account_number, transaction_type, amount, balance_after, timestamp
                FROM transactions
                WHERE account_number = ?
                ORDER BY timestamp DESC
                """;
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rawTimestamp = rs.getString("timestamp");
                    LocalDateTime ts = (rawTimestamp != null && !rawTimestamp.isBlank())
                            ? LocalDateTime.parse(rawTimestamp)
                            : LocalDateTime.now();

                    list.add(new Transaction(
                            rs.getString("transaction_id"),
                            rs.getString("account_number"),
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getDouble("balance_after"),
                            ts
                    ));
                }
            }
        }
        return list;
    }
}
