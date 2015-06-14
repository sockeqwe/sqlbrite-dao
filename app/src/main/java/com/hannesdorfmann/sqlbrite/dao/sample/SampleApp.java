package com.hannesdorfmann.sqlbrite.dao.sample;

import android.app.Application;
import com.hannesdorfmann.mosby.dagger1.Injector;
import dagger.ObjectGraph;

/**
 * @author Hannes Dorfmann
 */
public class SampleApp extends Application implements Injector{

  ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new SampleModule(this));
  }

  @Override public ObjectGraph getObjectGraph() {
    return objectGraph;
  }
}
