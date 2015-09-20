package com.hannesdorfmann.sqlbrite.dao.sql.testdao;

import java.util.Arrays;

/**
 * A simple {@link User} just for unit testing
 *
 * @author Hannes Dorfmann
 */
public class User {

  public long id;

  public String name;

  public int age;

  public double weight;

  public byte[] blob;

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public void setBlob(byte[] blob) {
    this.blob = blob;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (id != user.id) return false;
    if (age != user.age) return false;
    if (Double.compare(user.weight, weight) != 0) return false;
    if (!name.equals(user.name)) return false;
    return Arrays.equals(blob, user.blob);
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + age;
    temp = Double.doubleToLongBits(weight);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + Arrays.hashCode(blob);
    return result;
  }
}
