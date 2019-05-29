package com.codessay.money.transfer;

import com.codessay.money.transfer.controller.AccountController;
import com.codessay.money.transfer.repository.impl.InMemoryAccountRepository;
import com.codessay.money.transfer.util.Paths;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;
import org.eclipse.jetty.http.MimeTypes;

public class App {
    public static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    private static final int DEFAULT_PORT = 8000;
    private static final String DEFAULT_DB_PATH = ".db";

    private final int port;
    private final PersistentEntityStore entityStore;
    private final AccountController accountController;

    public App() {
        this(DEFAULT_PORT, DEFAULT_DB_PATH);
    }

    public App(int port, String dbPath) {
        this.port = port;

        entityStore = PersistentEntityStores.newInstance(dbPath);
        accountController = new AccountController(new InMemoryAccountRepository(entityStore));
    }

    public static void main(String[] args) {
        new App().startup();
    }

    public void startup() {
        JavalinJackson.configure(mapper);

        var server = Javalin.create()
                .port(port)
                .defaultContentType(MimeTypes.Type.APPLICATION_JSON_UTF_8.asString());

        server.get(Paths.INDEX, ctx -> ctx.result("Index"));

        server.get(Paths.ACCOUNTS_ACCOUNT, accountController::getAccount);
        server.get(Paths.ACCOUNTS, accountController::getAllAccounts);
        server.post(Paths.ACCOUNTS, accountController::addAccount);
        server.post(Paths.ACCOUNTS_TRANSFER, accountController::transfer);
        server.put(Paths.ACCOUNTS_ACCOUNT, accountController::updateAccount);
        server.delete(Paths.ACCOUNTS_ACCOUNT, accountController::deleteAccount);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
    }
}