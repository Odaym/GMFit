package com.mcsaatchi.gmfit.profile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponsePhoneNumber;
import java.util.List;

class OperationContactPhoneNumbersRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<OperationContactsResponsePhoneNumber> phoneNumbers;

  OperationContactPhoneNumbersRecyclerAdapter(Context context,
      List<OperationContactsResponsePhoneNumber> phoneNumbers) {
    this.context = context;
    this.phoneNumbers = phoneNumbers;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_operation_contact_phone_number, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.phoneNumberNameTV.setText(phoneNumbers.get(position).getName());
    holder.phoneNumberValueTV.setText(phoneNumbers.get(position).getNumber());
  }

  @Override public int getItemCount() {
    return phoneNumbers.size();
  }

  public OperationContactsResponsePhoneNumber getItem(int position) {
    return phoneNumbers.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView phoneNumberNameTV;
    private TextView phoneNumberValueTV;

    public ViewHolder(View itemView) {
      super(itemView);

      phoneNumberNameTV = (TextView) itemView.findViewById(R.id.phoneNumberNameTV);
      phoneNumberValueTV = (TextView) itemView.findViewById(R.id.phoneNumberValueTV);
    }
  }
}
