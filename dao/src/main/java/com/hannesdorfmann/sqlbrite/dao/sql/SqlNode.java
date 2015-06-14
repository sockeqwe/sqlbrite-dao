package com.hannesdorfmann.sqlbrite.dao.sql;

import java.util.Set;

/**
 * This class represents a SQL command node. It will be used to build the SQL
 * string that can be executed on the database
 *
 * @author Hannes Dorfmann
 */
public interface SqlNode {

  /**
   * This method will be called to build the sql string recursively
   *
   * @param builder string builder
   */
  void buildSql(StringBuilder builder);

  /**
   * Get the sql string for this {@link SqlNode}.
   * <p>
   * <b>This method will be called from {@link #buildSql(StringBuilder)}. So
   * don't call this directly.</b>
   * </p>
   *
   * @return A sql string representing this node
   */
  String getSql();

  /**
   * Get the previous {@link SqlChildNode}. This mehtod will be called to
   * build the sql string recursivly. <b>Do not call it directly</b>
   *
   * @return the previous node
   */
  SqlNode getPrevious();

  /**
   * Get the set of tables (table names) that might be queried or changed during executing the sql
   * statement
   *
   * @return A set of string with the table names this sql statement affects
   */
  Set<String> getAffectedTables();
}
