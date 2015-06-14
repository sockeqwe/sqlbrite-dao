package com.hannesdorfmann.sqlbrite.dao.sample;

import android.content.Context;
import android.widget.TextView;
import com.hannesdorfmann.annotatedadapter.annotation.ViewField;
import com.hannesdorfmann.annotatedadapter.annotation.ViewType;
import com.hannesdorfmann.annotatedadapter.support.recyclerview.SupportAnnotatedAdapter;
import com.hannesdorfmann.sqlbrite.dao.sample.model.Customer;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class CustomerAdapter extends SupportAnnotatedAdapter implements CustomerAdapterBinder {

  @ViewType(layout = R.layout.list_customer,
      views = {
          @ViewField(id = R.id.cid, type = TextView.class, name = "id"),
          @ViewField(id = R.id.firstname, type = TextView.class, name = "first"),
          @ViewField(id = R.id.lastname, type = TextView.class, name = "last"),
      }) public final int customer = 0;

  private List<Customer> customers;

  public CustomerAdapter(Context context) {
    super(context);
  }

  public void setCustomers(List<Customer> customers) {
    this.customers = customers;
  }

  @Override public int getItemCount() {
    return customers == null ? 0 : customers.size();
  }

  @Override public void bindViewHolder(CustomerAdapterHolders.CustomerViewHolder vh, int position) {
    Customer c = customers.get(position);

    vh.id.setText("" + c.getId());
    vh.first.setText(c.getFirstname());
    vh.last.setText(c.getLastname());
  }
}
