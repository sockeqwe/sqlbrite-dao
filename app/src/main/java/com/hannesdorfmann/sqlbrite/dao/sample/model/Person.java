package com.hannesdorfmann.sqlbrite.dao.sample.model;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;

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
