package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * Adds a GROUP BY
 *
 * @author Hannes Dorfmann
 *
 */
public class GROUP_BY extends SqlCursorCompileableChildNode implements
		SqlCompileableSelectChildNode {

	private final String groupBy;

	public GROUP_BY(SqlNode previous, String groupBy) {
		super(previous);
		this.groupBy = groupBy;
	}

	@Override
	public String getSql() {
		return " GROUP BY " + groupBy;
	}

	/**
	 * Adds a SQL ORDER BY
	 *
	 * @param orderBy the order criteria
   * @return {@link ORDER_BY}
	 */
	public ORDER_BY ORDER_BY(String orderBy) {
		return new ORDER_BY(this, orderBy);
	}

	/**
	 * Adds a SQL HAVING clause
	 *
	 * @param having the having criteria
	 *
   * @return {@link HAVING}
	 */
	public HAVING HAVING(String having) {
		return new HAVING(this, having);
	}

	/**
	 * Adds a SQL LIMIT clause
	 *
	 * @param limit the limit
	 *
   * @return {@link LIMIT}
	 */
	public LIMIT LIMIT(String limit) {
		return new LIMIT(this, limit);
	}

}
