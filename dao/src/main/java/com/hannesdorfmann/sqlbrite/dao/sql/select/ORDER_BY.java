package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * The SQL ORDER BY
 * 
 * @author Hannes Dorfmann
 * 
 */
public class ORDER_BY extends SqlCursorCompileableChildNode implements
		SqlCompileableSelectChildNode {

	private final String order;

	public ORDER_BY(SqlNode prev, String order) {
		super(prev);
		this.order = order;
	}

	@Override
	public String getSql() {
		return " ORDER BY " + order;
	}

	/**
	 * Adds a new LIMIT clause
	 * 
	 * @param limit
	 * @return
	 */
	public LIMIT LIMIT(String limit) {
		return new LIMIT(this, limit);
	}

}
