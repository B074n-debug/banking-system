package dao;

import model.Account;
import model.ChequeAccount;
import model.InvestmentAccount;
import model.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final DatabaseManager dbManager;

    public AccountDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void save(Account acc) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, date_opened, interest_rate, company, company_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getAccountNumber());
            ps.setString(2, acc.getCustomerId());
            ps.setString(3, acc.getAccountType());
            ps.setDouble(4, acc.getBalance());
            ps.setString(5, acc.getBranch());
            ps.setString(6, acc.getDateOpened().toString());

            if (acc instanceof SavingsAccount s) {
                ps.setDouble(7, s.getInterestRate());
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            } else if (acc instanceof InvestmentAccount inv) {
                ps.setDouble(7, inv.getInterestRate());
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            } else if (acc instanceof ChequeAccount chq) {
                ps.setDouble(7, 0.0);
                ps.setString(8, chq.getCompany());
                ps.setString(9, chq.getCompanyAddress());
            }
            ps.executeUpdate();
        }
    }

    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = dbManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public List<Account> findByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        List<Account> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = dbManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    public void delete(String accountNumber) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = dbManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        String accNo = rs.getString("account_number");
        String custId = rs.getString("customer_id");
        double bal = rs.getDouble("balance");
        String branch = rs.getString("branch");
        LocalDate opened = LocalDate.parse(rs.getString("date_opened"));

        return switch (type) {
            case "SAVINGS" -> new SavingsAccount(accNo, bal, branch, opened, custId);
            case "INVESTMENT" -> {
                InvestmentAccount inv = new InvestmentAccount(accNo, InvestmentAccount.MINIMUM_INITIAL_DEPOSIT, branch, opened, custId);
                inv.setBalance(bal);
                yield inv;
            }
            case "CHEQUE" -> new ChequeAccount(accNo, bal, branch, opened, custId,
                    rs.getString("company"), rs.getString("company_address"));
            default -> throw new SQLException("Unknown account type: " + type);
        };
    }
}
