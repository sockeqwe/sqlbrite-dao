package com.hannesdorfmann.sqlbritedao.sample.model.customer;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import com.hannesdorfmann.sqlbritedao.sample.model.Person;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable public class Customer extends Person {

  public static final String TABLE_NAME = "Customer";
  public static final String COL_FIRSTNAME = "firstname";
  public static final String COL_LASTNAME = "lastname";
  public static final String COL_ADULT = "adult";

  @Column(COL_FIRSTNAME) String firstname;
  @Column(COL_LASTNAME)  private String mLastname;
  private boolean adult;

  public Customer() {
  }

  public Customer(long id, String firstname, String lastname, boolean adult) {
    this.id = id;
    this.firstname = firstname;
    this.mLastname = lastname;
    this.adult = adult;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return mLastname;
  }

  public boolean isAdult() {
    return adult;
  }

  @Column(COL_ADULT) public void setAdult(boolean adult) {
    this.adult = adult;
  }

  public void setmLastname(String mLastname) {
    this.mLastname = mLastname;
  }
}
