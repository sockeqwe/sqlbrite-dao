package com.hannesdorfmann.sqlbrite.dao.sql.table;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateTableIfNotExistsTest {

	private String colDef(int i) {
		switch (i % 4) {
		case 0:
			return "col" + i + " INTEGER NOT NULL";

		case 1:
			return "col" + i + " TEXT NOT NULL";

		case 2:
			return "col" + i + " DOUBLE NULL";

		case 3:
			return "col" + i + " BLOB NULL";
		default:
			return null;
		}
	}

	@Test
	public void test() {

		for (int j = 0; j < 10; j++) {
			String tableName = "table" + j;
			int cols = 8;
			String[] columns = new String[cols];
			StringBuilder b = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
			b.append(tableName);
			b.append(" ( ");

			for (int i = 0; i < cols; i++) {
				String colDef = colDef(i);

				columns[i] = colDef;

				b.append(colDef);

				if (i < cols - 1)
					b.append(", ");

			}

			b.append(" )");

			assertEquals(b.toString(), new CREATE_TABLE_IF_NOT_EXISTS(
					tableName, columns).asCompileableStatement().sql);
		}

	}
}
