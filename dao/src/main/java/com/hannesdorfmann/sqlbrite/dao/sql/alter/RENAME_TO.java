package com.hannesdorfmann.sqlbrite.dao.sql.alter;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlExecuteCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * This is a RENAME TO statement for a ALTER TABLE.
 * <p>
 * <code>
 * ALTER TABLE MyDatabase.MyTable RENAME TO MyDatabase.NewTableName
 * </code>
 * </p>
 * 
 * @author Hannes Dorfmann
 * 
 */
public class RENAME_TO extends SqlExecuteCompileableChildNode {

	private final String sql;

	public RENAME_TO(SqlNode previous, String renameTo) {
		super(previous);
		this.sql = " RENAME TO " + renameTo;
	}

	@Override
	public String getSql() {
		return sql;
	}
}
