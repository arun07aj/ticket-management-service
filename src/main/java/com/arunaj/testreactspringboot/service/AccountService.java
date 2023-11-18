package com.arunaj.testreactspringboot.service;

import com.arunaj.testreactspringboot.model.Account;
import com.arunaj.testreactspringboot.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> loadAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findAccountByUsername(username);

        return accountOptional.map(account ->
                new User(account.getUsername(), account.getPassword(), getAuthorities(account))
        ).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        // extract and convert roles from your Account entity
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
    }
}