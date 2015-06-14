package com.hannesdorfmann.sqlbrite.dao.sql.alter;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;

/**
 * Edit a SQL TABLE by using ALTER TABLE command
 * 
 * @author Hannes Dorfmann
 * 
 */
public class ALTER_TABLE extends SqlRootNode {

	private final String sql;

	public ALTER_TABLE(String tableName) {
		if (tableName == null)
			throw new IllegalArgumentException("The table name is null");

		this.sql = "ALTER TABLE " + tableName;
	}

	@Override
	public String getSql() {
		return sql;
	}

	/**
	 * Rename a Sql Table
	 * 
	 * @param newTableName
	 * @return
	 */
	public RENAME_TO RENAME_TO(String newTableName) {
		return new RENAME_TO(this, newTableName);
	}

	/**
	 * Add a column to the table
	 * 
	 * @param columnDef
	 * @return
	 */
	public ADD_COLUMN ADD_COLUMN(String columnDef) {
		return new ADD_COLUMN(this, columnDef);
	}

}
