package com.hannesdorfmann.sqlbrite.dao.sql;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * The abstract implementation of a {@link SqlExecuteCompileable}
 * 
 * @author Hannes Dorfmann
 * 
 */
public abstract class SqlExecuteCompileableChildNode extends
		SqlCompileableChildNode implements SqlExecuteCompileable {

	public SqlExecuteCompileableChildNode(SqlNode previous) {
		super(previous);
	}

	@Override
	public void execute(SQLiteDatabase database) throws SQLException {
		database.execSQL(asCompileableStatement().sql);
	}

}
