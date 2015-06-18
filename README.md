# SQLBrite DAO

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
  private CustomerMapper() {
  }

  /**
   * Retrieves the first element from  Cursor by scanning for @Column annotated fields.
   * @param cursor The Cursor
   * @return null if Cursor is empty or a single Customer fetched from the cursor
   */
  public static Customer single(Cursor cursor) {
      ...
  }

  /**
   * Fetches a list of {@link Customer } from a Cursor by scanning for @Column annotated fields.
   * @param cursor The Cursor
   * @return An empty List if cursor is empty or a list of items fetched from the cursor
   */
  public static List<Customer> list(Cursor cursor) {
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

So basically it generates code for iterating over a `Cursor` (cursor gets closed internally and resources released) and retrieve fetch data by calling `cursor.getString(index)` and so on.

```java
Cursor c = ... ; // Some SQL SELECT statement

List<Customer> customers = CustomerMapper.list(cursor);
```

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
        .WHERE(Customer.COL_LASTNAME + " = ? "),

        lastname // Argument that replaces "?" in WHERE

       .map(new Func1<SqlBrite.Query, List<Customer>>() {  // Converts SqlBrite.Query to List<Customer>

        @Override public List<Customer> call(SqlBrite.Query query) {
          Cursor cursor = query.run();
          return CustomerMapper.list(cursor);  // Generated Mapper class, already discussed above
        }
      });
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