import controller.AccountController;
import controller.AuthenticationController;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.DatabaseManager;
import dao.TransactionDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            DatabaseManager dbManager = new DatabaseManager("jdbc:sqlite:banking.db");
            dbManager.initializeSchema();

            CustomerDAO customerDAO = new CustomerDAO(dbManager);
            AccountDAO accountDAO = new AccountDAO(dbManager);
            TransactionDAO transactionDAO = new TransactionDAO(dbManager);

            AuthenticationController authController = new AuthenticationController(customerDAO);
            AccountController accountController = new AccountController(accountDAO, transactionDAO);

            new LoginView(stage, authController, accountController).show();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("BAC Banking System starting...");
        launch(args);
    }
}
