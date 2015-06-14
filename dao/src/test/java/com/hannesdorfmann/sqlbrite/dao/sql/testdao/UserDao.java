package com.hannesdorfmann.sqlbrite.dao.sql.testdao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.squareup.sqlbrite.SqlBrite;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.functions.Action1;
import rx.functions.Func1;

public class UserDao extends Dao {

  private final String TABLE = "test";
  private final String COL_ID = "id";
  private final String COL_NAME = "name";
  private final String COL_AGE = "age";
  private final String COL_WEIGHT = "weight";
  private final String COL_BLOB = "blob";

  @Override public void createTable(SQLiteDatabase db) {

    CREATE_TABLE(TABLE, COL_ID + " INTEGER PRIMARY KEY autoincrement",
        COL_NAME + " TEXT NOT NULL, " + COL_AGE + " INTEGER NOT NULL ",
        COL_WEIGHT + "  DOUBLE NOT NULL", COL_BLOB + " BLOB").execute(db);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    if (oldVersion == 1 && newVersion == 2) {

      ALTER_TABLE(TABLE).ADD_COLUMN(COL_WEIGHT + "  DOUBLE NOT NULL").execute(db);
    }
  }

  /**
   * Insert {@link User}
   *
   * @throws Exception
   */
  public User insert(String name, int age, double weight, byte[] blob) {

    BEGIN_TRANSACTION();

    ContentValues cv = new ContentValues(4);
    cv.put(COL_NAME, name);
    cv.put(COL_AGE, age);
    cv.put(COL_WEIGHT, weight);
    cv.put(COL_BLOB, blob);

    final AtomicLong id = new AtomicLong();
    insert(TABLE, cv).subscribe(new Action1<Long>() {
      @Override public void call(Long aLong) {
        id.set(aLong);
      }
    });

    COMMIT();

    User u = new User();
    u.setId(id.get());
    u.setName(name);
    u.setAge(age);
    u.setWeight(weight);
    u.setBlob(blob);

    return u;
  }

  public User getById(long id) {

    final AtomicReference<User> userRef = new AtomicReference<>();
    query(SELECT(COL_ID, COL_AGE, COL_BLOB, COL_NAME, COL_WEIGHT).FROM(TABLE).WHERE(COL_ID + "=?"),
        Long.toString(id)).map(new Func1<SqlBrite.Query, User>() {

      @Override public User call(SqlBrite.Query query) {
        Cursor c = null;
        try {
          c = query.run();
          c.moveToFirst();

          if (c.getCount() == 0) {
            return null;
          }

          User user = new User();
          user.setId(c.getLong(0));
          user.setAge(c.getInt(1));
          user.setBlob(c.getBlob(2));
          user.setName(c.getString(3));
          user.setWeight(c.getDouble(4));
          return user;
        } finally {

          if (c != null) {
            c.close();
          }
        }
      }
    }).subscribe(new Action1<User>() {
      @Override public void call(User user) {
        userRef.set(user);
      }
    });

    return userRef.get();
  }
}
