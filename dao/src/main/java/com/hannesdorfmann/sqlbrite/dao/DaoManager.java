package com.hannesdorfmann.sqlbrite.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.squareup.sqlbrite.SqlBrite;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link DaoManager} manages, like the name suggests, the component that
 * manages the {@link Dao}. It's responsible to call
 * {@link Dao#createTable(SQLiteDatabase)} and
 * {@link Dao#onUpgrade(SQLiteDatabase, int, int)}. It also injects the
 * {@link SQLiteOpenHelper} to the dao by calling
 *
 * @author Hannes Dorfmann
 */
public class DaoManager {

  /**
   * A simple map to hold the references to a concrete {@link Dao}.
   */
  private Set<Dao> daos = new HashSet<>();

  private final String name;
  private final int version;
  private SqlBrite sqlBrite;

  public DaoManager(Context c, String databaseName, int version, Dao... daos) {
    this(c, databaseName, version, null, null, daos);
  }

  public DaoManager(Context c, String databaseName, int version,
      SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler, Dao... daos) {

    OpenHelper openHelper = new OpenHelper(c, databaseName, factory, version, errorHandler);
    this.sqlBrite = SqlBrite.create(openHelper);
    this.name = databaseName;
    this.version = version;
    for (Dao dao : daos) {
      addDao(dao);
    }
  }

  /**
   * Get the database version
   */
  public int getVersion() {
    return version;
  }

  /**
   * Get the name
   */
  public String getName() {
    return name;
  }

  /**
   * Deletes the complete database file
   */
  public void delete(Context c) {
    c.deleteDatabase(getName());
  }

  /**
   * Close the database
   * @throws IOException
   */
  public void close() throws IOException {
    sqlBrite.close();
  }



  /**
   * Activate or deactivate logging
   *
   * @param enabled true if logging enabled, false if not.
   */
  public void setLogging(boolean enabled) {
    sqlBrite.setLoggingEnabled(enabled);
  }

  /**
   * Adds an dao
   */
  private void addDao(Dao dao) {
    dao.setSqlBrite(sqlBrite);
    daos.add(dao);
  }

  /**
   * Internally used SqlOpenHelper
   */
  private class OpenHelper extends SQLiteOpenHelper {
    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
        int version, DatabaseErrorHandler errorHandler) {
      super(context, name, factory, version, errorHandler);
    }

    @Override public void onCreate(SQLiteDatabase db) {
      for (Dao d : daos) {
        d.createTable(db);
      }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      for (Dao d : daos) {
        d.onUpgrade(db, oldVersion, newVersion);
      }
    }
  }
}