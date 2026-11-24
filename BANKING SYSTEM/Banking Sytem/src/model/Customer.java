package model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String username;
    private String password;
    private List<Account> accounts;

    public Customer(String customerId, String firstName, String lastName, String address,
                    String phone, String username, String password) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public List<Account> viewAccountDetails() {
        return accounts;
    }

    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<Account> getAccounts() { return accounts; }
}
