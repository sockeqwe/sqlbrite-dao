package com.hannesdorfmann.sqlbrite.objectmapper.processor.test;

import android.database.Cursor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Hannes Dorfmann
 */
public class PublicSetterAnnotatedTest {

  @Test public void mapper() {
    Cursor cursor = Mockito.mock(Cursor.class);

    Mockito.doReturn(1).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_STRING);
    Mockito.doReturn("aString").when(cursor).getString(1);

    Mockito.doReturn(2).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_INT);
    Mockito.doReturn(123).when(cursor).getInt(2);

    Mockito.doReturn(3).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_LONG);
    Mockito.doReturn(456L).when(cursor).getLong(3);

    Mockito.doReturn(4).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_SHORT);
    Mockito.doReturn(Short.MAX_VALUE).when(cursor).getShort(4);

    Mockito.doReturn(5).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_FLOAT);
    Mockito.doReturn(1.2f).when(cursor).getFloat(5);

    Mockito.doReturn(6).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_DOUBLE);
    Mockito.doReturn(1.3).when(cursor).getDouble(6);

    Mockito.doReturn(7).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_BOOLEAN);
    Mockito.doReturn(1).when(cursor).getInt(7);

    byte[] bytes = { 1, 0, 1 };
    Mockito.doReturn(8).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_BYTE);
    Mockito.doReturn(bytes).when(cursor).getBlob(8);

    long timestamp = 123456789;
    Mockito.doReturn(9).when(cursor).getColumnIndexOrThrow(DefaultFieldClass.COL_DATE);
    Mockito.doReturn(timestamp).when(cursor).getLong(9);

    PublicSetterAnnotatedClass model = PublicSetterAnnotatedClassMapper.MAPPER.call(cursor);

    Assert.assertEquals("aString", model.getaString());
    Assert.assertEquals(123, model.getAnInt());
    Assert.assertEquals(456L, model.getALong());
    Assert.assertEquals(Short.MAX_VALUE, model.getAShort());
    Assert.assertEquals(1.2f, model.getAFloat(), 0);
    Assert.assertEquals(1.3, model.getADouble(), 0);
    Assert.assertEquals(true, model.isABool());
    Assert.assertEquals(bytes, model.getBytes());
    Assert.assertEquals(timestamp, model.getADate().getTime());
  }
}
