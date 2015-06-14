package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;
import java.util.Collections;
import java.util.Set;

/**
 * A sql NATURAL LEFT OUTER JOIN
 *
 * @author Hannes Dorfmann
 */
public class CROSS_JOIN extends SqlChildNode {

  private final String sql;
  private final Set<String> affectedTables;

  public CROSS_JOIN(SqlNode previous, String tableToJoin) {
    super(previous);
    sql = " CROSS JOIN " + tableToJoin;
    affectedTables = Collections.singleton(tableToJoin);
  }

  @Override public String getSql() {
    return sql;
  }

  /**
   * The ON part.
   *
   * @param columns Example:
   * <code> "table1.col1 = table2.col1 AND table1.col2 = table2.col2" </code>
   * @return ON
   */
  public ON ON(String columns) {
    return new ON(this, columns);
  }

  @Override public Set<String> getAffectedTables() {
    return affectedTables;
  }
}


