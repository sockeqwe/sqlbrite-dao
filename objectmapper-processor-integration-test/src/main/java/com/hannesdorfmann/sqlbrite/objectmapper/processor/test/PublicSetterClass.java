package com.hannesdorfmann.sqlbrite.objectmapper.processor.test;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable public class PublicSetterClass {

  public static final String COL_STRING = "aString";
  public static final String COL_INT = "anInt";
  public static final String COL_LONG = "aLong";
  public static final String COL_SHORT = "aShort";
  public static final String COL_FLOAT = "aFloat";
  public static final String COL_DOUBLE = "aDouble";
  public static final String COL_BOOLEAN = "aBool";
  public static final String COL_BYTE = "bytes";
  public static final String COL_DATE = "aDate";

  @Column(COL_STRING) private String aString;
  @Column(COL_INT) private int anInt;
  @Column(COL_LONG) private long aLong;
  @Column(COL_SHORT) private short aShort;
  @Column(COL_FLOAT) private float aFloat;
  @Column(COL_DOUBLE) private double aDouble;
  @Column(COL_BOOLEAN) private boolean aBool;
  @Column(COL_BYTE) private byte[] bytes;
  @Column(COL_DATE) private Date aDate;

  public String getaString() {
    return aString;
  }

  public void setAString(String aString) {
    this.aString = aString;
  }

  public int getAnInt() {
    return anInt;
  }

  public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public long getALong() {
    return aLong;
  }

  public void setALong(long aLong) {
    this.aLong = aLong;
  }

  public short getAShort() {
    return aShort;
  }

  public void setAShort(short aShort) {
    this.aShort = aShort;
  }

  public float getAFloat() {
    return aFloat;
  }

  public void setAFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public double getADouble() {
    return aDouble;
  }

  public void setADouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public boolean isABool() {
    return aBool;
  }

  public void setABool(boolean aBool) {
    this.aBool = aBool;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public Date getADate() {
    return aDate;
  }

  public void setADate(Date aDate) {
    this.aDate = aDate;
  }
}
