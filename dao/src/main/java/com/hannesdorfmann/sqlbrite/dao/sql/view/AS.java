package com.hannesdorfmann.sqlbrite.dao.sql.view;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;
import com.hannesdorfmann.sqlbrite.dao.sql.select.SqlCompileableSelectChildNode;
import java.util.HashSet;
import java.util.Set;

/**
 * This one is used in combination with {@link CREATE_VIEW} or
 * {@link CREATE_VIEW_IF_NOT_EXISTS}
 *
 * @author Hannes Dorfmann
 */
public class AS extends SqlExecuteCompileableChildNode {

  private final SqlCompileableSelectChildNode selectChild;

  public AS(SqlNode previous, SqlCompileableSelectChildNode selectChild) {
    super(previous);
    this.selectChild = selectChild;
  }

  @Override public String getSql() {
    return " AS ";
  }

  @Override public CompileableStatement asCompileableStatement() {
    CompileableStatement superCompileableStatement = super.asCompileableStatement();
    CompileableStatement selectChildCompileableStatement = selectChild.asCompileableStatement();

    Set<String> affectedTabels = new HashSet<>();
    if (superCompileableStatement.tables != null) {
      affectedTabels.addAll(superCompileableStatement.tables);
    }

    if (selectChildCompileableStatement.tables != null) {
      affectedTabels.addAll(selectChildCompileableStatement.tables);
    }

    return new CompileableStatement(
        superCompileableStatement.sql + selectChildCompileableStatement.sql,
        affectedTabels.isEmpty() ? null : affectedTabels);
  }
}
