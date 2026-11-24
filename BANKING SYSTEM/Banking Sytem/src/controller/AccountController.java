package controller;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;
import model.ChequeAccount;
import model.InvestmentAccount;
import model.SavingsAccount;
import model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountController {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AccountController(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    // Part A FR2, FR3, FR6, FR8
    public String openAccount(String customerId, String type, double initialDeposit,
                              String branch, String company, String companyAddress) throws Exception {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Please select an account type.");
        }
        String accNo = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate today = LocalDate.now();

        Account account = switch (type.toUpperCase()) {
            case "SAVINGS" -> new SavingsAccount(accNo, initialDeposit, branch, today, customerId);
            case "INVESTMENT" -> new InvestmentAccount(accNo, initialDeposit, branch, today, customerId);
            case "CHEQUE" -> new ChequeAccount(accNo, initialDeposit, branch, today, customerId, company, companyAddress);
            default -> throw new IllegalArgumentException("Unsupported account type: " + type);
        };

        accountDAO.save(account);
        if (initialDeposit > 0) {
            recordTransaction(accNo, "DEPOSIT", initialDeposit, account.getBalance());
        }
        return "Opened " + account.getAccountType() + " account (" + accNo + ").";
    }

    // Part A Sec 3.2 Sequence Diagram (Deposit Funds)
    public String deposit(String accountNumber, double amount) throws Exception {
        Account account = validateAndFindAccount(accountNumber);
        account.deposit(amount);
        accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
        recordTransaction(account.getAccountNumber(), "DEPOSIT", amount, account.getBalance());
        return String.format("Deposited BWP %.2f into %s. New balance: BWP %.2f",
                amount, accountNumber, account.getBalance());
    }

    // Part A FR5 & Use Case (Withdraw Funds)
    public String withdraw(String accountNumber, double amount) throws Exception {
        Account account = validateAndFindAccount(accountNumber);
        account.withdraw(amount);
        accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
        recordTransaction(account.getAccountNumber(), "WITHDRAWAL", amount, account.getBalance());
        return String.format("Withdrew BWP %.2f from %s. New balance: BWP %.2f",
                amount, accountNumber, account.getBalance());
    }

    // Part A Sec 3.3 State Diagram (PayInterest loop across eligible accounts)
    public String processMonthlyInterest() throws Exception {
        List<Account> allAccounts = accountDAO.findAll();
        int creditedCount = 0;
        for (Account account : allAccounts) {
            double interest = account.calculateInterest();
            if (interest > 0) {
                account.payInterest();
                accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
                recordTransaction(account.getAccountNumber(), "INTEREST", interest, account.getBalance());
                creditedCount++;
            }
        }
        return "Monthly interest processed for " + creditedCount + " eligible account(s).";
    }

    public List<Account> getCustomerAccounts(String customerId) {
        try {
            return accountDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        try {
            return transactionDAO.findByAccountNumber(accountNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Account validateAndFindAccount(String accountNumber) throws Exception {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Please select an account first.");
        }
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    private void recordTransaction(String accountNumber, String type, double amount, double balanceAfter) throws Exception {
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaction tx = new Transaction(txId, accountNumber, type, amount, balanceAfter, LocalDateTime.now());
        transactionDAO.save(tx);
    }
}
