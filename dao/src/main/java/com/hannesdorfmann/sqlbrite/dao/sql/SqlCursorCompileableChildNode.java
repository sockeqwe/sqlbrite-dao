package com.hannesdorfmann.sqlbrite.dao.sql;

/**
 * The basic implementation of a {@link SqlFinishedStatement}
 *
 * @author Hannes Dorfmann
 */
public abstract class SqlCursorCompileableChildNode extends SqlCompileableChildNode
    implements SqlFinishedStatement {

  public SqlCursorCompileableChildNode(SqlNode previous) {
    super(previous);
  }
}
