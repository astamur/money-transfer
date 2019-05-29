package com.codessay.money.transfer.controller;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;
import com.codessay.money.transfer.repository.AccountRepository;
import com.codessay.money.transfer.util.HttpUtils;
import io.javalin.BadRequestResponse;
import io.javalin.Context;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

public class AccountController {
    private static final String ID_PARAM = "id";

    private AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void addAccount(@NotNull Context context) {
        var account = Account.validate(context.bodyValidator(Account.class));

        accountRepository.add(account);

        context.status(HttpStatus.CREATED_201)
                .header(HttpHeader.LOCATION.name(), HttpUtils.formatAccountLocation(account.getId()));
    }

    public void getAccount(@NotNull Context context) {
        context.json(accountRepository.getAll(getAndValidateId(context)));
    }

    public void getAllAccounts(@NotNull Context context) {
        context.json(accountRepository.getAll());
    }

    public void updateAccount(@NotNull Context context) {
        var account = Account.validate(context.bodyValidator(Account.class));
        var id = getAndValidateId(context);

        if (account.getId() != null && !account.getId().equals(id)) {
            throw new BadRequestResponse("Account id update is not allowed");
        }

        if (account.getId() == null) {
            account.setId(id);
        }

        accountRepository.update(account);
    }

    public void deleteAccount(@NotNull Context context) {
        accountRepository.delete(getAndValidateId(context));
    }

    public void transfer(@NotNull Context context) {
        var params = TransferParams.validate(context.bodyValidator(TransferParams.class));

        accountRepository.transfer(params);
    }

    private String getAndValidateId(@NotNull Context ctx) {
        var id = ctx.pathParam(ID_PARAM);

        HttpUtils.validateAccountId(id, ID_PARAM);

        return id;
    }
}