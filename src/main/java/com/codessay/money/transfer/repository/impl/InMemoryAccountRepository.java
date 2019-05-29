package com.codessay.money.transfer.repository.impl;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.repository.AccountRepository;
import com.codessay.money.transfer.repository.binder.BigDecimalBinder;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.PersistentEntityStore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAccountRepository implements AccountRepository {
    private static final String ENTITY_TYPE = "Account";

    private final PersistentEntityStore store;

    public InMemoryAccountRepository(PersistentEntityStore store) {
        this.store = store;
    }

    @Override
    public Account add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Can't save account. Value is null.");
        }

        if (account.getId() != null) {
            throw new IllegalStateException("Can't save with existing id");
        }

        account.setId(store.computeInTransaction(txn -> {
            store.registerCustomPropertyType(txn, BigDecimal.class, BigDecimalBinder.BINDER);

            var entity = txn.newEntity(ENTITY_TYPE);
            var id = entity.getId();

            entity.setProperty("currency", account.getCurrency());
            entity.setProperty("balance", account.getBalance());

            return id.toString();
        }));

        return account;
    }

    @Override
    public Account get(String id) {
        return null;
    }

    @Override
    public List<Account> get() {
        return store.computeInReadonlyTransaction(txn -> {
            store.registerCustomPropertyType(txn, BigDecimal.class, BigDecimalBinder.BINDER);

            List<Account> accountList = new ArrayList<>();
            EntityIterable allAccounts = txn.getAll(ENTITY_TYPE);

            allAccounts.forEach(entity -> accountList.add(toAccount(entity)));

            return accountList;
        });
    }

    @Override
    public Account update() {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    private static Account toAccount(Entity entity) {
        return new Account()
                .setId(entity.getId().toString())
                .setCurrency((String) entity.getProperty("currency"))
                .setBalance((BigDecimal) entity.getProperty("balance"));
    }
}