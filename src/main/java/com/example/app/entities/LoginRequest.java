package com.example.app.entities;

public class LoginRequest {
    private String username;
    private String password;

    // Default constructor
    public LoginRequest() {
        // TODO Auto-generated constructor stub
    }

    // Parameterized constructor
    public LoginRequest(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
