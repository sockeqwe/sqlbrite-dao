package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * A simple SQL WHERE condition
 * 
 * @author Hannes Dorfmann
 * 
 */
public class WHERE extends SqlCursorCompileableChildNode implements
		SqlCompileableSelectChildNode {

	private final String condition;

	public WHERE(SqlNode previous, String condition) {
		super(previous);
		this.condition = condition;
	}

	@Override
	public String getSql() {
		return " WHERE " + condition;
	}

	/**
	 * Add an SQL ORDER BY statement
	 * 
	 * @param orderBy
	 * @return
	 */
	public ORDER_BY ORDER_BY(String orderBy) {
		return new ORDER_BY(this, orderBy);
	}

	/**
	 * Add a SQL GROUP BY
	 * 
	 * @param groupBy
	 * @return
	 */
	public GROUP_BY GROUP_BY(String groupBy) {
		return new GROUP_BY(this, groupBy);
	}

	/**
	 * Adds a SQL HAVING
	 * 
	 * @param having
	 * @return
	 */
	public HAVING HAVING(String having) {
		return new HAVING(this, having);
	}

	/**
	 * Adds a SQL LIMIT clause
	 * 
	 * @param limit
	 * @return
	 */
	public LIMIT LIMIT(String limit) {
		return new LIMIT(this, limit);
	}
}
