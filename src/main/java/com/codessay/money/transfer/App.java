package com.codessay.money.transfer;

import com.codessay.money.transfer.controller.AccountController;
import com.codessay.money.transfer.repository.impl.InMemoryAccountRepository;
import com.codessay.money.transfer.service.impl.AccountServiceImpl;
import com.codessay.money.transfer.util.Paths;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.PersistentEntityStores;

public class App {
    public static final ObjectMapper mapper;

    private static final int DEFAULT_PORT = 8000;
    private static final String DEFAULT_DB_PATH = ".db";

    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    private final int port;
    private final String dbPath;
    private final PersistentEntityStore entityStore;
    private final AccountController accountController;

    public App() {
        this(DEFAULT_PORT, DEFAULT_DB_PATH);
    }

    public App(int port, String dbPath) {
        this.port = port;
        this.dbPath = dbPath;

        entityStore = PersistentEntityStores.newInstance(dbPath);
        accountController = new AccountController(new AccountServiceImpl(new InMemoryAccountRepository(entityStore)));
    }

    public static void main(String[] args) {
        new App().startup();
    }

    public void startup() {
        JavalinJackson.configure(mapper);

        var server = Javalin.create().port(port);

        server.get(Paths.INDEX, ctx -> ctx.result("Index"));

        server.get(Paths.ACCOUNTS, accountController::getAllAccounts);
        server.get(Paths.ACCOUNTS_ACCOUNT, accountController::getAccount);
        server.post(Paths.ACCOUNTS, accountController::addAccount);
        server.post(Paths.ACCOUNTS_TRANSFER, accountController::transfer);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        server.start();
    }
}