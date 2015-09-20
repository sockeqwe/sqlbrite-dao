package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LimitTest {

  @Test public void test() {

    for (int i = 0; i < 10; i++) {
      String limit = "" + i + ", 100";

      assertEquals(" LIMIT " + limit, new LIMIT(null, limit).asCompileableStatement().sql);
    }
  }
}
