package com.hannesdorfmann.sqlbrite.dao.sql.view;

import com.hannesdorfmann.sqlbrite.dao.sql.select.SelectTest;
import com.hannesdorfmann.sqlbrite.dao.sql.select.SELECT;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateViewTest {

  @Test public void test() {

    for (int j = 0; j < 10; j++) {
      String viewName = "TableName" + j;

      assertEquals("CREATE VIEW " + viewName, new CREATE_VIEW(viewName).getSql());
    }
  }

  @Test public void testCreateViewAsSelect() {

    int tests = 100;
    int columns = 5;

    for (int i = 0; i < tests; i++) {

      String viewName = "MyView" + i;

      SelectTest.Columns c = new SelectTest.Columns(columns);
      String tableName = "table" + i;
      String whereClause = "col1=val1 AND col2=val2";
      String groupBy = "col" + i;
      String having = "sum(col" + i + ") > " + (i * 100);
      String limit = "" + i + ", " + (10 + i);
      String orderBy = "col" + i + " DESC";

      String sql = new CREATE_VIEW(viewName).AS(new SELECT(c.columns).FROM(tableName)
              .WHERE(whereClause)
              .GROUP_BY(groupBy)
              .HAVING(having)
              .ORDER_BY(orderBy)
              .LIMIT(limit)).asCompileableStatement().sql;

      String genSql = "CREATE VIEW " + viewName + " AS " + c.sql + " FROM " + tableName + " WHERE "
          + whereClause + " GROUP BY " + groupBy + " HAVING " + having + " ORDER BY " + orderBy
          + " LIMIT " + limit;

      assertEquals(sql, genSql);
    }
  }
}
