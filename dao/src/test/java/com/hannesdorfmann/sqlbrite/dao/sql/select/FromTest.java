package com.hannesdorfmann.sqlbrite.dao.sql.select;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCompileable;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FromTest {

  @Test public void test() {
    String table = "table";
    SqlCompileable.CompileableStatement compiled = new FROM(null, table).asCompileableStatement();
    assertEquals(" FROM " + table, compiled.sql);

    Set<String> observableTables = new HashSet<>();
    observableTables.add(table);
    assertEquals(compiled.tables, observableTables);
  }

  @Test public void multipleTables() {
    String table1 = "table1";
    String table2 = "table2";
    String table3 = "table3";

    SqlCompileable.CompileableStatement compiled =
        new FROM(null, table1, table2, table3).asCompileableStatement();
    assertEquals(" FROM " + table1 + ", " + table2 + ", " + table3, compiled.sql);

    Set<String> observableTables = new HashSet<>();
    observableTables.add(table1);
    observableTables.add(table2);
    observableTables.add(table3);
    assertEquals(compiled.tables, observableTables);
  }
}
