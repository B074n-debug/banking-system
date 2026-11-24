package view;

import controller.AccountController;
import controller.AuthenticationController;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;

public class LoginView {
    private final Stage stage;
    private final AuthenticationController authController;
    private final AccountController accountController;

    public LoginView(Stage stage, AuthenticationController authController, AccountController accountController) {
        this.stage = stage;
        this.authController = authController;
        this.accountController = accountController;
    }

    public void show() {
        TabPane tabPane = new TabPane();

        // --- TAB 1: CUSTOMER LOGIN (Part A Sec 3.1 & FR9) ---
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(15));
        loginGrid.setVgap(10);
        loginGrid.setHgap(10);

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginBtn = new Button("Login");
        Label loginStatus = new Label();

        loginGrid.add(new Label("Username:"), 0, 0);
        loginGrid.add(userField, 1, 0);
        loginGrid.add(new Label("Password:"), 0, 1);
        loginGrid.add(passField, 1, 1);
        loginGrid.add(loginBtn, 1, 2);
        loginGrid.add(loginStatus, 0, 3, 2, 1);

        loginBtn.setOnAction(e -> {
            try {
                Customer customer = authController.validateCredentials(userField.getText(), passField.getText());
                if (customer != null) {
                    loginStatus.getStyleClass().removeAll("error-label", "success-label");
                    loginStatus.getStyleClass().add("success-label");
                    loginStatus.setText("Login successful!");
                    new AccountView(stage, customer, authController, accountController).show();
                } else {
                    loginStatus.getStyleClass().removeAll("error-label", "success-label");
                    loginStatus.getStyleClass().add("error-label");
                    loginStatus.setText("Error: Invalid username or password.");
                }
            } catch (Exception ex) {
                loginStatus.getStyleClass().removeAll("error-label", "success-label");
                loginStatus.getStyleClass().add("error-label");
                loginStatus.setText("Error: " + ex.getMessage());
            }
        });

        // --- TAB 2: REGISTER CUSTOMER (Part A FR1) ---
        GridPane regGrid = new GridPane();
        regGrid.setPadding(new Insets(15));
        regGrid.setVgap(8);
        regGrid.setHgap(10);

        TextField fName = new TextField();
        TextField lName = new TextField();
        TextField addr = new TextField();
        TextField phone = new TextField();
        TextField regUser = new TextField();
        PasswordField regPass = new PasswordField();
        Button regBtn = new Button("Register");
        Label regStatus = new Label();

        regGrid.addRow(0, new Label("First Name:"), fName);
        regGrid.addRow(1, new Label("Last Name:"), lName);
        regGrid.addRow(2, new Label("Address:"), addr);
        regGrid.addRow(3, new Label("Phone:"), phone);
        regGrid.addRow(4, new Label("Username:"), regUser);
        regGrid.addRow(5, new Label("Password:"), regPass);
        regGrid.add(regBtn, 1, 6);
        regGrid.add(regStatus, 0, 7, 2, 1);

        regBtn.setOnAction(e -> {
            try {
                authController.registerCustomer(
                        fName.getText(), lName.getText(), addr.getText(),
                        phone.getText(), regUser.getText(), regPass.getText()
                );
                regStatus.getStyleClass().removeAll("error-label", "success-label");
                regStatus.getStyleClass().add("success-label");
                regStatus.setText("Registration successful! Please log in.");
            } catch (Exception ex) {
                regStatus.getStyleClass().removeAll("error-label", "success-label");
                regStatus.getStyleClass().add("error-label");
                regStatus.setText("Error: " + ex.getMessage());
            }
        });

        Tab loginTab = new Tab("Customer Login", loginGrid);
        loginTab.setClosable(false);
        Tab regTab = new Tab("Register", regGrid);
        regTab.setClosable(false);
        tabPane.getTabs().addAll(loginTab, regTab);

        Label titleLabel = new Label("BAC Banking System");
        titleLabel.getStyleClass().add("title-label");

        VBox root = new VBox(10, titleLabel, tabPane);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 420, 360);
        applyStylesheet(scene);

        stage.setTitle("Banking System - Login");
        stage.setScene(scene);
        stage.show();
    }

    private void applyStylesheet(Scene scene) {
        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            return;
        }

        for (Path candidate : java.util.List.of(
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
}