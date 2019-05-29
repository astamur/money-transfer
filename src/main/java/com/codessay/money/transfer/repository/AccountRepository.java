package com.codessay.money.transfer.repository;

import com.codessay.money.transfer.model.Account;

import java.util.List;

public interface AccountRepository {
    Account add(Account account);

    Account get(String id);

    List<Account> get();

    Account update();

    void delete(String id);
}