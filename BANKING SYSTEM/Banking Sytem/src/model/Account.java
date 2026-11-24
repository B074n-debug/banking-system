package model;

import java.time.LocalDate;

public abstract class Account implements Interest {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected LocalDate dateOpened;
    protected String customerId;

    public Account(String accountNumber, double balance, String branch, LocalDate dateOpened, String customerId) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.dateOpened = dateOpened;
        this.customerId = customerId;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than BWP 0.00.");
        }
        this.balance += amount;
    }

    public abstract void withdraw(double amount);

    public String getAccountDetails() {
        return String.format("[%s] %s | Branch: %s | Opened: %s | Balance: BWP %.2f",
                getAccountType(), accountNumber, branch, dateOpened, balance);
    }

    public abstract String getAccountType();

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getBranch() { return branch; }
    public LocalDate getDateOpened() { return dateOpened; }
    public String getCustomerId() { return customerId; }
}
