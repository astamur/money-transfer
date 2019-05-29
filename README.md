# Money transfer
A simple money transfer RESTful application using Java 11, [Javalin](https://javalin.io/) library and [Xodus](https://github.com/JetBrains/xodus) database.

### Build and run application
`./gradlew clean build && java -jar build/libs/money-transfer.jar `

### API usage description
**`/accounts`**
  - **GET** - get all accounts list (`curl "http://localhost:8000/accounts"`)
  - **POST** - create new account (`curl "http://localhost:8000/accounts" -d '{"currency":"RUB", "balance":100}'`)

**`/accounts/:id`**
  - **GET** - get single account with specific `id` (`curl "http://localhost:8000/accounts/0-0"`)
  - **PUT** - update account with specific `id` (`curl -X PUT "http://localhost:8000/accounts/0-0" -d '{"currency":"USD", "balance":200}'`)
  - **DELETE** - delete account with specific `id` (`curl -X DELETE "http://localhost:8000/accounts/0-0"`)
  
**`/accounts/transfer`**
  - **POST** - make transfer (`curl "http://localhost:8000/accounts/transfer" -d '{"from":"0-0", "to":"0-1", "amount":50}'`)


Account JSON:
```javascript
{
  "id": "0-0", 
  "currency":"RUB",
  "balance":100
}
```

Transfer parameters JSON:
```javascript
{
  "from":"0-0", 
  "to":"0-1", 
  "amount":50
}
```
