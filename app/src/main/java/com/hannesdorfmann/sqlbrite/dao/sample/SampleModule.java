package com.hannesdorfmann.sqlbrite.dao.sample;

import android.content.Context;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.hannesdorfmann.sqlbrite.dao.sample.model.CustomerDao;
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
    }
)
public class SampleModule {

  Context context;
  CustomerDao customerDao;
  DaoManager daoManager;

  public SampleModule(Context context) {
    this.context = context;
    customerDao = new CustomerDao();
    daoManager = new DaoManager(context, "Customers.db", 1, customerDao);
    daoManager.setLogging(true);
  }

  @Singleton @Provides public CustomerDao providesCustomerDao(){
    return customerDao;
  }
}
