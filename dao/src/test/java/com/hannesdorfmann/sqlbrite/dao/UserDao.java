package com.hannesdorfmann.sqlbrite.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    ContentValues cv = new ContentValues(4);
    cv.put(COL_NAME, name);
    cv.put(COL_AGE, age);
    cv.put(COL_WEIGHT, weight);
    cv.put(COL_BLOB, blob);

   long id =  insert(TABLE, cv).toBlocking().first();

    User u = new User();
    u.setId(id);
    u.setName(name);
    u.setAge(age);
    u.setWeight(weight);
    u.setBlob(blob);

    return u;
  }

  public User getById(long id) {

    return query(
          SELECT(COL_ID, COL_AGE, COL_BLOB, COL_NAME, COL_WEIGHT)
            .FROM(TABLE)
            .WHERE(COL_ID + "=?")
        )
        .args(Long.toString(id))
        .run()
        .mapToOne(new Func1<Cursor, User>() {
          @Override public User call(Cursor c) {
            User user = new User();
            user.setId(c.getLong(0));
            user.setAge(c.getInt(1));
            user.setBlob(c.getBlob(2));
            user.setName(c.getString(3));
            user.setWeight(c.getDouble(4));
            return user;
          }
        })
        .toBlocking()
        .first();
  }
}
