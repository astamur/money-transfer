package com.codessay.money.transfer.controller;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;
import com.codessay.money.transfer.service.AccountService;
import com.codessay.money.transfer.util.HttpUtils;
import com.codessay.money.transfer.util.Paths;
import io.javalin.Context;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;

public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public void addAccount(Context ctx) {
        Account account = Account.validate(ctx.bodyValidator(Account.class));

        accountService.add(account);

        ctx.status(HttpStatus.CREATED_201)
                .header(HttpHeader.LOCATION.name(), Paths.getAccountLocation(account.getId()));
    }

    public void getAllAccounts(Context ctx) {
        accountService.get();

        ctx.json(accountService.get());
    }

    public void getAccount(Context ctx) {
        ctx.json(accountService.get(ctx.pathParam("id")));
    }

    public void transfer(Context ctx) {
        accountService.transfer(getParams(ctx));
        ctx.json(accountService.get(ctx.pathParam("id")));
    }

    private TransferParams getParams(Context ctx) {
        var params = new TransferParams();

        params.setFrom(HttpUtils.requiredQueryParamString(ctx, "from"));
        params.setTo(HttpUtils.requiredQueryParamString(ctx, "to"));
        params.setAmount(HttpUtils.requiredQueryParamDouble(ctx, "amount"));

        return params;
    }
}