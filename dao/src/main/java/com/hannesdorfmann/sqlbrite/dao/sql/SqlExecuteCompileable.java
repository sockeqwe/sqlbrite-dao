package com.hannesdorfmann.sqlbrite.dao.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Makes the sql statment be executed by {@link SQLiteDatabase#execSQL(String)}.
 * Execute a single SQL statement that is NOT a SELECT or any other SQL
 * statement that returns data.
 *
 * @author Hannes Dorfmann
 */
public interface SqlExecuteCompileable extends SqlCompileable {

  /**
   * Execute a single SQL statement that is NOT a SELECT or any other SQL
   * statement that returns data.
   *
   * @throws SQLException
   */
  void execute(SQLiteDatabase database) throws SQLException;
}
