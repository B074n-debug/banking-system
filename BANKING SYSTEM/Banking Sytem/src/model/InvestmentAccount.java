package model;

import java.time.LocalDate;

public class InvestmentAccount extends Account {
    private final double interestRate = 0.05;
    public static final double MINIMUM_INITIAL_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, double initialDeposit, String branch, LocalDate dateOpened, String customerId) {
        super(accountNumber, 0.0, branch, dateOpened, customerId);
        if (initialDeposit < MINIMUM_INITIAL_DEPOSIT) {
            throw new IllegalArgumentException("Investment Account requires a minimum initial deposit of BWP 500.00.");
        }
        this.balance = initialDeposit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new IllegalStateException("Insufficient funds in Investment Account.");
        }
        this.balance -= amount;
    }

    @Override
    public double calculateInterest() {
        return this.balance * interestRate;
    }

    @Override
    public void payInterest() {
        this.balance += calculateInterest();
    }

    @Override
    public String getAccountType() {
        return "INVESTMENT";
    }

    public double getInterestRate() { return interestRate; }
}
