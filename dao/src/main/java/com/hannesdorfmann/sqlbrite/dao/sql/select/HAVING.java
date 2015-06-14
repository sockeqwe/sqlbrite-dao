package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * The SQL HAVING clause
 *
 * @author Hannes Dorfmann
 */
public class HAVING extends SqlCursorCompileableChildNode implements SqlCompileableSelectChildNode {

  private final String having;

  public HAVING(SqlNode previous, String having) {
    super(previous);
    this.having = having;
  }

  @Override public String getSql() {
    return " HAVING " + having;
  }

  /**
   * Adds a ORDER BY
   *
   * @param orderBy The order by criteria
   * @return {@link ORDER_BY}
   */
  public ORDER_BY ORDER_BY(String orderBy) {
    return new ORDER_BY(this, orderBy);
  }

  /**
   * Adds a LIMIT clause
   *
   * @param limit the limit clause
   * @return {@link LIMIT}
   */
  public LIMIT LIMIT(String limit) {
    return new LIMIT(this, limit);
  }
}
