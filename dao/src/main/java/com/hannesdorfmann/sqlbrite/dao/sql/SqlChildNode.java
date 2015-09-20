package com.hannesdorfmann.sqlbrite.dao.sql;

import java.util.Set;

/**
 * This is a simple abstract implementation of {@link SqlChildNode}
 *
 * @author Hannes Dorfmann
 */
public abstract class SqlChildNode extends SqlRootNode implements SqlNode {

  protected final SqlNode previous;

  public SqlChildNode(SqlNode previous) {
    this.previous = previous;
  }

  @Override public SqlNode getPrevious() {
    return previous;
  }

  @Override public Set<String> getAffectedTables() {
    return null;
  }
}
