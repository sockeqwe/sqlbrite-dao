package com.hannesdorfmann.sqlbrite.dao;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.NonNull;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * The {@link DaoManager} manages, like the name suggests, the component that
 * manages the {@link Dao}. It's responsible to call
 * {@link Dao#createTable(SQLiteDatabase)} and
 * {@link Dao#onUpgrade(SQLiteDatabase, int, int)}. It also setup the {@link BriteDatabase}.
 *
 * <p>
 * To create a new instance use {@link #with(Context)} which returns a {@link Builder}
 * </p>
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

    OpenHelper openHelper;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
      openHelper = new OpenHelper(builder.context, name, builder.cursorFactory, version,
          builder.errorHandler, builder.foreignKeyConstraints);
    } else {
      openHelper = new OpenHelperApi16(builder.context, name, builder.cursorFactory, version,
          builder.errorHandler, builder.foreignKeyConstraints);
    }

    SqlBrite brite;
    if (builder.logger != null) {
      brite = SqlBrite.create(builder.logger);
    } else {
      brite = SqlBrite.create();
    }

    db = brite.wrapDatabaseHelper(openHelper,
        builder.scheduler == null ? Schedulers.io() : builder.scheduler);
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

    protected boolean foreignKeyConstraints;

    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
        int version, DatabaseErrorHandler errorHandler, boolean foreignKeyConstraints) {
      super(context, name, factory, version, errorHandler);
      this.foreignKeyConstraints = foreignKeyConstraints;
    }

    @Override public void onOpen(SQLiteDatabase db) {
      super.onOpen(db);
      if (Build.VERSION.SDK_INT < 16) {
        if (foreignKeyConstraints) {
          db.execSQL("PRAGMA foreign_keys=ON;");
        }
      }
    }

    @Override public void onConfigure(SQLiteDatabase db) {
      super.onConfigure(db);
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

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private class OpenHelperApi16 extends OpenHelper {
    public OpenHelperApi16(Context context, String name, SQLiteDatabase.CursorFactory factory,
        int version, DatabaseErrorHandler errorHandler, boolean foreignKeyConstraints) {
      super(context, name, factory, version, errorHandler, foreignKeyConstraints);
    }

    @Override public void onConfigure(SQLiteDatabase db) {
      super.onConfigure(db);
      if (foreignKeyConstraints) {
        db.setForeignKeyConstraintsEnabled(true);
      }
    }
  }

  /**
   * The Builder to configure and instantiate a {@link DaoManager}.
   */
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
    private SqlBrite.Logger logger = null;
    private Scheduler scheduler = null;
    private boolean foreignKeyConstraints = false;

    private Builder(Context context) {
      this.context = context.getApplicationContext();
    }

    public Builder databaseName(@NonNull String name) {
      if (name == null || name.length() == 0) {
        throw new NullPointerException("name == null");
      }
      this.name = name;
      return this;
    }

    /**
     * Specify the database version
     */
    public Builder version(int version) {
      this.version = version;
      return this;
    }

    /**
     * Specify the {@link SQLiteDatabase.CursorFactory}
     *
     * @param factory the factory
     * @return the builder
     */
    public Builder cursorFactory(SQLiteDatabase.CursorFactory factory) {
      this.cursorFactory = cursorFactory;
      return this;
    }

    /**
     * set the {@link DatabaseErrorHandler}
     *
     * @param errorHandler the errorhandler
     * @return the builder itself
     */
    public Builder errorHandler(DatabaseErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
      return this;
    }

    /**
     * Registers a {@link TablesCreatedListener}
     *
     * @return the builder itseld
     */
    public Builder onTablesCreated(@NonNull TablesCreatedListener createdListener) {
      if (createdListener == null) {
        throw new NullPointerException("tablesCreatedListener == null");
      }

      this.createdListener = createdListener;
      return this;
    }

    /**
     * Registers a {@link TablesUpgradedListener}
     *
     * @param tablesUpgradedListener the listener
     * @return the builder itself
     */
    public Builder onTablesUpgraded(@NonNull TablesUpgradedListener tablesUpgradedListener) {
      if (tablesUpgradedListener == null) {
        throw new NullPointerException("tablesUpgradedListener == null");
      }
      this.upgradedListener = tablesUpgradedListener;
      return this;
    }

    /**
     * Add a Dao
     *
     * @param dao The dao to be added to this DaoManager
     * @return the builder itseld
     */
    public Builder add(@NonNull Dao dao) {
      if (dao == null) {
        throw new NullPointerException("dao == null");
      }
      this.daos.add(dao);
      return this;
    }

    /**
     * Enables or disables logging
     *
     * @param logging true for enabled, otherwise false
     * @return the builder itself
     */
    public Builder logging(boolean logging) {
      this.logging = logging;
      return this;
    }

    /**
     * Specify the logger that should be used internally by SqlBrite. This will automatically
     * enable
     * logging {@link Builder#logging(boolean)}
     *
     * @param logger The logger instance
     * @return the builder itself
     */
    public Builder logger(@NonNull SqlBrite.Logger logger) {
      if (logger == null) {
        throw new NullPointerException("Logger == null");
      }
      this.logger = logger;
      logging(true);
      return this;
    }

    /**
     * Set the scheduler that should be used by SqlBrite to emit items.
     * A Scheduler is required for a few reasons, but the most important is that query
     * notifications
     * can trigger on the thread of your choice. The query can then be run without blocking the
     * main
     * thread or the thread which caused the trigger.
     *
     * <p>
     * Per default {@link Schedulers#io()} is used.
     * </p>
     *
     * @param scheduler The {@link Scheduler} on which items from {@link BriteDatabase#createQuery}
     * will be emitted.
     * @return the builder itself
     */
    public Builder scheduler(Scheduler scheduler) {
      this.scheduler = scheduler;
      return this;
    }

    /**
     * Enable foreign key on the underlying database. Per default foreign key support is disabled.
     *
     * @param enabled true to enable, false to disable
     * @return the builder itself
     */
    public Builder foreignKeyConstraints(boolean enabled) {
      this.foreignKeyConstraints = enabled;
      return this;
    }

    /**
     * Builds a DaoManager with the specified config (via this builder)
     *
     * @return DaoManager instance
     */
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