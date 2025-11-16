package com.arunaj.tms.controller;

import com.arunaj.tms.dto.UserLoginDTO;
import com.arunaj.tms.dto.UserSignupDTO;
import com.arunaj.tms.exception.AccountAlreadyExistsException;
import com.arunaj.tms.exception.BadRequestException;
import com.arunaj.tms.model.Account;
import com.arunaj.tms.service.AccountService;
import com.arunaj.tms.util.CaptchaUtil;
import com.arunaj.tms.util.JwtUtil;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@CrossOrigin(origins = {"https://arunaj.com", "http://localhost:3000"})
@RequestMapping("/api/public")
public class PublicController {
    private static final Logger logger = LoggerUtil.getLogger(PublicController.class);
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) throws Exception {
        // Enabled CAPTCHA for login endpoint
        if (captchaUtil.captchaHelper(loginDTO.getCaptchaResponse())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reCAPTCHA response");
        }

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
    public ResponseEntity<?> signup(@RequestBody UserSignupDTO signupDTO) {
        try{
            // Enabled CAPTCHA for signup endpoint
            if (captchaUtil.captchaHelper(signupDTO.getCaptchaResponse())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reCAPTCHA response");
            }
            return accountService.createAccount(signupDTO);
        }
        catch(BadRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(AccountAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch(Exception e){
            logger.error("Failed to create account: {}", String.valueOf(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating account at the moment");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> testHealth() {
        return ResponseEntity.status(HttpStatus.OK).body("TMS says HI..!");
    }

}
