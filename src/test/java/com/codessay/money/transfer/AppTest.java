package com.codessay.money.transfer;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.model.TransferParams;
import com.codessay.money.transfer.util.Paths;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

class AppTest {
    private static final int TEST_PORT = 8090;
    private static final String TEST_DB_PATH = ".test-db";
    private static final Pattern accountPattern = Pattern.compile("^/accounts/\\d+-\\d+$");

    @BeforeAll
    static void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = TEST_PORT;

        new App(TEST_PORT, TEST_DB_PATH).startup();
    }

    @Test
    void testGetIndex() {
        get(Paths.INDEX).then().body(is("Index"));
    }

    @Test
    void testAddAndGetAccount() {
        // Create new account
        Response response = addAccount(getJsonWithoutId("RUB", "12.34"));

        var location = response.getHeader(HttpHeader.LOCATION.name());
        var id = location.split("/")[2];

        // Ensure that account has been created
        //@formatter:off
        get(location)
            .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id))
                .body(Account.PROPERTY_CURRENCY, equalTo("RUB"))
                .body(Account.PROPERTY_BALANCE, equalTo(12.34f));
        //@formatter:on
    }

    @Test
    void testGetAccounts() {
        // Create new accounts
        var response1 = addAccount(getJsonWithoutId("RUB", "12.34"));
        var response2 = addAccount(getJsonWithoutId("USD", "56.78"));

        var location1 = response1.getHeader(HttpHeader.LOCATION.name());
        var location2 = response2.getHeader(HttpHeader.LOCATION.name());

        var id1 = location1.split("/")[2];
        var id2 = location2.split("/")[2];

        //@formatter:off
        List<Account> accounts = get(Paths.ACCOUNTS)
                .then()
                    .extract()
                    .body()
                    .jsonPath()
                    .getList(".", Account.class);
        //@formatter:on

        assertThat(accounts, hasItems(
                hasProperty(Account.PROPERTY_ID, is(id1)),
                hasProperty(Account.PROPERTY_ID, is(id2))
        ));
    }

    @Test
    void testUpdateAccount() {
        // Create new account
        Response response = addAccount(getJsonWithoutId("RUB", "12.34"));

        var location = response.getHeader(HttpHeader.LOCATION.name());
        var id = location.split("/")[2];

        // Update account
        //@formatter:off
        given()
            .body(getJsonWithoutId("USD", "56.78"))
        .when()
            .put(location)
        .then()
            .statusCode(HttpStatus.OK_200);

        // Ensure account has been updated
        get(location)
            .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id))
                .body(Account.PROPERTY_CURRENCY, equalTo("USD"))
                .body(Account.PROPERTY_BALANCE, equalTo(56.78f));
        //@formatter:on
    }

    @Test
    void testDeleteAccount() {
        Response response = addAccount(getJsonWithoutId("RUB", "12.34"));

        var location = response.getHeader(HttpHeader.LOCATION.name());

        delete(location).then().statusCode(HttpStatus.OK_200);

        get(location).then().statusCode(HttpStatus.NOT_FOUND_404);

    }

    @Test
    void testPostTransfer() {
        // Create new accounts
        var response1 = addAccount(getJsonWithoutId("RUB", "100"));
        var response2 = addAccount(getJsonWithoutId("RUB", "100"));

        var location1 = response1.getHeader(HttpHeader.LOCATION.name());
        var location2 = response2.getHeader(HttpHeader.LOCATION.name());

        var id1 = location1.split("/")[2];
        var id2 = location2.split("/")[2];

        // Make a transfer
        //@formatter:off
        given()
            .body(getTransferParamsJson(id1, id2,"50" ))
        .when()
            .post(Paths.ACCOUNTS_TRANSFER)
        .then()
            .statusCode(HttpStatus.OK_200);
        //@formatter:on

        // Ensure that the first account's balance has decreased on 50
        get(location1)
                .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id1))
                .body(Account.PROPERTY_CURRENCY, equalTo("RUB"))
                .body(Account.PROPERTY_BALANCE, equalTo(50));
        //@formatter:on

        // Ensure that the second account's balance has increased on 50
        get(location2)
                .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id2))
                .body(Account.PROPERTY_CURRENCY, equalTo("RUB"))
                .body(Account.PROPERTY_BALANCE, equalTo(150));
        //@formatter:on
    }

    @Test
    void testPostTransferConcurrently() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // Create new accounts
        var response1 = addAccount(getJsonWithoutId("RUB", "500"));
        var response2 = addAccount(getJsonWithoutId("RUB", "500"));

        var location1 = response1.getHeader(HttpHeader.LOCATION.name());
        var location2 = response2.getHeader(HttpHeader.LOCATION.name());

        var id1 = location1.split("/")[2];
        var id2 = location2.split("/")[2];

        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0) {
                executor.execute(() -> {
                    //@formatter:off
                    given()
                        .body(getTransferParamsJson(id1, id2,"1" ))
                    .when()
                        .post(Paths.ACCOUNTS_TRANSFER)
                    .then()
                        .statusCode(HttpStatus.OK_200);
                    //@formatter:on
                });
            } else {
                executor.execute(() -> {
                    //@formatter:off
                    given()
                        .body(getTransferParamsJson(id2, id1,"1" ))
                    .when()
                        .post(Paths.ACCOUNTS_TRANSFER)
                    .then()
                        .statusCode(HttpStatus.OK_200);
                    //@formatter:on
                });
            }
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Ensure that the first account's balance remains 100
        get(location1)
                .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id1))
                .body(Account.PROPERTY_CURRENCY, equalTo("RUB"))
                .body(Account.PROPERTY_BALANCE, equalTo(500));
        //@formatter:on

        // Ensure that the second account's balance remains 100
        get(location2)
                .then()
                .statusCode(HttpStatus.OK_200)
                .body(Account.PROPERTY_ID, equalTo(id2))
                .body(Account.PROPERTY_CURRENCY, equalTo("RUB"))
                .body(Account.PROPERTY_BALANCE, equalTo(500));
        //@formatter:on
    }

    private String getJsonWithoutId(String currency, String balance) {
        return String.format("{\"%s\":\"%s\", \"%s\":%s}",
                Account.PROPERTY_CURRENCY, currency,
                Account.PROPERTY_BALANCE, balance);
    }

    private String getTransferParamsJson(String fromId, String toId, String amount) {
        return String.format("{\"%s\":\"%s\", \"%s\":\"%s\", \"%s\":%s}",
                TransferParams.PROPERTY_FROM, fromId,
                TransferParams.PROPERTY_TO, toId,
                TransferParams.PROPERTY_AMOUNT, amount);
    }

    private Response addAccount(String json) {
        //@formatter:off
        return  given()
                    .body(json)
                .when()
                    .post(Paths.ACCOUNTS)
                .then()
                    .statusCode(HttpStatus.CREATED_201)
                    .header(HttpHeader.LOCATION.name(), matchesPattern(accountPattern))
                    .extract()
                    .response();
        //@formatter:on
    }
}