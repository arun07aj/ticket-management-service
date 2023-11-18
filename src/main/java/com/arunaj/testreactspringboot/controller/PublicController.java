package com.arunaj.testreactspringboot.controller;

import com.arunaj.testreactspringboot.dto.UserLoginDTO;
import com.arunaj.testreactspringboot.model.Account;
import com.arunaj.testreactspringboot.service.AccountService;
import com.arunaj.testreactspringboot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) throws Exception {
        authenticate(loginDTO.getUsername(), loginDTO.getPassword());
        final Optional<Account> account = accountService.loadAccountByUsername(loginDTO.getUsername());
        if(account.isPresent()) {
            final String token = jwtUtil.generateToken(account.get());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not exists");
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
