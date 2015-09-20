package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderByTest {

  @Test public void test() {

    for (int i = 0; i < 10; i++) {
      String order = "col" + i + " DESC";
      String sql = " ORDER BY " + order;

      assertEquals(sql, new ORDER_BY(null, order).asCompileableStatement().sql);
    }
  }
}
