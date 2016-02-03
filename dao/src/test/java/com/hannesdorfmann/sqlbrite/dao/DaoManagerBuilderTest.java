package com.hannesdorfmann.sqlbrite.dao;

import android.content.Context;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class) @Config(manifest = Config.NONE)
public class DaoManagerBuilderTest {

  Context context;

  @Before public void init() {
    context = Robolectric.getShadowApplication().getApplicationContext();
  }

  @Test public void noDatabaseName() throws Exception {

    try {
      DaoManager.with(context).build();
      Assert.fail("Exception expected");
    } catch (IllegalArgumentException e) {
      Assert.assertEquals(
          "Database name not set. Use Builder.databaseName() to specify a database name",
          e.getMessage());
    }
  }

  @Test public void noVersion() {

    try {
      DaoManager.with(context).databaseName("foo").build();
      Assert.fail("Exception expected");
    } catch (IllegalArgumentException e) {
      Assert.assertEquals(
          "Database version not set. Use Builder.version() to specify the database version",
          e.getMessage());
    }
  }

  @Test public void noDaosAdded() {

    try {
      DaoManager.with(context).databaseName("foo").version(1).build();
      Assert.fail("Exception expected");
    } catch (IllegalArgumentException e) {
      Assert.assertEquals("No DAO added. Use Builder.add() to register at least one DAO",
          e.getMessage());
    }
  }

  @Test public void minimalBuilder() {
    DaoManager.with(context).databaseName("foo").version(1).add(new UserDao()).build();
  }

}
