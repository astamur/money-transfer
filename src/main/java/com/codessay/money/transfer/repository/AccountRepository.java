package com.codessay.money.transfer.repository;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;

import java.util.List;

public interface AccountRepository {
    void add(Account account);

    Account getAll(String id);

    List<Account> getAll();

    void update(Account update);

    void delete(String id);

    void transfer(TransferParams params);
}