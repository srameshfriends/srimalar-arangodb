#### Srimalar Arangodb Transaction Management

* Supported only single database transaction management
* The arangodb collection automatically added in to the transaction.
* Perform arangodb database operation it will begin transaction automatically (beginStreamTransaction).
* The transaction life cycle manually need to be call (commitStreamTransaction) or (abortStreamTransaction)
* Make sure the operation will be completed otherwise it will cause unknown issues of your database transactions.

##### 2023-06-04

- Message property & Message Cache model updated

##### 2023-06-01

- Spring web dependency added
- Message property, message exception, error model added.
- update CRUD classes.

##### 2023-04-30

Version 1.0

- Initial db operation is created.
- Date format is yyyy-MM-dd
- Date time format is yyyy-MM-dd HH:mm:ss

 
