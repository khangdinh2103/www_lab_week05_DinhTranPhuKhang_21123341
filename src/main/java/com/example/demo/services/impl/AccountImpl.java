package com.example.demo.services.impl;

import com.example.demo.models.Account;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.services.IAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountImpl implements IAccount {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account register(Account account) {
        account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword())); // Mã hóa mật khẩu
        return accountRepository.save(account); // Lưu tài khoản vào database
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }
}

