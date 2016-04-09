package com.hannesdorfmann.sqlbrite.objectmapper.processor.test;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import java.util.Date;

/**
 * @author Hannes Dorfmann
 */
@ObjectMappable public class PublicFieldClass {

  public static final String COL_STRING = "aString";
  public static final String COL_INT = "anInt";
  public static final String COL_LONG = "aLong";
  public static final String COL_SHORT = "aShort";
  public static final String COL_FLOAT = "aFloat";
  public static final String COL_DOUBLE = "aDouble";
  public static final String COL_BOOLEAN = "aBool";
  public static final String COL_BYTE = "bytes";
  public static final String COL_DATE = "aDate";


  @Column(COL_STRING) String aString;
  @Column(COL_INT) int anInt;
  @Column(COL_LONG) long aLong;
  @Column(COL_SHORT) short aShort;
  @Column(COL_FLOAT) float aFloat;
  @Column(COL_DOUBLE) double aDouble;
  @Column(COL_BOOLEAN) boolean aBool;
  @Column(COL_BYTE) byte[] bytes;
  @Column(COL_DATE) Date aDate;
}
