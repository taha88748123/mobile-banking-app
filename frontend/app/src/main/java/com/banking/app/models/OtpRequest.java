package com.banking.app.models;

public class OtpRequest {
    public String email;
    public String otp;

    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
