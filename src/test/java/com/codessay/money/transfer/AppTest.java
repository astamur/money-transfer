package com.codessay.money.transfer;

import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpHeader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.matchesPattern;

class AppTest {
    private static final int TEST_PORT = 8090;
    private static final String TEST_DB_PATH = ".test-db";
    private static final Pattern accountPattern = Pattern.compile("^/accounts/\\d-\\d+$");

    @BeforeAll
    static void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = TEST_PORT;

        new App(TEST_PORT, TEST_DB_PATH).startup();
    }

    @Test
    void testGetIndex() {
        get("/index").then().body(is("Index"));
    }

    @Test
    void testAddAccount() {
        //@formatter:off
        given()
            .body("{\"currency\":\"RUB\", \"balance\":100}")
        .when()
            .post("/accounts")
        .then()
            .statusCode(201)
            .header(HttpHeader.LOCATION.name(), matchesPattern(accountPattern));
        //@formatter:on
    }

    @Test
    void testGetAccounts() {
        get("/accounts").then().body(is("Accounts list"));
    }

    @Test
    void testGetAccount() {
        get("/accounts/12345").then().body(is("Account: 12345"));
    }

    @Test
    void testPostTransfer() {
        post("/accounts/transfer").then().statusCode(201);
    }
}