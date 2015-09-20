package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupByTest {

  @Test public void test() {

    for (int i = 0; i < 10; i++) {
      String condition = "col" + i;
      String sql = " GROUP BY " + condition;

      assertEquals(sql, new GROUP_BY(null, condition).asCompileableStatement().sql);
    }
  }
}
