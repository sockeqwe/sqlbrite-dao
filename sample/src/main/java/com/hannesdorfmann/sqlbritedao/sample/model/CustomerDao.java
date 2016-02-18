package com.hannesdorfmann.sqlbritedao.sample.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.hannesdorfmann.sqlbritedao.sample.model.customer.Customer;
import com.hannesdorfmann.sqlbritedao.sample.model.customer.CustomerMapper;
import com.hannesdorfmann.sqlbritedao.sample.model.customer.KotlinCustomer;
import java.util.List;
import rx.Observable;

/**
 * @author Hannes Dorfmann
 */
public class CustomerDao extends Dao {

  @Override public void createTable(SQLiteDatabase database) {

    CREATE_TABLE(Customer.TABLE_NAME, Customer.COL_ID + " INTEGER PRIMARY KEY NOT NULL",
        Customer.COL_FIRSTNAME + " TEXT", Customer.COL_LASTNAME + " TEXT",
        Customer.COL_ADULT + " BOOLEAN").execute(database);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public Observable<List<Customer>> getCustomers() {
    return query(SELECT(Customer.COL_ID, Customer.COL_FIRSTNAME, Customer.COL_LASTNAME,
        Customer.COL_ADULT).FROM(Customer.TABLE_NAME)).run().mapToList(CustomerMapper.MAPPER);
  }

  public Observable<Long> insert(int id, String firstname, String lastname, boolean adult) {
    ContentValues values = CustomerMapper.contentValues()
        .id(id)
        .firstname(firstname)
        .lastname(lastname)
        .adult(adult)
        .build();

    return insert(Customer.TABLE_NAME, values);
  }
}