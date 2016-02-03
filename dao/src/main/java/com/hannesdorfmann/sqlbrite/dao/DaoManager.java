package com.hannesdorfmann.sqlbrite.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link DaoManager} manages, like the name suggests, the component that
 * manages the {@link Dao}. It's responsible to call
 * {@link Dao#createTable(SQLiteDatabase)} and
 * {@link Dao#onUpgrade(SQLiteDatabase, int, int)}. It also setup the {@link BriteDatabase}.
 *
 * @author Hannes Dorfmann
 */
public class DaoManager {

  private Set<Dao> daos;
  private final String name;
  private final int version;
  private BriteDatabase db;
  private TablesCreatedListener createdListener;
  private TablesUpgradedListener upgradedListener;

  private DaoManager(Builder builder) {

    if (builder.name == null) {
      throw new IllegalArgumentException(
          "Database name not set. Use Builder.databaseName() to specify a database name");
    }

    if (builder.version == -1) {
      throw new IllegalArgumentException(
          "Database version not set. Use Builder.version() to specify the database version");
    }

    if (builder.daos.isEmpty()) {
      throw new IllegalArgumentException(
          "No DAO added. Use Builder.add() to register at least one DAO");
    }

    this.name = builder.name;
    this.version = builder.version;
    this.createdListener = builder.createdListener;
    this.upgradedListener = builder.upgradedListener;
    this.daos = builder.daos;

    OpenHelper openHelper =
        new OpenHelper(builder.context, name, builder.cursorFactory, version, builder.errorHandler);

    db = SqlBrite.create().wrapDatabaseHelper(openHelper);
    db.setLoggingEnabled(builder.logging);

    for (Dao dao : builder.daos) {
      dao.setSqlBriteDb(db);
    }
  }

  /**
   * Get the underlying {@link BriteDatabase} instance
   *
   * @return The database
   */
  public BriteDatabase getDatabase() {
    return db;
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
   *
   * @throws IOException
   */
  public void close() throws IOException {
    db.close();
  }

  /**
   * Use this to instantiate a builder
   *
   * @param context The context
   * @return A {@link Builder}
   */
  public static Builder with(Context context) {
    return new Builder(context);
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

      if (createdListener != null) {
        createdListener.onTablesCreated(db);
      }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      for (Dao d : daos) {
        d.onUpgrade(db, oldVersion, newVersion);
      }

      if (upgradedListener != null) {
        upgradedListener.onTablesUpgraded(db, oldVersion, newVersion);
      }
    }
  }

  public static class Builder {
    private Set<Dao> daos = new HashSet<>();
    private String name;
    private int version = -1;
    private final Context context;
    private SQLiteDatabase.CursorFactory cursorFactory = null;
    private DatabaseErrorHandler errorHandler = new DefaultDatabaseErrorHandler();
    private TablesCreatedListener createdListener = null;
    private TablesUpgradedListener upgradedListener = null;
    private boolean logging = false;

    private Builder(Context context) {
      this.context = context.getApplicationContext();
    }

    public Builder databaseName(String name) {
      this.name = name;
      return this;
    }

    public Builder version(int version) {
      this.version = version;
      return this;
    }

    public Builder cursorFactory(SQLiteDatabase.CursorFactory factory) {
      this.cursorFactory = cursorFactory;
      return this;
    }

    public Builder errorHandler(DatabaseErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
      return this;
    }

    public Builder onTablesCreated(TablesCreatedListener createdListener) {
      this.createdListener = createdListener;
      return this;
    }

    public Builder onTablesUpgraded(TablesUpgradedListener tablesUpgradedListener) {
      this.upgradedListener = tablesUpgradedListener;
      return this;
    }

    public Builder add(Dao dao) {
      this.daos.add(dao);
      return this;
    }

    public Builder logging(boolean logging) {
      this.logging = logging;
      return this;
    }

    public DaoManager build() {
      return new DaoManager(this);
    }
  }

  /**
   * Listener that gets notified after all registered DAOs have created their SQL Table.
   * In other words after {@link Dao#createTable(SQLiteDatabase)} has been invoked for each DAO.
   */
  public interface TablesCreatedListener {

    /**
     * Callback that gets invoked after all registered DAOs have created their SQL Table.
     * In other words after {@link Dao#createTable(SQLiteDatabase)} has been invoked for each DAO.
     *
     * @param db the database
     */
    void onTablesCreated(SQLiteDatabase db);
  }

  /**
   * Listener that gets notified after all registered DAOs have upgraded their SQL Table Schema.
   * In other words after {@link Dao#onUpgrade(SQLiteDatabase, int, int)} has been invoked for each
   * DAO.
   */
  public interface TablesUpgradedListener {

    /**
     * Callback that gets invoked after all registered DAOs have upgraded their SQL Table Schema.
     * In other words after {@link Dao#onUpgrade(SQLiteDatabase, int, int)} has been invoked for
     * each DAO.
     *
     * @param db the database
     * @param oldVersion The old version of the database
     * @param newVersion The new version we have upgraded to
     */
    void onTablesUpgraded(SQLiteDatabase db, int oldVersion, int newVersion);
  }
}