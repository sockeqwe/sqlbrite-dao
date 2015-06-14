package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class FromTest {

	@Test
	public void test() {

		for (int i = 0; i < 10; i++) {
			String table = "table" + i;

			assertEquals(" FROM " + table, new FROM(null, table).asCompileableStatement().sql);

		}

	}

}
