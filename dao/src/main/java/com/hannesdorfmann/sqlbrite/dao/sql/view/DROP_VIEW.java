package com.hannesdorfmann.sqlbrite.dao.sql.view;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileable;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;

/**
 * DROP a SQL VIEW
 *
 * @author Hannes Dorfmann
 */
public class DROP_VIEW extends SqlRootNode implements SqlExecuteCompileable {

  private final String sql;

  public DROP_VIEW(String viewName) {
    if (viewName == null) throw new IllegalArgumentException("The VIEWs name is null");

    sql = "DROP VIEW " + viewName;
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
