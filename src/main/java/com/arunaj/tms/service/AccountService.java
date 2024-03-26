package com.arunaj.tms.service;

import com.arunaj.tms.dto.UserSignupDTO;
import com.arunaj.tms.exception.AccountAlreadyExistsException;
import com.arunaj.tms.exception.BadRequestException;
import com.arunaj.tms.model.Account;
import com.arunaj.tms.model.AccountRole;
import com.arunaj.tms.repository.AccountRepository;
import com.arunaj.tms.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    private static final Logger logger = LoggerUtil.getLogger(AccountService.class);
    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> loadAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findAccountByUsername(username);

        return accountOptional.map(account ->
                new User(account.getUsername(), account.getPassword(), account.getAuthorities())
        ).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public Optional<Account> getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if(accountRepository.findAccountByUsername(authentication.getName()).isPresent()) {
                return accountRepository.findAccountByUsername(authentication.getName());
            }
        }
        logger.warn("current logged-in user could not be identified");
        return Optional.empty();
    }

    public AccountRole getCurrentLoggedInUserRole() {
        Optional<Account> currentUser = getCurrentLoggedInUser();
        if(currentUser.isPresent()) {
            return currentUser.get().getRole();
        }
        logger.warn("role of current logged-in user could not be identified");
        return null;
    }

    public ResponseEntity<?> createAccount(UserSignupDTO signupDTO){
        if (signupDTO.getUsername() == null || signupDTO.getUsername().isBlank() ||
                signupDTO.getEmail() == null || signupDTO.getEmail().isBlank() ||
                signupDTO.getPassword() == null || signupDTO.getPassword().isBlank()) {
            throw new BadRequestException("One or more mandatory field(s) are missing");
        }
        else if(!isValidEmail(signupDTO.getEmail())) {
            throw new BadRequestException("Email provided is not valid");
        }
        else{
            if (!accountRepository.findAccountByEmail(signupDTO.getEmail()).isPresent()) {
                if (!accountRepository.findAccountByUsername(signupDTO.getUsername()).isPresent()) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    Account account = new Account(signupDTO.getUsername(), signupDTO.getEmail(), passwordEncoder.encode(signupDTO.getPassword()));
                    account.setRole(AccountRole.USER);
                    account.setActive(true);
                    accountRepository.save(account);

                    long createdID = accountRepository.findAccountByUsername(signupDTO.getUsername())
                            .orElseThrow(() -> new IllegalStateException("Account not found after saving"))
                            .getId();

                    return ResponseEntity.status(HttpStatus.CREATED).body("User created with ID: #" + createdID);
                }
                throw new AccountAlreadyExistsException("user already exists with the username: " + signupDTO.getUsername());
            }
            throw new AccountAlreadyExistsException("user already exists with the email: " + signupDTO.getEmail());
        }

    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return false; // Email format is not valid
        }

        // Extracting domain from the email
        String[] emailParts = email.split("@");
        String domain = emailParts[1].toLowerCase();

        // List of supported domains
        String[] supportedDomains = {"arunaj.co", "gmail.com", "outlook.com"};

        // Check if the domain is in the supported list
        for (String supportedDomain : supportedDomains) {
            if (domain.equals(supportedDomain)) {
                return true;
            }
        }

        throw new BadRequestException("Email domain is not supported, please provide either Gmail / Outlook");
    }

}