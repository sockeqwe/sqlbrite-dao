# SQLBrite DAO

[![Build Status](https://travis-ci.org/sockeqwe/sqlbrite-dao.svg?branch=master)](https://travis-ci.org/sockeqwe/sqlbrite-dao)

This library adds an layer on top of [SQLBrite](https://github.com/square/sqlbrite) by providing basically two modules:

1. ObjectMapper
2. Dao


## ObjectMapper
This project doesn't aim to implement a full ORM framework and will never be. It just uses annotation processing to generate code for reading value from `Cursor` and handle `ContentValues`.
With `@Column` you specify with database table column belongs to which field of your Model class. You can annotate fields (no private or protected fields are allowed) or public setter methods.
With `@ObjectMappable` you have to annotate your model class containnig `@Column` annotations like this:

```java
@ObjectMappable
public class Customer {

  public static final String TABLE_NAME = "Customer";
  public static final String COL_FIRSTNAME = "firstname";
  public static final String COL_LASTNAME = "lastname";
  public static final String COL_ID = "id";



  protected long id;
  @Column(COL_FIRSTNAME) String firstname;
  @Column(COL_LASTNAME) String lastname;

  public Customer() {
  }

  public Customer(long id, String firstname, String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
  }

   @Column(COL_ID)
   public void setId(int id) {
    this.id = id;
   }

   public long getId() {
    return id;
   }


  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }
}
```

For `Customer` a class called `CustomerMapper` gets generated that looks like this:

```java
public final class CustomerMapper {

  /**
   * RxJava Func1 Method that can be used to generate a Customer from a Cursor's data (sql row)
   */
  public static final Func1<Cursor, Customer> MAPPER = new Func1<Cursor, Customer>() {
    ...
  }


  /**
   * Get a typesafe ContentValues Builder
   * @return The ContentValues Builder
   */
  public static ContentValuesBuilder contentValues() {
    ...
  }

}
```

So basically it generates a `Func1` that can be applied on a SQLBrite `QueryObservable` to instantiate your data object (i.e. Customer) out of a `Cursor` (cursor gets closed internally and resources released by SQLBrite) and retrieve fetch data by calling `cursor.getString(index)` and so on. See **DAO** section below for a concrete example how to use the generated `Func1`.
Additionally, it also generates a type safe builder for `ContentValues`.
```java
ContentValues cv = CustomerMapper.contentValues()
                        .id(1)
                        .firstname("Hannes")
                        .lastname("Dorfmann")
                        .build();
```

The supported types for `@Column` are:
 - String
 - int
 - long
 - short
 - float
 - double
 - byte[]
 - java.util.Date (mapped to long internally, time in milli seconds)


## DAO
Create your own Data Access Object (DAO) where you define methods to manipulate or query your database table.
Usually a DAO represents a database table, like the database table for storing `Customer`:

```java
public class CustomerDao extends Dao {

  @Override public void createTable(SQLiteDatabase database) {

    CREATE_TABLE(Customer.TABLE_NAME,
        Customer.COL_ID + " INTEGER PRIMARY KEY NOT NULL",
        Customer.COL_FIRSTNAME + " TEXT",
        Customer.COL_LASTNAME + " TEXT")
        .execute(database);

  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion == 1 && newVersion == 2){
      ALTER_TABLE(Customer.TABLE_NAME)
          .ADD_COLUMN(Customer.PHONE_NUMBER +" TEXT")
          .execute(db);
    }
  }


  ...

}
```

`onCreateTable()` and `onUpgrade()` gets called internally (from an internal `SQLiteOpenHelper`) to create or migrate the table for `Customer`.

As you already have seen above, the DAO provides provides a high level API so you don't have to deal that much with String concatenation and can use IDE's auto completion to build your sql statements. The same API can be used to create SQL query statements:

```java
public class CustomerDao extends Dao {

  @Override public void createTable(SQLiteDatabase database) {
    ...
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    ...
  }


  public Observable<List<Customer>> getCustomers(String lastname) {
      return query(
        SELECT(Customer.COL_ID, Customer.COL_FIRSTNAME, Customer.COL_LASTNAME)
        .FROM(Customer.TABLE_NAME)
        .WHERE(Customer.COL_LASTNAME + " = ? ")
       )
       .args(lastname) // arguments that replace the "?" placeholders in SQL statement
       .run() // Executes query
       .mapToList(CustomerMapper.MAPPER) // Use the generated Func1 method
      );
    }


    public Observable<Long> addCustomer(int id, String firstname, String lastname) {
      ContentValues values = CustomerMapper.contentValues()
                             .id(id)
                             .firstname(firstname)
                             .lastname(lastname)
                             .build();

      return insert(Customer.TABLE_NAME, values);
    }

}
```

To register your DAO classes to `SQLBrite` you have to create a `DaoManager`. While a `Dao` represents a table of a database `DaoManager` represents the whole database file. `DaoManager` internally creates a `SQLiteOpenHelper` and instantiates a `SqlBrite` instance. All DAO's registered to the same `DaoManager` share the same `SqlBrite` instance.
```java
CustomerDao customerDao = new CustomerDao();
AddressDao addressDao = new AddressDao();

int dbVersion = 1;
DaoManager daoManager = new DaoManager(context, "Customers.db", dbVersion, customerDao, addressDao);
daoManager.setLogging(true);
```

Please note that adding DAO's dynamically (later) is not possible. You have to instantiate a `DaoManager` and pass all your DAO's in the constructor as seen above.

To sum up:
 - A `DaoManager` is representing the whole database file and basically is a `SQLiteOpenHelper` and manages `SqlBrite` instance for you.
 - A `Dao` is representing a table of a database. You define a public API for other software components of your App like `getCustomers()` or `addCustomer()` to query and manipulate the data of the underlying table.

## Dependencies
Latest version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.sqlbrite/dao/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.hannesdorfmann.sqlbrite/dao/)

```groovy
// DAO
compile 'com.hannesdorfmann.sqlbrite:dao:xxx'

// Object Mapper
compile 'com.hannesdorfmann.sqlbrite:annotations:xxx'
apt 'com.hannesdorfmann.sqlbrite:object-mapper:xxx'
```
To run annotation processing you have to use [apt](https://bitbucket.org/hvisser/android-apt).
Please note that DAO and Object-Mapper are independent. You can choose whether you want to use both or not. 

## License
```
Copyright 2015 Hannes Dorfmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
