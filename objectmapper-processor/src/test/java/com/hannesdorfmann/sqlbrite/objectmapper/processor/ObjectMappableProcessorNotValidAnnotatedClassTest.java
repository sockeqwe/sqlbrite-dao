package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import javax.tools.JavaFileObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests to check if properly annotated classes are processed correctly by
 * {@link ObjectMappableProcessor}. Only put tests in here that produce correctly generated code
 *
 * @author Hannes Dorfmann
 */
public class ObjectMappableProcessorNotValidAnnotatedClassTest {

  /**
   * Tests that fields with min default visibility work as expected
   */
  @Test public void noPublicConstructor() {
    JavaFileObject file =
        JavaFileObjects.forSourceLines("test.PrivateConstructorClass",
            "package test;",
            "",
            "@"+ ObjectMappable.class.getCanonicalName(),
            "public class PrivateConstructorClass {",
            "   @"+ Column.class.getCanonicalName()+"(\"foo\")",
            "   String foo;",
            "",
            "private PrivateConstructorClass(){}",
            "}"
        );

    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(file)
        .processedWith(new ObjectMappableProcessor())
        .failsToCompile()
        .withErrorContaining("Class test.PrivateConstructorClass has Column annotated fields (incl. super class) and therefore must provide an public empty constructor (zero parameters)");
  }

  @Ignore
  @Test public void annotatedInterface() {
    JavaFileObject file =
        JavaFileObjects.forSourceLines("test.InterfaceNotValid",
            "package test;",
            "",
            "@"+ ObjectMappable.class.getCanonicalName(),
            "public interface InterfaceNotValid {}"
        );

    // TODO wtf? Why this doesn't throw a processing exception? Annotating an interface in real life throws this exception, so what's going wrong here?
    Truth.assertAbout(JavaSourceSubjectFactory.javaSource())
        .that(file)
        .processedWith(new ObjectMappableProcessor())
        .failsToCompile()
        .withErrorContaining("test.InterfaceNotValid is annotated with @ObjectMappable but only classes can be annotated with this annotation");

  }
}
