package com.hannesdorfmann.sqlbrite.objectmapper.processor.test;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable public class PublicSetterAnnotatedClass {

  public static final String COL_STRING = "aString";
  public static final String COL_INT = "anInt";
  public static final String COL_LONG = "aLong";
  public static final String COL_SHORT = "aShort";
  public static final String COL_FLOAT = "aFloat";
  public static final String COL_DOUBLE = "aDouble";
  public static final String COL_BOOLEAN = "aBool";
  public static final String COL_BYTE = "bytes";
  public static final String COL_DATE = "aDate";

  private String aString;
  private int anInt;
  private long aLong;
  private short aShort;
  private float aFloat;
  private double aDouble;
  private boolean aBool;
  private byte[] bytes;
  private Date aDate;

  public String getaString() {
    return aString;
  }

  @Column(COL_STRING) public void setAString(String aString) {
    this.aString = aString;
  }

  public int getAnInt() {
    return anInt;
  }

  @Column(COL_INT) public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public long getALong() {
    return aLong;
  }

  @Column(COL_LONG) public void setALong(long aLong) {
    this.aLong = aLong;
  }

  public short getAShort() {
    return aShort;
  }

  @Column(COL_SHORT) public void setAShort(short aShort) {
    this.aShort = aShort;
  }

  public float getAFloat() {
    return aFloat;
  }

  @Column(COL_FLOAT) public void setAFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public double getADouble() {
    return aDouble;
  }

  @Column(COL_DOUBLE) public void setADouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public boolean isABool() {
    return aBool;
  }

  @Column(COL_BOOLEAN) public void setABool(boolean aBool) {
    this.aBool = aBool;
  }

  public byte[] getBytes() {
    return bytes;
  }

  @Column(COL_BYTE) public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public Date getADate() {
    return aDate;
  }

  @Column(COL_DATE) public void setADate(Date aDate) {
    this.aDate = aDate;
  }
}
