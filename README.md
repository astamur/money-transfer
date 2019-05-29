# Money transfer
A simple money transfer RESTful application using Java 11, [Javalin](https://javalin.io/) library and [Xodus](https://github.com/JetBrains/xodus) database.

### Build and run application
`./gradlew clean build && java -jar build/libs/money-transfer.jar `

### API usage description

`/accounts`
  - GET - *get all accounts list*
  - POST - *create new account*

`/accounts/:id`
 
  - 


`curl -X POST -v "http://localhost:8000/accounts" -d '{"currency":"RUB", "balance":100}'`
