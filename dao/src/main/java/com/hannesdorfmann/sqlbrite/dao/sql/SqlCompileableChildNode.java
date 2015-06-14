package com.hannesdorfmann.sqlbrite.dao.sql;

import java.util.HashSet;
import java.util.Set;

public abstract class SqlCompileableChildNode extends SqlChildNode implements
		SqlCompileable {

	public SqlCompileableChildNode(SqlNode previous) {
		super(previous);
	}

	@Override
	public CompileableStatement asCompileableStatement() {

		StringBuilder builder = new StringBuilder(getSql());


    Set<String> affectedTables = new HashSet<>();
    Set<String> tables = getAffectedTables();
    if (tables != null){
      affectedTables.addAll(tables);
    }


    SqlNode pre = previous;

		// iterate over the SqlNodes
		while (pre != null) {
      tables = pre.getAffectedTables();
      if (tables != null){
        affectedTables.addAll(tables);
      }
			pre.buildSql(builder);
			pre = pre.getPrevious();
		}

		return new CompileableStatement(builder.toString(), affectedTables);
	}

}
