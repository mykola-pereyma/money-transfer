## MoneyTransfer project in Kotlin

### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [sqlite](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Jackson](https://github.com/FasterXML/jackson) - JSON serialization/deserialization
 - [JUnit 5](https://junit.org/junit5/), [AssertJ](http://joel-costigliola.github.io/assertj/) and [Rest Assured](http://rest-assured.io/) for testing
 
This project creates a new in-memory sqlite database with two tables `AccountTransactions` and `TransferRequests`. 

### Routes:

`GET /transfer-requests` --> get all transfer requests in the database

cURL: `curl --header "Content-Type: application/json" --reques GET http://localhost:8080/transfer-requests`
#
`GET /account-transactions` --> get all accounts transactions in the database

cURL: `curl --header "Content-Type: application/json" --reques GET http://localhost:8080/transfer-requests`

#
`POST /transfer-requests` --> make a new transfer requests to the database by providing a JSON object.

cURL: `curl --header "Content-Type: application/json" --reques POST --data '{"senderAccount":"1111222233334444", "receiverAccount":"5555666677778888", "amount":"100", "currencyCode":"EUR"}' http://localhost:8080/transfer-requests`
e.g - 

    {
        "senderAccount":"1111222233334444",
         "receiverAccount":"5555666677778888",
          "amount":"100", "currencyCode":"EUR"
    }

returns

    {
        "id" : 1324,
        "status" : "PROCESSED",
        "dateUpdated" : 1583169646367
    }
    
### Testing

The sample MoneyTransfer service and corresponding endpoints covered with:

- Unit testing of services with AssertJ - DAO and business logic
- Integration testing of endpoints using running server with Rest Assured - routing tests/status codes/response structure