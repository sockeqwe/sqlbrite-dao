package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;
import java.util.Collections;
import java.util.Set;

/**
 * The SQL FROM statement to specify the table which should be queried
 *
 * @author Hannes Dorfmann
 */
public class FROM extends SqlCursorCompileableChildNode implements SqlCompileableSelectChildNode {

  private final String sql;
  private final Set<String> affectedTables;

  public FROM(SqlNode previous, String tableName) {
    super(previous);
    this.sql = " FROM " + tableName;
    this.affectedTables = Collections.singleton(tableName);
  }

  @Override public String getSql() {
    return sql;
  }

  @Override public Set<String> getAffectedTables() {
    return affectedTables;
  }

  /**
   * Adds a SQL WHERE clause
   *
   * @param condition The where clause condition
   * @return {@link WHERE}
   */
  public WHERE WHERE(String condition) {
    return new WHERE(this, condition);
  }


  // TODO find a better String formater lib
  /*
   * Adds a SQL WHERE clause
   * @param condition The where clause that will be formatted with String.forma() so you can use %s if you want to
   * @param args The argument used in String.format() to format the condition
   * @return WHERE

  public WHERE WHERE(String condition, Object ... args){
    return new WHERE(this, String.format(condition, args));
  }
  */

  /**
   * Adds a SQL ORDER BY
   *
   * @param orderBy the order criteria
   * @return {@link ORDER_BY}
   */
  public ORDER_BY ORDER_BY(String orderBy) {
    return new ORDER_BY(this, orderBy);
  }

  /**
   * Adds a SQL GROUP_BY
   *
   * @param groupBy The group by criteria
   * @return {@link GROUP_BY}
   */
  public GROUP_BY GROUP_BY(String groupBy) {
    return new GROUP_BY(this, groupBy);
  }

  /**
   * Add a SQL HAVING clause
   *
   * @param having The having criteria
   * @return {@link HAVING}
   */
  public HAVING HAVING(String having) {
    return new HAVING(this, having);
  }

  /**
   * Adds a SQL LIMIT clause
   *
   * @param limit the limit
   * @return {@link LIMIT}
   */
  public LIMIT LIMIT(String limit) {
    return new LIMIT(this, limit);
  }

  /**
   * Add a NATURAL LEFT OUTER JOIN
   *
   * @param tableToJoin THe table name that you want to join
   * @return {@link NATURAL_LEFT_OUTER_JOIN}
   */
  public NATURAL_LEFT_OUTER_JOIN NATURAL_LEFT_OUTER_JOIN(String tableToJoin) {
    return new NATURAL_LEFT_OUTER_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL LEFT JOIN
   *
   * @param tableToJoin The table to join
   * @return {@link NATURAL_LEFT_JOIN}
   */
  public NATURAL_LEFT_JOIN NATURAL_LEFT_JOIN(String tableToJoin) {
    return new NATURAL_LEFT_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL INNER JOIN.
   *
   * @param tableToJoin the table to join
   * @return {@link NATURAL_INNER_JOIN}
   */
  public NATURAL_INNER_JOIN NATURAL_INNER_JOIN(String tableToJoin) {
    return new NATURAL_INNER_JOIN(this, tableToJoin);
  }

  /**
   * Add a NATURAL CROSS JOIN
   *
   * @param tableToJoin The table name to join
   * @return {@link NATURAL_CROSS_JOIN}
   */
  public NATURAL_CROSS_JOIN NATURAL_CROSS_JOIN(String tableToJoin) {
    return new NATURAL_CROSS_JOIN(this, tableToJoin);
  }

  /**
   * Add a LEFT OUTER JOIN
   *
   * @param tableToJoin the name of the table you want to join
   * @return {@link LEFT_OUTER_JOIN}
   */
  public LEFT_OUTER_JOIN LEFT_OUTER_JOIN(String tableToJoin) {
    return new LEFT_OUTER_JOIN(this, tableToJoin);
  }

  /**
   * Add a INNER JOIN
   *
   * @param tableToJoin the table to join
   * @return {@link INNER_JOIN}
   */
  public INNER_JOIN INNER_JOIN(String tableToJoin) {
    return new INNER_JOIN(this, tableToJoin);
  }

  /**
   * Make a cross join
   *
   * @param tableToJoin The table to join
   * @return {@link CROSS_JOIN}
   */
  public CROSS_JOIN CROSS_JOIN(String tableToJoin) {
    return new CROSS_JOIN(this, tableToJoin);
  }
}
