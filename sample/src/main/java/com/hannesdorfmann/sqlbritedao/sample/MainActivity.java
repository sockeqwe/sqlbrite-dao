package com.hannesdorfmann.sqlbritedao.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hannesdorfmann.sqlbritedao.sample.model.CustomerDao;
import com.hannesdorfmann.sqlbritedao.sample.model.customer.Customer;
import java.util.List;
import javax.inject.Inject;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

  @Inject CustomerDao customerDao;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  @Bind(R.id.cid) EditText idEdit;
  @Bind(R.id.lastname) EditText lastnameEdit;
  @Bind(R.id.firstname) EditText firstnameEdit;
  @Bind(R.id.adult) CheckBox adultCheckBox;

  CustomerAdapter adapter;

  Subscription customersSubscription;
  Subscriber<Long> insertSubscription;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    injectDependencies();

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
        firstnameEdit.getText().toString(), lastnameEdit.getText().toString(),
        adultCheckBox.isChecked()).subscribe(insertSubscription);
  }

  private void injectDependencies() {
    ((SampleApp) getApplication()).getObjectGraph().inject(this);
  }
}
