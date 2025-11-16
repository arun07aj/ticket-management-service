package com.arunaj.tms.controller;

import com.arunaj.tms.model.AccountRole;
import com.arunaj.tms.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"https://arunaj.com", "http://localhost:3000"})
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/role")
    public ResponseEntity<?> getRole() {
        try {
            AccountRole role = accountService.getCurrentLoggedInUserRole();
            if(role != null) {
                return ResponseEntity.status(HttpStatus.OK).body(role.toString());
            }
            throw new Exception("user role returned null");
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error identifying user role");
        }
    }
}
