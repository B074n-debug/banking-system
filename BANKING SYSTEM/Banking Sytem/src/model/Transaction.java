package model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, String accountNumber, String transactionType,
                       double amount, double balanceAfter, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s | %-10s | BWP %8.2f | New Balance: BWP %8.2f",
                timestamp.toLocalDate(), transactionType, amount, balanceAfter);
    }
}
