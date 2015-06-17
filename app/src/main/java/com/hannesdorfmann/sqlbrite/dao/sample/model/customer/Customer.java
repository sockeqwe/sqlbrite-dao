package com.hannesdorfmann.sqlbrite.dao.sample.model.customer;

import com.hannesdorfmann.sqlbrite.dao.sample.model.Person;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable
public class Customer extends Person {

  public static final String TABLE_NAME = "Customer";
  public static final String COL_FIRSTNAME = "firstname";
  public static final String COL_LASTNAME = "lastname";


  @Column(COL_FIRSTNAME) String firstname;

  @Column(COL_LASTNAME) String lastname;

  public Customer() {
  }

  public Customer(long id, String firstname, String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
  }


  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }
}
