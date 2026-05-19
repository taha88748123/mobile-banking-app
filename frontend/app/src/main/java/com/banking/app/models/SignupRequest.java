package com.banking.app.models;

public class SignupRequest {
    public String fullName;
    public String email;
    public String password;
    public String phone;

    public SignupRequest(String fullName, String email, String password, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
