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
import com.squareup.sqlbrite.QueryObservable;
import java.util.Collections;
import rx.Observable;

import static com.squareup.sqlbrite.BriteDatabase.Transaction;

/**
 * Data Access Object (DAO).
 *
 * @author Hannes Dorfmann
 */
public abstract class Dao {

  /**
   * Builder pattern to build a query.
   */
  public class QueryBuilder {

    String rawStatement;
    Iterable<String> rawStatementAffectedTables;
    SqlFinishedStatement statement;
    String[] args;
    boolean autoUpdate = true;

    public QueryBuilder(@Nullable Iterable<String> rawStatementAffectedTables,
        @NonNull String rawStatement) {

      if (rawStatement == null) {
        throw new NullPointerException("Raw SQL Query Statement is null");
      }

      this.rawStatement = rawStatement;
      this.rawStatementAffectedTables = rawStatementAffectedTables;
      this.autoUpdate =
          rawStatementAffectedTables != null && rawStatementAffectedTables.iterator().hasNext();
    }

    private QueryBuilder(@NonNull SqlFinishedStatement statement) {
      if (statement == null) {
        throw new NullPointerException("Statment is null!");
      }
      this.statement = statement;
    }

    /**
     * Set the arguments used for the prepared statement
     *
     * @param args The strings used to replace "?" in the SELECT query statement
     * @return The QueryBuilder itself
     */
    public QueryBuilder args(String... args) {
      this.args = args;
      return this;
    }

    /**
     * Registers this query for automatically updates through SQLBrite. SQLBrite offers a mechanism
     * to get notified on data changes on the queried database table (like insert, update or delete
     * rows) and automatically rerun this query. Per default this feature is enabled.
     *
     * @param autoUpdate true to enable, false to disable.
     * @return The QueryBuilder itself
     */
    public QueryBuilder autoUpdates(boolean autoUpdate) {
      this.autoUpdate = autoUpdate;

      // Using raw statement, but no table to observe specified
      if (autoUpdate && statement == null && (rawStatementAffectedTables == null
          || !rawStatementAffectedTables.iterator().hasNext())) {
        throw new RuntimeException("You try to set autoUpdates(true) but, "
            + "your raw sql query statement has not specified which tables are affected by this query. Hence autoUpdates can not be enabled! Specify the affected tables as second parameter of rawQuery(String rawSQL, String ... affectedTables) method.");
      }

      return this;
    }

    /**
     * Executes the query and returns an {@code QueryObservable}
     *
     * @return {@code QueryObservable}
     * @see QueryObservable
     */
    public QueryObservable run() {
      return executeQuery(this);
    }
  }

  protected BriteDatabase db;

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
  void setSqlBriteDb(BriteDatabase db) {
    this.db = db;
  }

  /**
   * Create a new Transaction. Don't forget to commit your changes by marking the transaction as
   * successful or rollback your changes.
   *
   * @return New transaction.
   */
  public Transaction newTransaction() {
    return db.newTransaction();
  }

  /**
   * Creates a query.
   *
   * @param statement the sql statement
   * @return QueryBuilder to proceed query building
   */
  protected QueryBuilder query(@NonNull SqlFinishedStatement statement) {
    return new QueryBuilder(statement);
  }

  /**
   * Creates a raw query and enables auto updates for the given tables
   *
   * @param tables The affected table. updates get triggered if the observed tables changes. Use
   * {@code null} or
   * {@link #rawQuery(String)} if you don't want to register for automatic updates
   * @param sql The sql query statement
   * @return Observable of this query
   */
  protected QueryBuilder rawQueryOnManyTables(@Nullable final Iterable<String> tables,
      @NonNull final String sql) {
    return new QueryBuilder(tables, sql);
  }

  /**
   * Creates a raw query and enables auto updates for the given single table
   *
   * @param table the affected table. updates get triggered if the observed tables changes. Use
   * {@code null} or
   * {@link #rawQuery(String)} if you don't want to register for automatic updates
   * @param sql The sql query statement
   */
  protected QueryBuilder rawQuery(@Nullable final String table, @NonNull String sql) {
    return rawQueryOnManyTables(table == null ? null : Collections.singleton(table), sql);
  }

  /**
   * Creates a raw query. AutoUpdates are disabled since the name of the table to observe are not
   * specified.
   *
   * @param sql The raw SQL query statement. Arguments can still be specified with "?" as
   * placeholder
   */
  protected QueryBuilder rawQuery(@NonNull String sql) {
    return rawQueryOnManyTables(null, sql);
  }

  /**
   * Executes the a query
   */
  private QueryObservable executeQuery(QueryBuilder queryBuilder) {

    // Raw query properties as default
    String sql = queryBuilder.rawStatement;
    Iterable<String> affectedTables = queryBuilder.rawStatementAffectedTables;

    // If SqlFinishedStatement is set then use that one
    if (queryBuilder.statement != null) {

      SqlCompileable.CompileableStatement compileableStatement =
          queryBuilder.statement.asCompileableStatement();

      sql = compileableStatement.sql;
      affectedTables = compileableStatement.tables;
    }

    // Check for auto update
    if (!queryBuilder.autoUpdate || affectedTables == null) {
      affectedTables = Collections.emptySet();
    }

    return db.createQuery(affectedTables, sql, queryBuilder.args);
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

    return Observable.just(db.update(table, values, conflictAlgorithm, whereClause, whereArgs));
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
}