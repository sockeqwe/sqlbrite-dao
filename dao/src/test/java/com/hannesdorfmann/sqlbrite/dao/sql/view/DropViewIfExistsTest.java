package com.hannesdorfmann.sqlbrite.dao.sql.view;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DropViewIfExistsTest {

  @Test public void test() {

    for (int j = 0; j < 50; j++) {
      String viewName = "view" + j;

      assertEquals("DROP VIEW IF EXISTS " + viewName,
          new DROP_VIEW_IF_EXISTS(viewName).asCompileableStatement().sql);
    }
  }
}
