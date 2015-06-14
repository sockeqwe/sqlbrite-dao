package com.hannesdorfmann.sqlbrite.dao.sql.view;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileable;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;

/**
 * DROP a SQL VIEW IF EXISTS
 *
 * @author Hannes Dorfmann
 */
public class DROP_VIEW_IF_EXISTS extends SqlRootNode implements SqlExecuteCompileable {

  private final String sql;

  public DROP_VIEW_IF_EXISTS(String viewName) {
    if (viewName == null) throw new IllegalArgumentException("The VIEWs name is null");

    sql = "DROP VIEW IF EXISTS " + viewName;
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
