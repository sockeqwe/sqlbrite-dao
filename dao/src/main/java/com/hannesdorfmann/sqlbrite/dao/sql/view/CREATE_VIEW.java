package com.hannesdorfmann.sqlbrite.dao.sql.view;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlRootNode;
import com.hannesdorfmann.sqlbrite.dao.sql.select.SELECT;
import com.hannesdorfmann.sqlbrite.dao.sql.select.SqlCompileableSelectChildNode;

/**
 * This represents a SQL <code>CREATE VIEW ViewName </code>
 * 
 * @author Hannes Dorfmann
 * 
 */
public class CREATE_VIEW extends SqlRootNode {

	private final String sql;

	public CREATE_VIEW(String viewName) {
		sql = "CREATE VIEW " + viewName;
	}

	@Override
	public String getSql() {
		return sql;
	}

	/**
	 * Creates a AS followed by a {@link SELECT} statement
	 * 
	 * @param selectChild
	 * @return
	 */
	public AS AS(SqlCompileableSelectChildNode selectChild) {
		return new AS(this, selectChild);

	}

}
