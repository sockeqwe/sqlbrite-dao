package com.hannesdorfmann.sqlbrite.dao.sql.alter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlterTableTest {

  private String colDef(int i) {
    switch (i % 4) {
      case 0:
        return "col" + i + " INTEGER NOT NULL";

      case 1:
        return "col" + i + " TEXT NOT NULL";

      case 2:
        return "col" + i + " DOUBLE NULL";

      case 3:
        return "col" + i + " BLOB NULL";
      default:
        return null;
    }
  }

  @Test public void test() {

    for (int j = 0; j < 10; j++) {
      String tableName = "TableName" + j;

      assertEquals("ALTER TABLE " + tableName, new ALTER_TABLE(tableName).getSql());
    }
  }

  @Test public void testAlterTableRenameTo() {
    for (int j = 0; j < 10; j++) {
      String tableName = "TableName" + j;
      String newName = "NewTableName" + j;

      assertEquals("ALTER TABLE " + tableName + " RENAME TO " + newName,
          new ALTER_TABLE(tableName).RENAME_TO(newName).asCompileableStatement().sql);
    }
  }

  @Test public void testAlterTableAddColumn() {
    for (int j = 0; j < 10; j++) {
      String tableName = "TableName" + j;
      String colDef = colDef(j);

      assertEquals("ALTER TABLE " + tableName + " ADD COLUMN " + colDef,
          new ALTER_TABLE(tableName).ADD_COLUMN(colDef).asCompileableStatement().sql);
    }
  }
}
