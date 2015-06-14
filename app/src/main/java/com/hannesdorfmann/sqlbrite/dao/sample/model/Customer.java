package com.hannesdorfmann.sqlbrite.dao.sample.model;

/**
 * @author Hannes Dorfmann
 */
public class Customer {

  public static final String TABLE_NAME="Customer";
  public static final String COL_ID = "id";
  public static final String COL_FIRSTNAME = "firstname";
  public static final String COL_LASTNAME = "lastname";


  long id;
  String firstname;
  String lastname;

  public Customer(long id, String firstname, String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
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
