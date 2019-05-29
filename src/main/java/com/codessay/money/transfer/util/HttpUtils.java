package com.codessay.money.transfer.util;

import com.codessay.money.transfer.model.Account;
import io.javalin.BadRequestResponse;

public class HttpUtils {
    public static String formatAccountLocation(String id) {
        return String.format("%s/%s", Paths.ACCOUNTS, id);
    }

    public static void validateAccountId(String id, String paramName) {
        if (!id.matches(Account.ACCOUNT_ID_REGEX)) {
            throw new BadRequestResponse(String.format("Invalid parameter '%s'. Account id should match this pattern: '%s'",
                    paramName, Account.ACCOUNT_ID_REGEX));
        }
    }
}