package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;
import java.util.Collections;
import java.util.Set;

/**
 * A sql NATURAL LEFT OUTER JOIN
 *
 * @author Hannes Dorfmann
 */
public class NATURAL_LEFT_OUTER_JOIN extends SqlCursorCompileableChildNode {

  private final String sql;
  private final Set<String> affectedTables;

  public NATURAL_LEFT_OUTER_JOIN(SqlNode previous, String tableToJoin) {
    super(previous);
    sql = " NATURAL LEFT OUTER JOIN " + tableToJoin;
    this.affectedTables = Collections.singleton(tableToJoin);
  }

  @Override public Set<String> getAffectedTables() {
    return affectedTables;
  }

  @Override public String getSql() {
    return sql;
  }

  /**
   * Adds a SQL WHERE clause
   */
  public WHERE WHERE(String condition) {
    return new WHERE(this, condition);
  }

  /**
   * Adds a SQL ORDER BY
   */
  public ORDER_BY ORDER_BY(String orderBy) {
    return new ORDER_BY(this, orderBy);
  }

  /**
   * Adds a SQL GROUP_BY
   */
  public GROUP_BY GROUP_BY(String groupBy) {
    return new GROUP_BY(this, groupBy);
  }

  /**
   * Add a SQL HAVING clause
   */
  public HAVING HAVING(String having) {
    return new HAVING(this, having);
  }

  /**
   * Adds a SQL LIMIT clause
   */
  public LIMIT LIMIT(String limit) {
    return new LIMIT(this, limit);
  }

  /**
   * Add a NATURAL LEFT OUTER JOIN
   */
  public NATURAL_LEFT_OUTER_JOIN NATURAL_LEFT_OUTER_JOIN(String tableToJoin) {
    return new NATURAL_LEFT_OUTER_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL LEFT JOIN
   */
  public NATURAL_LEFT_JOIN NATURAL_LEFT_JOIN(String tableToJoin) {
    return new NATURAL_LEFT_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL INNER JOIN
   */
  public NATURAL_INNER_JOIN NATURAL_INNER_JOIN(String tableToJoin) {
    return new NATURAL_INNER_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL CROSS JOIN
   */
  public NATURAL_CROSS_JOIN NATURAL_CROSS_JOIN(String tableToJoin) {
    return new NATURAL_CROSS_JOIN(this, tableToJoin);
  }

  /**
   * Add a LEFT OUTER JOIN
   */
  public LEFT_OUTER_JOIN LEFT_OUTER_JOIN(String tableToJoin) {
    return new LEFT_OUTER_JOIN(this, tableToJoin);
  }

  /**
   * Add a INNER JOIN
   */
  public INNER_JOIN INNER_JOIN(String tableToJoin) {
    return new INNER_JOIN(this, tableToJoin);
  }

  /**
   *
   * @param tableToJoin
   * @return
   */
  public CROSS_JOIN CROSS_JOIN(String tableToJoin) {
    return new CROSS_JOIN(this, tableToJoin);
  }
}
