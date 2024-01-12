package com.arunaj.tms.dto;

import com.arunaj.tms.model.AccountRole;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class UserSignupDTO {
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    public UserSignupDTO(String username, String email, String password, AccountRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }
}
