package com.hannesdorfmann.sqlbrite.dao.sql.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DropTableTest {

  @Test public void test() {

    for (int j = 0; j < 50; j++) {
      String tableName = "table" + j;

      assertEquals("DROP TABLE " + tableName,
          new DROP_TABLE(tableName).asCompileableStatement().sql);
    }
  }
}
