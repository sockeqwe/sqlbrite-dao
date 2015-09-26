package com.hannesdorfmann.sqlbrite.dao.sql.select;

import android.support.annotation.NonNull;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;

/**
 * This is a really simple wrapper
 *
 * @author Hannes Dorfmann
 */
public class SELECT extends SqlRootNode {

  private final String sql;

  /**
   * Create a <code>SELECT column1, column2, ... FROM tableName
   *
   * @param columns The list of columns, pass * for selecting all columns
   */
  public SELECT(@NonNull String... columns) {

    if (columns == null) {
      throw new NullPointerException("SELECT: Columns are null");
    }
    
    if (columns.length == 0) throw new IllegalArgumentException("At least on column is required");

    StringBuilder builder = new StringBuilder("SELECT ");

    for (int i = 0; i < columns.length; i++) {

      builder.append(columns[i]);

      if (i != (columns.length - 1)) builder.append(", ");
    }

    sql = builder.toString();
  }

  @Override public String getSql() {
    return sql;
  }

  /**
   * The sql FROM part to declare the table
   *
   * @param tableName The table name
   * @return FROM
   */
  public FROM FROM(@NonNull String tableName) {
    return new FROM(this, tableName);
  }

  public FROM FROM(@NonNull String... tableNames) {
    return new FROM(this, tableNames);
  }
}
