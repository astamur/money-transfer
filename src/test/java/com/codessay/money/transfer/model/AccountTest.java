package com.codessay.money.transfer.model;

import com.codessay.money.transfer.App;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {
    @Test
    void testSerialization() throws IOException {
        Account account = new Account("id_1", new BigDecimal("1.23"), "RUB");

        String json = App.mapper.writeValueAsString(account);

        assertEquals(account, App.mapper.readValue(json, Account.class));
    }
}