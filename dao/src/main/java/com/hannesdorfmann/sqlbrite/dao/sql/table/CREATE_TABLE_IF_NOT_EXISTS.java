package com.hannesdorfmann.sqlbrite.dao.sql.table;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileable;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;

/**
 * Create a new SQL TABLE
 *
 * @author Hannes Dorfmann
 */
public class CREATE_TABLE_IF_NOT_EXISTS extends SqlRootNode implements SqlExecuteCompileable {

  private final String sql;

  public CREATE_TABLE_IF_NOT_EXISTS(String tableName, String... columns) {

    if (tableName == null) throw new IllegalArgumentException("The table name is null");

    if (columns.length == 0) {
      throw new IllegalArgumentException("At least on column definition is required");
    }

    StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    builder.append(tableName);
    builder.append(" ( ");

    for (int i = 0; i < columns.length; i++) {

      builder.append(columns[i]);

      if (i != (columns.length - 1)) builder.append(", ");
    }
    builder.append(" )");
    sql = builder.toString();
  }

  @Override public String getSql() {
    return sql;
  }

  @Override public CompileableStatement asCompileableStatement() {
    return new CompileableStatement(sql, null);
  }

  @Override public void execute(SQLiteDatabase database) throws SQLException {
    database.execSQL(asCompileableStatement().sql);
  }
}
