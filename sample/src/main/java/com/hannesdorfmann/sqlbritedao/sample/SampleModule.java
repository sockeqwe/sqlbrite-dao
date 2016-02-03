package com.hannesdorfmann.sqlbritedao.sample;

import android.content.Context;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.hannesdorfmann.sqlbritedao.sample.model.CustomerDao;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * @author Hannes Dorfmann
 */
@Module(
    library = true,
    complete = false,
    injects = {
        MainActivity.class
    }) public class SampleModule {

  Context context;
  CustomerDao customerDao;
  DaoManager daoManager;

  public SampleModule(Context context) {
    this.context = context;
    customerDao = new CustomerDao();
    daoManager = DaoManager.with(context)
        .databaseName("Customers.db")
        .version(1)
        .add(customerDao)
        .logging(true)
        .build();
  }

  @Singleton @Provides public CustomerDao providesCustomerDao() {
    return customerDao;
  }
}
