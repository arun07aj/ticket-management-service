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
        if (!captchaUtil.isCaptchaValid(loginDTO.getCaptchaResponse())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reCAPTCHA response");
        }

        try {
            authenticate(loginDTO.getUsername(), loginDTO.getPassword());
            Account account = accountService.loadAccountByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> {
                        logger.error("Authentication succeeded but account not found: {}", loginDTO.getUsername());
                        return new IllegalStateException("Account not found after authentication");
                    });

            if (account == null) {
                logger.error("Authentication succeeded but account not found: {}", loginDTO.getUsername());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
            }

            final String token = jwtUtil.generateToken(account);
            logger.info("Login successful for user: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }
        catch (DisabledException e) {
            logger.warn("Disabled account login attempt: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is disabled");
        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for user: {}", loginDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        catch (Exception e){
            logger.error("Unexpected login error for user: {}", loginDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupDTO signupDTO) {
        // Enabled CAPTCHA for signup endpoint
        if (!captchaUtil.isCaptchaValid(signupDTO.getCaptchaResponse())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid reCAPTCHA response");
        }

        try{
            ResponseEntity<?> response = accountService.createAccount(signupDTO);
            logger.info("Account created successfully for username: {}", signupDTO.getUsername());
            return response;
        }
        catch(BadRequestException e){
            logger.warn("Invalid signup request for username: {} - {}", signupDTO.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch(AccountAlreadyExistsException e) {
            logger.warn("Account with same username exists: {}", signupDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        catch(Exception e){
            logger.error("Unexpected signup error for username: {}", signupDTO.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Signup failed");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> testHealth() {
        return ResponseEntity.status(HttpStatus.OK).body("TMS says HI..!");
    }

}
