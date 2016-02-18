package com.hannesdorfmann.sqlbrite.dao.sql;

import java.util.Set;

/**
 * This is the public interface to compile {@link SqlChildNode}s to a fully sql
 * string
 *
 * @author Hannes Dorfmann
 */
public interface SqlCompileable {

  class CompileableStatement {
    public String sql;
    public Set<String> tables;

    public CompileableStatement(String sql, Set<String> tables) {
      this.sql = sql;
      this.tables = tables;
    }
  }

  CompileableStatement asCompileableStatement();
}
