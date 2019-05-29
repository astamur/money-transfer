package com.codessay.money.transfer.util;

public class Paths {
    public static final String INDEX = "/index";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    public static final String ACCOUNTS = "/accounts";
    public static final String ACCOUNTS_ACCOUNT = String.format("%s/:id", ACCOUNTS);
    public static final String ACCOUNTS_TRANSFER = String.format("%s/transfer", ACCOUNTS);

    public static String getAccountLocation(String id) {
        return String.format("%s/%s", ACCOUNTS, id);
    }
}