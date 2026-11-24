package model;

import java.time.LocalDate;

public class SavingsAccount extends Account {
    private final double interestRate = 0.0005;

    public SavingsAccount(String accountNumber, double initialDeposit, String branch, LocalDate dateOpened, String customerId) {
        super(accountNumber, initialDeposit, branch, dateOpened, customerId);
    }

    @Override
    public void withdraw(double amount) {
        throw new UnsupportedOperationException("Withdrawals are not permitted on a Savings Account.");
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
        return "SAVINGS";
    }

    public double getInterestRate() { return interestRate; }
}
