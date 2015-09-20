package com.hannesdorfmann.sqlbrite.dao.sql.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DropTableIfExistsTest {

  @Test public void test() {

    for (int j = 0; j < 50; j++) {
      String tableName = "table" + j;

      assertEquals("DROP TABLE IF EXISTS " + tableName,
          new DROP_TABLE_IF_EXISTS(tableName).asCompileableStatement().sql);
    }
  }
}
