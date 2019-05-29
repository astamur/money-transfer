package com.codessay.money.transfer.service;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;

import java.util.List;

public interface AccountService {
    Account add(Account newAccount);

    void transfer(TransferParams params);

    List<Account> get();

    Account get(String id);
}