package com.hannesdorfmann.sqlbrite.dao.sample.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.hannesdorfmann.sqlbrite.dao.sample.model.customer.Customer;
import com.hannesdorfmann.sqlbrite.dao.sample.model.customer.CustomerMapper;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import com.squareup.sqlbrite.SqlBrite;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable
public abstract class Person {

  public static final String COL_ID = "id";



  protected long id;

  @Column(COL_ID)
  public void setId(int id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

}
