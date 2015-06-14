package com.hannesdorfmann.sqlbrite.dao.sql.alter;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * <code> ALTER TABLE ADD COLUMN columnDefinition</code>
 * 
 * @author Hannes Dorfmann
 * 
 */
public class ADD_COLUMN extends SqlExecuteCompileableChildNode {

	private final String sql;

	public ADD_COLUMN(SqlNode previous, String columnDef) {
		super(previous);
		this.sql = " ADD COLUMN " + columnDef;
	}

	@Override
	public String getSql() {
		return sql;
	}

}
