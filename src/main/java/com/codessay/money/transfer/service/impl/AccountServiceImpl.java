package com.codessay.money.transfer.service.impl;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;
import com.codessay.money.transfer.repository.AccountRepository;
import com.codessay.money.transfer.service.AccountService;

import java.util.List;

public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account add(Account newAccount) {
        return accountRepository.add(newAccount);
    }

    @Override
    public void transfer(TransferParams params) {

    }

    @Override
    public List<Account> get() {
        return accountRepository.get();
    }

    @Override
    public Account get(String id) {
        return accountRepository.get(id);
    }
}