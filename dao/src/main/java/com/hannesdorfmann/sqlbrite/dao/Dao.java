package com.hannesdorfmann.sqlbrite.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hannesdorfmann.sqlbrite.dao.sql.SqlCompileable;
import com.hannesdorfmann.sqlbrite.dao.sql.SqlFinishedStatement;
import com.hannesdorfmann.sqlbrite.dao.sql.alter.ALTER_TABLE;
import com.hannesdorfmann.sqlbrite.dao.sql.select.SELECT;
import com.hannesdorfmann.sqlbrite.dao.sql.table.CREATE_TABLE;
import com.hannesdorfmann.sqlbrite.dao.sql.table.DROP_TABLE;
import com.hannesdorfmann.sqlbrite.dao.sql.table.DROP_TABLE_IF_EXISTS;
import com.hannesdorfmann.sqlbrite.dao.sql.view.CREATE_VIEW;
import com.hannesdorfmann.sqlbrite.dao.sql.view.CREATE_VIEW_IF_NOT_EXISTS;
import com.hannesdorfmann.sqlbrite.dao.sql.view.DROP_VIEW;
import com.hannesdorfmann.sqlbrite.dao.sql.view.DROP_VIEW_IF_EXISTS;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;

import static com.squareup.sqlbrite.BriteDatabase.Transaction;

/**
 * Data Access Object (DAO).
 *
 * @author Hannes Dorfmann
 */
public abstract class Dao {

  protected BriteDatabase db;
  protected Transaction transaction;

  /**
   * Create here the database table for the given dao
   */
  public abstract void createTable(SQLiteDatabase database);

  /**
   * This method will be called, if a Database has been updated and a database
   * table scheme may be needed
   *
   * @param db the database
   * @param oldVersion old database version
   * @param newVersion new database version
   */
  public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

  /**
   * Set the {@link SQLiteOpenHelper}. This method will be called from the
   * {@link DaoManager} to inject the {@link SQLiteOpenHelper}.
   * <p>
   * You should not call this method directly. Let the {@link DaoManager} do
   * this, because it knows the right moment to invoke this method.
   * </p>
   *
   * @param db the database
   */
  void setSqlBriteDb(BriteDatabase db){
    this.db=db;
  }

  /**
   * Calls {@link Transaction#yieldIfContendedSafely()}
   *
   * @return true or false
   * @see Transaction#yieldIfContendedSafely()
   */
  protected boolean yieldIfContendedSafely() {
    return transaction.yieldIfContendedSafely();
  }

  /**
   * Calls {@link Transaction#yieldIfContendedSafely(long, TimeUnit)}
   *
   * @param sleepAmount Sleep amount
   * @param sleepUnit time unit
   * @return true or false
   * @see Transaction#yieldIfContendedSafely(long, TimeUnit)
   */
  protected boolean yieldIfContendedSafely(long sleepAmount, TimeUnit sleepUnit) {
    return transaction.yieldIfContendedSafely();
  }

  /**
   * Execute a query. Automatically registers itself for updates (trigger)
   *
   * @param statement the sql statement
   * @param args the arguments
   * @return Observable of the query
   */
  protected Observable<SqlBrite.Query> query(SqlFinishedStatement statement, String... args) {
    return query(statement, true, args);
  }

  /**
   * Executes a SQL query
   *
   * @param statement the SQL Statement
   * @param triggerAffectedTableUpdates true, if updates should be triggered automatically, false
   * if
   * no update should be triggered.
   * @param args The arguments of the SQL statement
   * @return Observable of the query
   */
  protected Observable<SqlBrite.Query> query(SqlFinishedStatement statement,
      boolean triggerAffectedTableUpdates, String... args) {

    SqlCompileable.CompileableStatement compileableStatement = statement.asCompileableStatement();
    Set<String> affectedTables =
        compileableStatement.tables == null ? new HashSet<String>() : compileableStatement.tables;

    if (!triggerAffectedTableUpdates) {
      affectedTables = new HashSet<>();
    }

    return rawQuery(affectedTables, compileableStatement.sql, args);
  }

  /**
   * Exceutes a raw query
   *
   * @param tables The affected table updates get triggered if the observer table changes
   * @param sql The sql query statement
   * @param args the sql query args
   * @return Observable of this query
   */
  protected Observable<SqlBrite.Query> rawQuery(@NonNull final Iterable<String> tables,
      @NonNull String sql, @NonNull String... args) {
    return db.createQuery(tables, sql, args);
  }

  /**
   * Creates a raw sql query
   *
   * @param table The affected table updates get triggered if the observer table changes
   * @param sql The sql query statement
   * @param args the sql query args
   * @return Observable of this query
   */
  protected Observable<SqlBrite.Query> rawQuery(@NonNull final String table, @NonNull String sql,
      @NonNull String... args) {
    return db.createQuery(table, sql, args);
  }

  /**
   * Insert a row into the given table
   *
   * @param table the table name
   * @param contentValues The content values
   * @return An observable with the row Id of the new inserted row
   */
  protected Observable<Long> insert(final String table, final ContentValues contentValues) {
    return Observable.just(db.insert(table, contentValues));
  }

  /**
   * Insert a row into the given table
   *
   * @param table the table name
   * @param contentValues The content values
   * @param conflictAlgorithm The conflict algorithm
   * @return An observable with the row Id of the new inserted row
   */
  protected Observable<Long> insert(final String table, final ContentValues contentValues,
      final int conflictAlgorithm) {
    return Observable.just(db.insert(table, contentValues, conflictAlgorithm));
  }

  /**
   * Update rows
   *
   * @param table The table to update
   * @param values The values to update
   * @param whereClause The where clause
   * @param whereArgs The where clause arguments
   * @return An observable containing the number of rows that have been changed by this update
   */
  protected Observable<Integer> update(@NonNull final String table,
      @NonNull final ContentValues values, @Nullable final String whereClause,
      @Nullable final String... whereArgs) {
    return Observable.just(db.update(table, values, whereClause, whereArgs));
  }

  /**
   * Update rows
   *
   * @param table The table to update
   * @param values The values to update
   * @param conflictAlgorithm The conflict algorithm
   * @param whereClause The where clause
   * @param whereArgs The where clause arguments
   * @return An observable containing the number of rows that have been changed by this update
   */
  protected Observable<Integer> update(@NonNull final String table,
      @NonNull final ContentValues values, final int conflictAlgorithm,
      @Nullable final String whereClause, @Nullable final String... whereArgs) {

    return Observable.just(
        db.update(table, values, conflictAlgorithm, whereClause, whereArgs));
  }

  /**
   * Deletes all rows from a table
   *
   * @param table The table to delete
   * @return Observable with the number of deleted rows
   */
  protected Observable<Integer> delete(@NonNull final String table) {
    return delete(table, null);
  }

  /**
   * Delete data from a table
   *
   * @param table The table name
   * @param whereClause the where clause
   * @param whereArgs the where clause arguments
   * @return Observable with the number of deleted rows
   */
  protected Observable<Integer> delete(@NonNull final String table,
      @Nullable final String whereClause, @Nullable final String... whereArgs) {
    return Observable.just(db.delete(table, whereClause, whereArgs));
  }

  /**
   * Start a SELECT query
   *
   * @param columns The columsn to select
   * @return {@link SELECT}
   */
  protected SELECT SELECT(String... columns) {
    return new SELECT(columns);
  }

  /**
   * Creates a new SQL TABLE
   *
   * @param tableName The name of the table
   * @param columnDefs The column definitions
   */
  protected CREATE_TABLE CREATE_TABLE(String tableName, String... columnDefs) {
    return new CREATE_TABLE(tableName, columnDefs);
  }

  /**
   * Drops a SQL Table
   */
  protected DROP_TABLE DROP_TABLE(String tableName) {
    return new DROP_TABLE(tableName);
  }

  /**
   * Drop a SQL TABLE if the table exists
   */
  protected DROP_TABLE_IF_EXISTS DROP_TABLE_IF_EXISTS(String tableName) {
    return new DROP_TABLE_IF_EXISTS(tableName);
  }

  /**
   * Execute a SQL <code>ALTER TABLE</code> command to change a table name or
   * definition
   */
  protected ALTER_TABLE ALTER_TABLE(String tableName) {
    return new ALTER_TABLE(tableName);
  }

  /**
   * Create a SQL VIEW
   */
  protected CREATE_VIEW CREATE_VIEW(String viewName) {
    return new CREATE_VIEW(viewName);
  }

  /**
   * Create a SQL VIEW IF NOT EXISTS
   */
  protected CREATE_VIEW_IF_NOT_EXISTS CREATE_VIEW_IF_NOT_EXISTS(String viewName) {
    return new CREATE_VIEW_IF_NOT_EXISTS(viewName);
  }

  /**
   * Drop a sql View
   */
  protected DROP_VIEW DROP_VIEW(String viewName) {
    return new DROP_VIEW(viewName);
  }

  /**
   * Drop a View if exists
   */
  protected DROP_VIEW_IF_EXISTS DROP_VIEW_IF_EXISTS(String viewName) {
    return new DROP_VIEW_IF_EXISTS(viewName);
  }

  /**
   * Begins a new sql transaction. Must be followed by
   * {@link #COMMIT()} or {@link #ROLLBACK()}
   */
  public void BEGIN_TRANSACTION() {
    transaction=db.newTransaction();
  }

  /**
   * Commits the current sql transaction
   */
  public void COMMIT() {
    transaction.markSuccessful();
    transaction.end();
  }

  /**
   * Rollback the current sql transaction
   */
  public void ROLLBACK() {
    transaction.end();
  }
}