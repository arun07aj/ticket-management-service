package com.arunaj.tms.dto;

public class UserLoginDTO {
    private String username;
    private String password;
    private String captchaResponse;

    public UserLoginDTO() {
    }

    public UserLoginDTO(String username, String password, String captchaResponse) {
        this.username = username;
        this.password = password;
        this.captchaResponse = captchaResponse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaResponse() { return captchaResponse; }

    public void setCaptchaResponse(String captchaResponse) { this.captchaResponse = captchaResponse; }
}
