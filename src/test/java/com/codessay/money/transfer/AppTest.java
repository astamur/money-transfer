package com.codessay.money.transfer;

import com.codessay.money.transfer.model.Account;
import com.codessay.money.transfer.util.Paths;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    void testAddAccount() {
        addAccount(getJsonWithoutId("RUB", "12.34"));
    }

    @Test
    void testGetAccounts() {
        var response1 = addAccount(getJsonWithoutId("RUB", "12.34"));
        var response2 = addAccount(getJsonWithoutId("USD", "56.78"));

        var location1 = response1.getHeader(HttpHeader.LOCATION.name());
        var location2 = response2.getHeader(HttpHeader.LOCATION.name());

        var id1 = location1.split("/")[2];
        var id2 = location2.split("/")[2];

        List<Account> accounts = get(Paths.ACCOUNTS)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList(".", Account.class);

        assertThat(accounts, hasItems(
                hasProperty("id", is(id1)),
                hasProperty("id", is(id2))
        ));
    }

    @Test
    void testGetAccount() {
        Response response = addAccount(getJsonWithoutId("RUB", "12.34"));

        var location = response.getHeader(HttpHeader.LOCATION.name());
        var id = location.split("/")[2];

        //@formatter:off
        get(response.getHeader(HttpHeader.LOCATION.name()))
            .then()
                .statusCode(HttpStatus.OK_200)
                .body("id", equalTo(id));
        //@formatter:on
    }

    @Test
    void testPostTransfer() {
        post(Paths.ACCOUNTS_TRANSFER).then().statusCode(HttpStatus.OK_200);
    }

    private String getJsonWithoutId(String currency, String balance) {
        return String.format("{\"currency\":\"%s\", \"balance\":%s}", currency, balance);
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