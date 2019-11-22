## Requirements
* Java 8
* Git
* Maven

## Design overview

* HTTP server: Jetty
* Database: H2 + Hibernate
* Application layers:
  + Controller - a nonblocking service processing incoming requests 
  + Business / Model - some random business logic
  + System - web, database, files, parsers
  + IOC - a simple fabric that provides all the main modules mainly as singletons
* Utility 
  + Gson
  + Junit 

## Tests

### Integration 

* **AppTest** - an integration test that is running whole application
* **StoreTest** - verifies database operations
* **EmbeddedServerTest** - verifies POST and GET methods 

```
********* POST:  SEND REQUEST *********
Transferring: 30.0
--->{"amount":30.0,"senderAccountId":52,"recipientAccountId":53,"action":"Send","transferType":0}
********* RESPONSE *********
<---{"message":"TRANSFER PROCESSED"}
********* GET:  CHECK REQUEST *********
--->{"amount":23412.83,"senderAccountId":52,"recipientAccountId":53,"paymentId":0,"action":"Check","transferType":0}
********* CHECK *********
<---{"message":"TRANSFER PROCESSED","sender":{"account":{"id":52}},"recipient":{"account":{"id":53}},"transfer":{"sender":{"account":{"id":52}},"recipient":{"account":{"id":53}}}}

```

### Other

* SendTest - verifies if arithmetic is correct

```
************ INITIAL ************
Sender account: 100.0
Recipient account: 100.0
************ TRANSFER ************
To send: 30.0
************ RESULT ************
Sent: 30.0
Sender commission: 17.5
Recipient commission: 17.5
Sender account: 52.5
Recipient account: 112.5
Payment: 30.0
Profit: 35.0
```
