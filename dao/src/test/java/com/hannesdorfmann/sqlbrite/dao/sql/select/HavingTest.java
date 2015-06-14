package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HavingTest {

	@Test
	public void test() {

		for (int i = 0; i < 10; i++) {
			String having = "sum(col" + i + ")";

			assertEquals(" HAVING " + having, new HAVING(null, having).asCompileableStatement().sql);

		}

	}

}
