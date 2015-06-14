package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * Add a LIMIT to SQL
 *
 * @author Hannes Dorfmann
 */
public class LIMIT extends SqlCursorCompileableChildNode implements SqlCompileableSelectChildNode {

  private final String limit;

  public LIMIT(SqlNode previous, String limit) {
    super(previous);
    this.limit = limit;
  }

  @Override public String getSql() {
    return " LIMIT " + limit;
  }
}
