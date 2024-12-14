package com.example.demo.services;

import com.example.demo.models.Account;

import java.util.Optional;

public interface IAccount {
    Account register(Account account);
    Optional<Account> findByUsername(String username);

    Account saveAccount(Account account);
}
