package com.hannesdorfmann.sqlbrite.dao.sql.select;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelectTest {

  public static class Columns {
    public String sql;
    public String[] columns;

    public Columns(int count) {
      createColumns(count);
    }

    private void createColumns(int count) {

      String[] columns = new String[count];
      StringBuilder b = new StringBuilder("SELECT ");

      for (int i = 0; i < count; i++) {

        String colName = "col" + i;

        columns[i] = colName;

        b.append(colName);
        if (i < count - 1) b.append(", ");
      }

      String sqlBuilder = b.toString();
      this.columns = columns;
      this.sql = sqlBuilder;
    }
  }

  @Test public void testSingleColumn() {

    int tests = 10;

    for (int i = 0; i < tests; i++) {
      String colName = "col" + i;
      assertEquals("SELECT " + colName, new SELECT(colName).getSql());
    }
  }

  @Test public void testColumns() {

    Columns col = new Columns(20);
    String selectSql = new SELECT(col.columns).getSql();
    assertEquals(col.sql, selectSql);
  }

  @Test public void testSelectFrom() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;

      String sql = new SELECT(c.columns).FROM(tableName).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhere() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";

      String sql =
          new SELECT(c.columns).FROM(tableName).WHERE(whereClause).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " WHERE " + whereClause;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereOrderBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String orderBy = "col" + i + " ASC";

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .ORDER_BY(orderBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " ORDER BY " + orderBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromOrderBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String orderBy = "col" + i + " ASC";

      String sql =
          new SELECT(c.columns).FROM(tableName).ORDER_BY(orderBy).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " ORDER BY " + orderBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String groupBy = "col" + i;

      String sql =
          new SELECT(c.columns).FROM(tableName).GROUP_BY(groupBy).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromNaturlaLeftOuterJoinWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;

      String sql = new SELECT(c.columns).FROM(tableName)
          .NATURAL_LEFT_OUTER_JOIN(tableToJoin)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " NATURAL LEFT OUTER JOIN " + tableToJoin + " WHERE "
              + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromNaturlaLeftJoinWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;

      String sql = new SELECT(c.columns).FROM(tableName)
          .NATURAL_LEFT_JOIN(tableToJoin)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " NATURAL LEFT JOIN " + tableToJoin + " WHERE "
          + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromNaturalInnerJoinWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;

      String sql = new SELECT(c.columns).FROM(tableName)
          .NATURAL_INNER_JOIN(tableToJoin)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " NATURAL INNER JOIN " + tableToJoin + " WHERE "
              + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromNaturalCrossJoinWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;

      String sql = new SELECT(c.columns).FROM(tableName)
          .NATURAL_CROSS_JOIN(tableToJoin)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " NATURAL CROSS JOIN " + tableToJoin + " WHERE "
              + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromLeftOuterJoinOnWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;
      String onClause = "table" + i + ".col" + i + "=table" + (i + 1) + ".col" + (i + 1);

      String sql = new SELECT(c.columns).FROM(tableName)
          .LEFT_OUTER_JOIN(tableToJoin)
          .ON(onClause)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " LEFT OUTER JOIN " + tableToJoin + " ON " + onClause
              + " WHERE " + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromInnerJoinOnWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;
      String onClause = "table" + i + ".col" + i + "=table" + (i + 1) + ".col" + (i + 1);

      String sql = new SELECT(c.columns).FROM(tableName)
          .INNER_JOIN(tableToJoin)
          .ON(onClause)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " INNER JOIN " + tableToJoin + " ON " + onClause
              + " WHERE " + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromCrossJoinOnWhereGroupBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String tableToJoin = "TableName" + i;
      String onClause = "table" + i + ".col" + i + "=table" + (i + 1) + ".col" + (i + 1);

      String sql = new SELECT(c.columns).FROM(tableName)
          .CROSS_JOIN(tableToJoin)
          .ON(onClause)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " CROSS JOIN " + tableToJoin + " ON " + onClause
              + " WHERE " + whereClause + " GROUP BY " + groupBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromHaving() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String having = "sum(col" + i + ") > " + (i * 100);

      String sql =
          new SELECT(c.columns).FROM(tableName).HAVING(having).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " HAVING " + having;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupByHaving() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String having = "sum(col" + i + ") > " + (i * 100);

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .HAVING(having)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy
              + " HAVING " + having;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupByHavingOrderBy() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String having = "sum(col" + i + ") > " + (i * 100);
      String orderBy = "col" + i + " DESC";

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .HAVING(having)
          .ORDER_BY(orderBy)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy
              + " HAVING " + having + " ORDER BY " + orderBy;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromLimit() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String limit = "" + i + ", " + (100 + i);

      String sql = new SELECT(c.columns).FROM(tableName).LIMIT(limit).asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereLimit() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String limit = "" + i + ", " + (100 + i);

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .LIMIT(limit)
          .asCompileableStatement().sql;
      String genSql = c.sql + " FROM " + tableName + " WHERE " + whereClause + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupByLimit() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String limit = "" + i + ", " + (100 + i);

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .LIMIT(limit)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy
              + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupByHavingLimit() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String having = "sum(col" + i + ") > " + (i * 100);
      String limit = "" + i + ", " + (10 + i);

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .HAVING(having)
          .LIMIT(limit)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy
              + " HAVING " + having + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }

  @Test public void testSelectFromWhereGroupByHavingOrderByLimit() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      Columns c = new Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String having = "sum(col" + i + ") > " + (i * 100);
      String limit = "" + i + ", " + (10 + i);
      String orderBy = "col" + i + " DESC";

      String sql = new SELECT(c.columns).FROM(tableName)
          .WHERE(whereClause)
          .GROUP_BY(groupBy)
          .HAVING(having)
          .ORDER_BY(orderBy)
          .LIMIT(limit)
          .asCompileableStatement().sql;
      String genSql =
          c.sql + " FROM " + tableName + " WHERE " + whereClause + " GROUP BY " + groupBy
              + " HAVING " + having + " ORDER BY " + orderBy + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }
}
