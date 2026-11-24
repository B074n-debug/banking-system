package controller;

import dao.CustomerDAO;
import model.Customer;
import java.util.UUID;

public class AuthenticationController {
    private final CustomerDAO customerDAO;

    public AuthenticationController(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    // Part A Sec 3.1: ValidateCredentials() -> CheckCustomer() -> CustomerFound()
    public Customer validateCredentials(String username, String password) throws Exception {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required.");
        }
        Customer customer = customerDAO.findByUsername(username.trim());
        if (customer != null && customer.login(username.trim(), password)) {
            return customer;
        }
        return null;
    }

    // Part A FR1 & NFR4 (Data Integrity)
    public Customer registerCustomer(String firstName, String lastName, String address,
                                     String phone, String username, String password) throws Exception {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("All required personal fields must be completed.");
        }
        if (customerDAO.findByUsername(username.trim()) != null) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        String customerId = "CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer customer = new Customer(customerId, firstName.trim(), lastName.trim(),
                address.trim(), phone.trim(), username.trim(), password);
        customerDAO.save(customer);
        return customer;
    }
}
