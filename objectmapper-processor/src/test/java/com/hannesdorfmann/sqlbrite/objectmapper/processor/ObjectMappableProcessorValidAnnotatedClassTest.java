package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import javax.tools.JavaFileObject;
import org.junit.Test;

/**
 * Tests to check if properly annotated classes are processed correctly by
 * {@link ObjectMappableProcessor}. Only put tests in here that produce correctly generated code
 *
 * @author Hannes Dorfmann
 */
public class ObjectMappableProcessorValidAnnotatedClassTest {

  /**
   * Tests that fields with min default visibility work as expected
   */
  @Test public void defaultFieldVisibility() {
    JavaFileObject file =
        JavaFileObjects.forSourceLines("test.DefaultFieldClass",
            "package test;",
            "",
            "@"+ ObjectMappable.class.getCanonicalName(),
            "public class DefaultFieldClass {",
            "   @"+ Column.class.getCanonicalName()+"(\"foo\")",
            "   String foo;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"anInt\")",
            "   int anInt;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aLong\")",
            "   long aLong;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aShort\")",
            "   short aShort;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aFloat\")",
            "   float aFloat;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDouble\")",
            "   double aDouble;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aBoolean\")",
            "   boolean aBoolean;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"bytes\")",
            "   byte[] bytes;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDate\")",
            "   java.util.Date aDate;",
            "}"
        );

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(file)
        .processedWith(new ObjectMappableProcessor())
        .compilesWithoutError();
  }


  /**
   * Tests that fields public visibility work as expected
   */
  @Test public void publicFieldVisibility() {
    JavaFileObject file =
        JavaFileObjects.forSourceLines("test.PublicFieldClass",
            "package test;",
            "",
            "@"+ ObjectMappable.class.getCanonicalName(),
            "public class PublicFieldClass {",
            "   @"+ Column.class.getCanonicalName()+"(\"foo\")",
            "   public String foo;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"anInt\")",
            "   public int anInt;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aLong\")",
            "   public long aLong;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aShort\")",
            "   public short aShort;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aFloat\")",
            "   public float aFloat;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDouble\")",
            "   public double aDouble;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aBoolean\")",
            "   public boolean aBoolean;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"bytes\")",
            "   public byte[] bytes;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDate\")",
            "   public java.util.Date aDate;",
            "}"
        );

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(file)
        .processedWith(new ObjectMappableProcessor())
        .compilesWithoutError();
  }


  /**
   * Tests that fields public visibility work as expected
   */
  @Test public void publicSetterVisibility() {
    JavaFileObject file =
        JavaFileObjects.forSourceLines("test.PublicSetterVisibility",
            "package test;",
            "",
            "@"+ ObjectMappable.class.getCanonicalName(),
            "public class PublicSetterVisibility {",
            "   @"+ Column.class.getCanonicalName()+"(\"foo\")",
            "   private String aString;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"anInt\")",
            "   private int anInt;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aLong\")",
            "   private long aLong;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aShort\")",
            "   private short aShort;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aFloat\")",
            "   private float aFloat;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDouble\")",
            "   private double aDouble;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aBoolean\")",
            "   private boolean aBoolean;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"bytes\")",
            "   private byte[] bytes;",
            "",
            "   @"+ Column.class.getCanonicalName()+"(\"aDate\")",
            "   private java.util.Date aDate;",
            "",
            "public void setAString(String aString){this.aString = aString; }",
            "public void setAnInt(int p){this.anInt = p; }",
            "public void setALong(long p){this.aLong = p; }",
            "public void setAShort(short p){this.aShort = p; }",
            "public void setAFloat(float p){this.aFloat = p; }",
            "public void setADouble(double p){this.aDouble = p; }",
            "public void setABoolean(boolean p){this.aBoolean = p; }",
            "public void setBytes(byte[] p){this.bytes = p; }",
            "public void setADate(java.util.Date p){this.aDate = p; }",
            "}"
        );

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(file)
        .processedWith(new ObjectMappableProcessor())
        .compilesWithoutError();
  }

}
