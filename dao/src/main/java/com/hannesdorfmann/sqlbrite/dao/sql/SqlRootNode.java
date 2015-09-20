package com.hannesdorfmann.sqlbrite.dao.sql;

import java.util.Set;

/**
 * The abstract implementation of the {@link SqlNode}.
 *
 * @author Hannes Dorfmann
 */
public abstract class SqlRootNode implements SqlNode {

  @Override public void buildSql(StringBuilder builder) {
    builder.insert(0, getSql());
  }

  @Override public SqlNode getPrevious() {
    return null;
  }

  @Override public Set<String> getAffectedTables() {
    return null;
  }
}
