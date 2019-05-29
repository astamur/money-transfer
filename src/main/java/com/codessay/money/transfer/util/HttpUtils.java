package com.codessay.money.transfer.util;

import io.javalin.BadRequestResponse;
import io.javalin.Context;

public class HttpUtils {
    public static String requiredQueryParamString(Context ctx, String paramName) {
        String value = ctx.queryParam(paramName);

        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestResponse(String.format("Parameter '%s' is required", paramName));
        }

        return value;
    }

    public static Double requiredQueryParamDouble(Context ctx, String paramName) {
        String value = requiredQueryParamString(ctx, paramName);

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new BadRequestResponse(String.format("Parameter '%s' has invalid format", paramName));
        }
    }
}