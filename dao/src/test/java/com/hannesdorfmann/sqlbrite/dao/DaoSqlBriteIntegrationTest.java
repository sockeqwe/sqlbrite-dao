package com.hannesdorfmann.sqlbrite.dao;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCompileable;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlFinishedStatement;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */

@PrepareForTest(BriteDatabase.class) @RunWith(PowerMockRunner.class)
public class DaoSqlBriteIntegrationTest {

  private UserDao userDao = new UserDao();

  @Test public void runStatementQuery() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    List<String> argsList = Arrays.asList(arg1, arg2);

    SqlFinishedStatement statement = userDao.SELECT("col1").FROM(table);
    SqlCompileable.CompileableStatement compileableStatement = statement.asCompileableStatement();

    userDao.query(statement).args(arg1, arg2).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(compileableStatement.tables), Mockito.eq(compileableStatement.sql),
            varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runStatementQueryNoAutoUpdates() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> emptySet = Collections.emptySet();

    SqlFinishedStatement statement = userDao.SELECT("col1").FROM(table);
    SqlCompileable.CompileableStatement compileableStatement = statement.asCompileableStatement();

    userDao.query(statement).args(arg1, arg2).autoUpdates(false).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(emptySet), Mockito.eq(compileableStatement.sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runRawStatementQueryNoTablesSpecified() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    String sql = "SELECT * FROM " + table;
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> emptySet = Collections.emptySet();

    userDao.rawQuery(sql).args(arg1, arg2).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(emptySet), Mockito.eq(sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runRawStatementQueryWithTables() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    String sql = "SELECT * FROM " + table;
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> tables = Collections.singleton(table);

    userDao.rawQueryOnManyTables(tables, sql).args(arg1, arg2).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(tables), Mockito.eq(sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runRawStatementQueryWithTablesNoAutoUpdates() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    String sql = "SELECT * FROM " + table;
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> tables = Collections.singleton(table);
    Set<String> emptySet = Collections.emptySet();

    userDao.rawQueryOnManyTables(tables, sql).args(arg1, arg2).autoUpdates(false).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(emptySet), Mockito.eq(sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runRawStatementQueryWithSingleTable() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    String sql = "SELECT * FROM " + table;
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> tables = Collections.singleton(table);

    userDao.rawQuery(table, sql).args(arg1, arg2).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(tables), Mockito.eq(sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }

  @Test public void runRawStatementQueryWithSingleTableNoAutoUpdates() {
    BriteDatabase db = PowerMockito.mock(BriteDatabase.class);
    userDao.setSqlBriteDb(db);

    String arg1 = "arg1", arg2 = "arg2";
    String table = "Table";
    String sql = "SELECT * FROM " + table;
    List<String> argsList = Arrays.asList(arg1, arg2);
    Set<String> emptySet = Collections.emptySet();

    userDao.rawQuery(table, sql).args(arg1, arg2).autoUpdates(false).run();

    ArgumentCaptor<String> varArgs = ArgumentCaptor.forClass(String.class);

    QueryObservable query = Mockito.verify(db, Mockito.times(1))
        .createQuery(Mockito.eq(emptySet), Mockito.eq(sql), varArgs.capture());

    Assert.assertEquals(argsList, varArgs.getAllValues());
  }
}
