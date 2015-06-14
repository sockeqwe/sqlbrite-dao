package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCursorCompileableChildNode;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlNode;

/**
 * This is used in combination with JOIN
 * 
 * @author Hannes Dorfmann
 * 
 */
public class ON extends SqlCursorCompileableChildNode {

	private final String sql;

	public ON(SqlNode previous, String columns) {
		super(previous);

		sql = " ON " + columns;

	}

	@Override
	public String getSql() {
		return sql;
	}

	/**
	 * Adds a SQL WHERE clause
	 * 
	 * @param condition
	 * @return
	 */
	public WHERE WHERE(String condition) {
		return new WHERE(this, condition);
	}

	/**
	 * Adds a SQL ORDER BY
	 * 
	 * @param orderBy
	 * @return
	 */
	public ORDER_BY ORDER_BY(String orderBy) {
		return new ORDER_BY(this, orderBy);
	}

	/**
	 * Adds a SQL GROUP_BY
	 * 
	 * @param groupBy
	 * @return
	 */
	public GROUP_BY GROUP_BY(String groupBy) {
		return new GROUP_BY(this, groupBy);
	}

	/**
	 * Add a SQL HAVING clause
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

	/**
	 * Add a NATURAL LEFT OUTER JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public NATURAL_LEFT_OUTER_JOIN NATURAL_LEFT_OUTER_JOIN(String tableToJoin) {
		return new NATURAL_LEFT_OUTER_JOIN(this, tableToJoin);
	}

	/**
	 * Add a NATURAL LEFT JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public NATURAL_LEFT_JOIN NATURAL_LEFT_JOIN(String tableToJoin) {
		return new NATURAL_LEFT_JOIN(this, tableToJoin);
	}

	/**
	 * Add a NATURAL INNER JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public NATURAL_INNER_JOIN NATURAL_INNER_JOIN(String tableToJoin) {
		return new NATURAL_INNER_JOIN(this, tableToJoin);
	}

	/**
	 * Add a NATURAL CROSS JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public NATURAL_CROSS_JOIN NATURAL_CROSS_JOIN(String tableToJoin) {
		return new NATURAL_CROSS_JOIN(this, tableToJoin);
	}

	/**
	 * Add a LEFT OUTER JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public LEFT_OUTER_JOIN LEFT_OUTER_JOIN(String tableToJoin) {
		return new LEFT_OUTER_JOIN(this, tableToJoin);
	}

	/**
	 * Add a INNER JOIN
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public INNER_JOIN INNER_JOIN(String tableToJoin) {
		return new INNER_JOIN(this, tableToJoin);
	}

	/**
	 * 
	 * @param tableToJoin
	 * @return
	 */
	public CROSS_JOIN CROSS_JOIN(String tableToJoin) {
		return new CROSS_JOIN(this, tableToJoin);
	}

}
