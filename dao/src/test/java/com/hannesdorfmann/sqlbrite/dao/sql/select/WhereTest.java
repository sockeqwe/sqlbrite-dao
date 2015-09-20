package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WhereTest {

  @Test public void test() {

    for (int i = 0; i < 10; i++) {
      String condition = "col" + i + "=value" + i;
      String sql = " WHERE " + condition;

      assertEquals(sql, new WHERE(null, condition).asCompileableStatement().sql);
    }
  }

  public void testOrderBy() {

    for (int i = 0; i < 10; i++) {
      String where = "col" + i + "=val" + i;
      String orderBy = "col" + i + " DESC";
      String sql = "WHERE " + where + " ORDER BY " + orderBy;

      assertEquals(sql, new WHERE(null, where).ORDER_BY(orderBy).asCompileableStatement().sql);
    }
  }
}
