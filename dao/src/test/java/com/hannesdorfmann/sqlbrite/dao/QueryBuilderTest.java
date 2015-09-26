package com.hannesdorfmann.sqlbrite.dao;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlFinishedStatement;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

 public class QueryBuilderTest {

  private UserDao userDao = new UserDao();

  @Test(expected = NullPointerException.class) public void nullStatementAsInput() {
    userDao.query(null);
    Assert.fail("NullPointerException expected but not thrown");
  }

  @Test(expected = NullPointerException.class) public void nullRawSqlStatementAsInput() {
    userDao.rawQuery(null);
    Assert.fail("NullPointerException expected but not thrown");
  }

  @Test(expected = RuntimeException.class) public void throwOnAutoUpdatesTrueButNoTables() {
    userDao.rawQuery(null, "SELECT * FROM User").autoUpdates(true);
    Assert.fail("autoUpdates(true) should throw an Exception");
  }


  @Test(expected = RuntimeException.class) public void throwOnAutoUpdatesTrue() {
    userDao.rawQuery("SELECT * FROM User").autoUpdates(true);
    Assert.fail("autoUpdates(true) should throw an Exception");
  }

  @Test public void checkValuesFromStatement() {

    SqlFinishedStatement statement = userDao.SELECT("col1").FROM("foo");
    Dao.QueryBuilder builder = userDao.query(statement);

    Assert.assertTrue(builder.statement == statement);
    Assert.assertNull(builder.rawStatement);
    Assert.assertNull(builder.rawStatementAffectedTables);

    Assert.assertTrue(builder.autoUpdate);
    builder.autoUpdates(false);
    Assert.assertFalse(builder.autoUpdate);

    builder.autoUpdates(true);
    Assert.assertTrue(builder.autoUpdate);

    Assert.assertNull(builder.args);
    String[] arguments = { "Foo", "Bar" };
    builder.args(arguments);
    Assert.assertEquals(builder.args, arguments);
  }

  @Test public void checkValuesFromRawStatement() {

    // Builder with no tables to observe (autoUpdates)
    String statement = "SELCET * FROM Foo";
    Dao.QueryBuilder builder = userDao.rawQuery(statement);

    Assert.assertTrue(builder.rawStatement == statement);
    Assert.assertNull(builder.statement);
    Assert.assertNull(builder.rawStatementAffectedTables);
    Assert.assertFalse(builder.autoUpdate);

    // Builder with table to observe (autoUpdates)
    List<String> tables = Arrays.asList("Table1, Table2");
    builder = userDao.rawQueryOnManyTables(tables, statement);

    Assert.assertTrue(builder.rawStatement == statement);
    Assert.assertNull(builder.statement);
    Assert.assertTrue(builder.rawStatementAffectedTables == tables);

    Assert.assertTrue(builder.autoUpdate);
    builder.autoUpdates(false);
    Assert.assertFalse(builder.autoUpdate);

    builder.autoUpdates(true);
    Assert.assertTrue(builder.autoUpdate);

    Assert.assertNull(builder.args);
    String[] arguments = { "Foo", "Bar" };
    builder.args(arguments);
    Assert.assertEquals(builder.args, arguments);
  }
}
