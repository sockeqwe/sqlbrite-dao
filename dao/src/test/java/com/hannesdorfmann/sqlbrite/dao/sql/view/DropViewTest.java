package com.hannesdorfmann.sqlbrite.dao.sql.view;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DropViewTest {

  @Test public void test() {

    for (int j = 0; j < 50; j++) {
      String viewName = "view" + j;

      assertEquals("DROP VIEW " + viewName, new DROP_VIEW(viewName).asCompileableStatement().sql);
    }
  }
}
