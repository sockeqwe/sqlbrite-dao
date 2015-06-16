package com.hannesdorfmann.sqlbrite.dao.sample;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.hannesdorfmann.mosby.dagger1.Dagger1MosbyActivity;
import com.hannesdorfmann.sqlbrite.dao.sample.model.customer.Customer;
import com.hannesdorfmann.sqlbrite.dao.sample.model.CustomerDao;
import java.util.List;
import javax.inject.Inject;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends Dagger1MosbyActivity {

  @Inject CustomerDao customerDao;
  @InjectView(R.id.recyclerView) RecyclerView recyclerView;
  @InjectView(R.id.cid) EditText idEdit;
  @InjectView(R.id.lastname) EditText lastnameEdit;
  @InjectView(R.id.firstname) EditText firstnameEdit;

  CustomerAdapter adapter;

  Subscription customersSubscription;
  Subscriber<Long> insertSubscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    adapter = new CustomerAdapter(this);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    customersSubscription = customerDao.getCustomers().subscribe(new Action1<List<Customer>>() {
      @Override public void call(List<Customer> customers) {
        adapter.setCustomers(customers);
        adapter.notifyDataSetChanged();
      }
    }, new Action1<Throwable>() {
      @Override public void call(Throwable e) {
        e.printStackTrace();
        Toast.makeText(MainActivity.this, "Error! check logcat", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    customersSubscription.unsubscribe();
    if (insertSubscription != null) {
      insertSubscription.unsubscribe();
    }
  }

  @OnClick(R.id.insert) public void onAddClicked() {
    if (insertSubscription != null) {
      insertSubscription.unsubscribe();
    }

    insertSubscription = new Subscriber<Long>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {
        e.printStackTrace();
        Toast.makeText(MainActivity.this, "Error! check logcat", Toast.LENGTH_SHORT).show();
      }

      @Override public void onNext(Long aLong) {
        Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_LONG).show();
      }
    };

    customerDao.insert(Integer.parseInt(idEdit.getText().toString()),
        firstnameEdit.getText().toString(), lastnameEdit.getText().toString())
        .subscribe(insertSubscription);
  }

  @Override protected void injectDependencies() {
    getObjectGraph().inject(this);
  }
}
