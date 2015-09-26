package com.hannesdorfmann.sqlbrite.objectmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate fields with this annotation to mark them as mappable from cursor to
 *
 * @author Hannes Dorfmann
 */
@Retention(RetentionPolicy.SOURCE) @Documented @Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Column {

  /**
   * Set the column name (without table prefix).
   * See http://androidxref.com/5.1.0_r1/xref/frameworks/base/core/java/android/database/AbstractCursor.java
   * line 275
   *
   * @return The column name
   */
  String value();

  /**
   * Throw an exception if the column name has not been in the sql cursor
   *
   * @return true if an exception should be thrown, false if not
   */
  boolean throwOnColumnIndexNotFound() default true;
}
