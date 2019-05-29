package com.codessay.money.transfer.repository.impl;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;
import com.codessay.money.transfer.repository.AccountRepository;
import com.codessay.money.transfer.repository.binder.BigDecimalBinder;
import io.javalin.BadRequestResponse;
import io.javalin.NotFoundResponse;
import jetbrains.exodus.entitystore.*;

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
    public void add(Account account) {
        if (account.getId() != null) {
            throw new BadRequestResponse(String.format("Can't add account with existing id: '%s'",
                    account.getId()));
        }

        account.setId(store.computeInTransaction(txn -> {
            store.registerCustomPropertyType(txn, BigDecimal.class, BigDecimalBinder.BINDER);

            var entity = txn.newEntity(ENTITY_TYPE);
            var id = entity.getId();

            entity.setProperty(Account.PROPERTY_CURRENCY, account.getCurrency());
            entity.setProperty(Account.PROPERTY_BALANCE, account.getBalance());

            return id.toString();
        }));
    }

    @Override
    public Account getAll(String id) {
        return store.computeInReadonlyTransaction(txn -> toAccount(getEntity(txn, id)));
    }

    @Override
    public List<Account> getAll() {
        return store.computeInReadonlyTransaction(txn -> {
            store.registerCustomPropertyType(txn, BigDecimal.class, BigDecimalBinder.BINDER);

            List<Account> accountList = new ArrayList<>();
            EntityIterable allAccounts = txn.getAll(ENTITY_TYPE);

            allAccounts.forEach(entity -> accountList.add(toAccount(entity)));

            return accountList;
        });
    }

    @Override
    public void update(Account account) {
        store.executeInTransaction(txn -> {
            var entity = getEntity(txn, account.getId());

            entity.setProperty(Account.PROPERTY_CURRENCY, account.getCurrency());
            entity.setProperty(Account.PROPERTY_BALANCE, account.getBalance());

            txn.saveEntity(entity);
        });
    }

    @Override
    public void delete(String id) {
        store.executeInTransaction(txn -> getEntity(txn, id).delete());
    }

    @Override
    public void transfer(TransferParams params) {
        store.executeInTransaction(txn -> {
            store.registerCustomPropertyType(txn, BigDecimal.class, BigDecimalBinder.BINDER);

            var fromEntity = getEntity(txn, params.getFrom());
            var toEntity = getEntity(txn, params.getTo());

            var fromAccount = toAccount(fromEntity);
            var toAccount = toAccount(toEntity);

            if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
                throw new BadRequestResponse("Different currencies");
            }

            if (fromAccount.getBalance().compareTo(params.getAmount()) < 0) {
                throw new BadRequestResponse("Insufficient funds");
            }

            fromEntity.setProperty(Account.PROPERTY_BALANCE, fromAccount.getBalance().subtract(params.getAmount()));
            toEntity.setProperty(Account.PROPERTY_BALANCE, toAccount.getBalance().add(params.getAmount()));
        });
    }

    private static Account toAccount(Entity entity) {
        return new Account()
                .setId(entity.getId().toString())
                .setCurrency((String) entity.getProperty(Account.PROPERTY_CURRENCY))
                .setBalance((BigDecimal) entity.getProperty(Account.PROPERTY_BALANCE));
    }

    private Entity getEntity(StoreTransaction txn, String id) {
        try {
            txn.getEntity(txn.toEntityId(id));
            return txn.getEntity(txn.toEntityId(id));
        } catch (EntityRemovedInDatabaseException e) {
            throw new NotFoundResponse(String.format("Account with id '%s' does not exist", id));
        }
    }
}