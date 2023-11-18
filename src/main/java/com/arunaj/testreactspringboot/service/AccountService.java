package com.arunaj.testreactspringboot.service;

import com.arunaj.testreactspringboot.model.Account;
import com.arunaj.testreactspringboot.repository.AccountRepository;
import com.arunaj.testreactspringboot.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
}