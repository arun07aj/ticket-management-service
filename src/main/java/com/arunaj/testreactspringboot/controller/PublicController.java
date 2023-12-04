package com.arunaj.testreactspringboot.controller;

import com.arunaj.testreactspringboot.dto.UserLoginDTO;
import com.arunaj.testreactspringboot.dto.UserSignupDTO;
import com.arunaj.testreactspringboot.exception.AccountAlreadyExistsException;
import com.arunaj.testreactspringboot.exception.BadRequestException;
import com.arunaj.testreactspringboot.model.Account;
import com.arunaj.testreactspringboot.service.AccountService;
import com.arunaj.testreactspringboot.util.JwtUtil;
import com.arunaj.testreactspringboot.util.LoggerUtil;
import org.slf4j.Logger;
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

import javax.management.BadAttributeValueExpException;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private static final Logger logger = LoggerUtil.getLogger(PublicController.class);
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
            logger.info("login successful");
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupDTO signupDTO) throws Exception {
        try{
            return accountService.createAccount(signupDTO);
        }
        catch(BadRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch(Exception e){
            logger.error("Failed to create account: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account at the moment");
        }
    }

}
