package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class HungarianNotationTest {

  @Test public void removeSetter() {

    Assert.assertEquals("lastname", HungarianNotation.removeNotationFromSetterAndSetPrefix(
        "setmLastname"));
    Assert.assertEquals("lastname", HungarianNotation.removeNotationFromSetterAndSetPrefix(
        "setMLastname"));
    Assert.assertEquals("lastName", HungarianNotation.removeNotationFromSetterAndSetPrefix(
        "setMLastName"));
    Assert.assertEquals("foo", HungarianNotation.removeNotationFromSetterAndSetPrefix("mFoo"));

    Assert.assertEquals("Bar", HungarianNotation.removeNotationFromSetterAndSetPrefix("setBar"));
    Assert.assertEquals("asd", HungarianNotation.removeNotationFromSetterAndSetPrefix("asd"));
    Assert.assertEquals("Asd", HungarianNotation.removeNotationFromSetterAndSetPrefix("Asd"));

    Assert.assertEquals("B", HungarianNotation.removeNotationFromSetterAndSetPrefix("B"));
    Assert.assertEquals("c", HungarianNotation.removeNotationFromSetterAndSetPrefix("c"));
  }



}
