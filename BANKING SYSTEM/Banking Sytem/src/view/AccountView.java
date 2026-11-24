package view;

import controller.AccountController;
import controller.AuthenticationController;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Account;
import model.Customer;
import model.Transaction;

public class AccountView {
    private final Stage stage;
    private final Customer customer;
    private final AuthenticationController authController;
    private final AccountController accountController;

    private final ListView<String> accountListView = new ListView<>();
    private final ListView<String> historyListView = new ListView<>();
    private final Label statusLabel = new Label("Ready.");

    public AccountView(Stage stage, Customer customer, AuthenticationController authController, AccountController accountController) {
        this.stage = stage;
        this.customer = customer;
        this.authController = authController;
        this.accountController = accountController;
    }

    public void show() {
        Label welcome = new Label("Welcome, " + customer.getFirstName() + " " + customer.getLastName());
        welcome.getStyleClass().add("title-label");

        Label accountsHeading = new Label("Your Accounts:");
        accountsHeading.getStyleClass().add("section-label");

        Label txHeading = new Label("Transactions:");
        txHeading.getStyleClass().add("section-label");

        Label openHeading = new Label("Open New Account:");
        openHeading.getStyleClass().add("section-label");

        Label historyHeading = new Label("Transaction History (Selected Account):");
        historyHeading.getStyleClass().add("section-label");

        // Account Selector & Transaction Controls (Part A Sec 3.2, FR4, FR5, FR10, FR12)
        ComboBox<String> accountSelector = new ComboBox<>();
        accountSelector.setPromptText("Select Account");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");

        Button depositBtn = new Button("Deposit");
        Button withdrawBtn = new Button("Withdraw");
        Button interestBtn = new Button("Apply Monthly Interest");
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("secondary-button");

        // Open New Account Controls (Part A FR2, FR3, FR6, FR8)
        ComboBox<String> newTypeBox = new ComboBox<>();
        newTypeBox.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        newTypeBox.setPromptText("Account Type");
        TextField initDepositField = new TextField();
        initDepositField.setPromptText("Initial Deposit (BWP)");
        TextField companyField = new TextField();
        companyField.setPromptText("Employer Name (Cheque)");
        TextField companyAddrField = new TextField();
        companyAddrField.setPromptText("Employer Address (Cheque)");
        Button openAccBtn = new Button("Open Account");

        Runnable refreshUI = () -> {
            String previousSelection = accountSelector.getValue();
            accountListView.getItems().clear();
            accountSelector.getItems().clear();

            for (Account acc : accountController.getCustomerAccounts(customer.getCustomerId())) {
                accountListView.getItems().add(acc.getAccountDetails());
                accountSelector.getItems().add(acc.getAccountNumber());
            }

            if (!accountSelector.getItems().isEmpty()) {
                if (previousSelection != null && accountSelector.getItems().contains(previousSelection)) {
                    accountSelector.getSelectionModel().select(previousSelection);
                } else {
                    accountSelector.getSelectionModel().selectFirst();
                }
                loadHistory(accountSelector.getValue());
            } else {
                historyListView.getItems().clear();
            }
        };

        accountSelector.setOnAction(e -> {
            String selected = accountSelector.getValue();
            if (selected != null && !selected.isBlank()) {
                loadHistory(selected);
            }
        });

        depositBtn.setOnAction(e -> {
            try {
                String selectedAccount = requireSelectedAccount(accountSelector);
                double amount = parsePositiveAmount(amountField.getText(), "Amount");
                String msg = accountController.deposit(selectedAccount, amount);
                setStatus(msg, false);
                amountField.clear();
                refreshUI.run();
            } catch (IllegalArgumentException ex) {
                setStatus("Error: " + ex.getMessage(), true);
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage(), true);
            }
        });

        withdrawBtn.setOnAction(e -> {
            try {
                String selectedAccount = requireSelectedAccount(accountSelector);
                double amount = parsePositiveAmount(amountField.getText(), "Amount");
                String msg = accountController.withdraw(selectedAccount, amount);
                setStatus(msg, false);
                amountField.clear();
                refreshUI.run();
            } catch (IllegalArgumentException ex) {
                setStatus("Error: " + ex.getMessage(), true);
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage(), true);
            }
        });

        interestBtn.setOnAction(e -> {
            try {
                String msg = accountController.processMonthlyInterest();
                setStatus(msg, false);
                refreshUI.run();
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage(), true);
            }
        });

        openAccBtn.setOnAction(e -> {
            try {
                String accountType = newTypeBox.getValue();
                if (accountType == null || accountType.isBlank()) {
                    setStatus("Error: Please select an account type.", true);
                    return;
                }

                double initAmt = 0.0;
                String rawInit = initDepositField.getText().trim();
                if (!rawInit.isEmpty()) {
                    initAmt = parseNonNegativeAmount(rawInit, "Initial deposit");
                }

                String msg = accountController.openAccount(
                        customer.getCustomerId(),
                        accountType,
                        initAmt,
                        "Gaborone Main",
                        companyField.getText().trim(),
                        companyAddrField.getText().trim()
                );
                setStatus(msg, false);
                initDepositField.clear();
                companyField.clear();
                companyAddrField.clear();
                newTypeBox.getSelectionModel().clearSelection();
                refreshUI.run();
            } catch (IllegalArgumentException ex) {
                setStatus("Error: " + ex.getMessage(), true);
            } catch (Exception ex) {
                setStatus("Error: " + ex.getMessage(), true);
            }
        });

        logoutBtn.setOnAction(e -> new LoginView(stage, authController, accountController).show());

        HBox txControls = new HBox(8, accountSelector, amountField, depositBtn, withdrawBtn, interestBtn);
        HBox openControls = new HBox(8, newTypeBox, initDepositField, companyField, companyAddrField, openAccBtn);

        VBox root = new VBox(10,
                new HBox(20, welcome, logoutBtn),
                accountsHeading, accountListView,
                txHeading, txControls,
                openHeading, openControls,
                historyHeading, historyListView,
                statusLabel
        );
        root.setPadding(new Insets(12));
        refreshUI.run();

        Scene scene = new Scene(root, 780, 560);
        applyStylesheet(scene);

        stage.setTitle("Banking System - Customer Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.getStyleClass().removeAll("error-label", "success-label");
        statusLabel.getStyleClass().add(isError ? "error-label" : "success-label");
        statusLabel.setText(message);
    }

    private void applyStylesheet(Scene scene) {
        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            return;
        }

        for (Path candidate : List.of(
                Path.of("src/style.css"),
                Path.of("style.css"),
                Path.of("src/main/resources/style.css"),
                Path.of("resources/style.css")
        )) {
            if (Files.exists(candidate)) {
                scene.getStylesheets().add(candidate.toUri().toString());
                return;
            }
        }
    }

    private String requireSelectedAccount(ComboBox<String> accountSelector) {
        String selected = accountSelector.getValue();
        if (selected == null || selected.isBlank()) {
            throw new IllegalArgumentException("Please select an account first.");
        }
        return selected;
    }

    private double parsePositiveAmount(String rawText, String fieldName) {
        String trimmed = rawText == null ? "" : rawText.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            double value = Double.parseDouble(trimmed);
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " must be greater than 0.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private double parseNonNegativeAmount(String rawText, String fieldName) {
        try {
            double value = Double.parseDouble(rawText.trim());
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private void loadHistory(String accountNumber) {
        historyListView.getItems().clear();
        if (accountNumber == null || accountNumber.isBlank()) {
            return;
        }
        for (Transaction tx : accountController.getTransactionHistory(accountNumber)) {
            historyListView.getItems().add(tx.toString());
        }
    }
}