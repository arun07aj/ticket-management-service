package com.arunaj.testreactspringboot.repository;

import com.arunaj.testreactspringboot.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findAccountByUsername(String username);
    Optional<Account> findAccountByEmail(String email);
}
