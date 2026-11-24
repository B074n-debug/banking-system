package model;

import java.time.LocalDate;

public class ChequeAccount extends Account {
    private final double interestRate = 0.0;
    private String company;
    private String companyAddress;

    public ChequeAccount(String accountNumber, double initialDeposit, String branch, LocalDate dateOpened,
                         String customerId, String company, String companyAddress) {
        super(accountNumber, initialDeposit, branch, dateOpened, customerId);
        if (company == null || company.isBlank() || companyAddress == null || companyAddress.isBlank()) {
            throw new IllegalArgumentException("Employer company name and company address are required to open a Cheque Account.");
        }
        this.company = company;
        this.companyAddress = companyAddress;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new IllegalStateException("Insufficient funds in Cheque Account.");
        }
        this.balance -= amount;
    }

    @Override
    public double calculateInterest() {
        return 0.0;
    }

    @Override
    public void payInterest() {
        // Cheque accounts do not earn monthly interest
    }

    @Override
    public String getAccountType() {
        return "CHEQUE";
    }

    public String getCompany() { return company; }
    public String getCompanyAddress() { return companyAddress; }
}
