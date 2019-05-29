package com.codessay.money.transfer.model;

import io.javalin.validation.TypedValidator;

import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class Account {
    private static final Pattern pattern = Pattern.compile("^[A-Z]{3}$");

    private String id;
    private BigDecimal balance;
    private String currency;

    public Account() {
    }

    public Account(String id, BigDecimal balance, String currency) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public Account setId(String id) {
        this.id = id;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public Account setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (getId() != null ? !getId().equals(account.getId()) : account.getId() != null) return false;
        if (getBalance() != null ? !getBalance().equals(account.getBalance()) : account.getBalance() != null)
            return false;
        return getCurrency() != null ? getCurrency().equals(account.getCurrency()) : account.getCurrency() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getBalance() != null ? getBalance().hashCode() : 0);
        result = 31 * result + (getCurrency() != null ? getCurrency().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("balance=" + balance)
                .add("currency='" + currency + "'")
                .toString();
    }

    public static Account validate(TypedValidator<Account> validator) {
        return validator
                .check(obj -> obj.getCurrency() != null && pattern.matcher(obj.getCurrency()).matches(),
                        "Currency can not be null and should have 3 uppercase letters")
                .check(obj -> obj.getBalance() != null && obj.getBalance().scale() > -1 && obj.getBalance().scale() < 3,
                        "Balance can not be null and should have 2 digits after decimal point")
                .get();
    }
}