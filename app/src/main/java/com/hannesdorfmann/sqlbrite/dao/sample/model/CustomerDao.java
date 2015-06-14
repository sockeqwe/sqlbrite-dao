package com.hannesdorfmann.sqlbrite.dao.sample.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.squareup.sqlbrite.SqlBrite;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
public class CustomerDao extends Dao {

  @Override public void createTable(SQLiteDatabase database) {
    CREATE_TABLE(Customer.TABLE_NAME, Customer.COL_ID + " INTEGER PRIMARY KEY NOT NULL",
        Customer.COL_FIRSTNAME + " TEXT", Customer.COL_LASTNAME + " TEXT").execute(database);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  public Observable<List<Customer>> getCustomers() {
    return query(SELECT(Customer.COL_ID, Customer.COL_FIRSTNAME, Customer.COL_LASTNAME).FROM(
        Customer.TABLE_NAME)).map(new Func1<SqlBrite.Query, List<Customer>>() {
      @Override public List<Customer> call(SqlBrite.Query query) {

        List<Customer> customers = new ArrayList<Customer>();
        Cursor c = null;
        try {
          c = query.run();
          if (c.moveToFirst()) {
            do {
              Customer customer = new Customer(c.getInt(0), c.getString(1), c.getString(2));
              customers.add(customer);
            } while (c.moveToNext());
          }
        } finally {
          if (c != null) {
            c.close();
          }
        }

        return customers;
      }
    });
  }

  public Observable<Long> insert(int id, String firstname, String lastname) {
    ContentValues values = new ContentValues();
    values.put(Customer.COL_ID, id);
    values.put(Customer.COL_FIRSTNAME, firstname);
    values.put(Customer.COL_LASTNAME, lastname);
    return insert(Customer.TABLE_NAME, values);
  }
}
