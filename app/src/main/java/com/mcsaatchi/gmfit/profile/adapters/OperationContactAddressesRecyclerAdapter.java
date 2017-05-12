package com.mcsaatchi.gmfit.profile.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseLocation;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import java.util.List;

public class OperationContactAddressesRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<OperationContactsResponseLocation> locations;

  public OperationContactAddressesRecyclerAdapter(Context context,
      List<OperationContactsResponseLocation> locations) {
    this.context = context;
    this.locations = locations;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_operation_contact_address, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.addressTitleTV.setText(locations.get(position).getLocation());

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
    OperationContactPhoneNumbersRecyclerAdapter operationContactPhoneNumbersRecyclerAdapter =
        new OperationContactPhoneNumbersRecyclerAdapter(context,
            locations.get(position).getPhoneNumbers());

    holder.phoneNumbersRecycler.setLayoutManager(mLayoutManager);
    holder.phoneNumbersRecycler.setAdapter(operationContactPhoneNumbersRecyclerAdapter);
    holder.phoneNumbersRecycler.setNestedScrollingEnabled(false);
    holder.phoneNumbersRecycler.addItemDecoration(new SimpleDividerItemDecoration(context));
  }

  @Override public int getItemCount() {
    return locations.size();
  }

  public OperationContactsResponseLocation getItem(int position) {
    return locations.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView addressTitleTV;
    private RecyclerView phoneNumbersRecycler;

    public ViewHolder(View itemView) {
      super(itemView);

      addressTitleTV = (TextView) itemView.findViewById(R.id.addressTitleTV);
      phoneNumbersRecycler = (RecyclerView) itemView.findViewById(R.id.phoneNumbersRecycler);
    }
  }
}
