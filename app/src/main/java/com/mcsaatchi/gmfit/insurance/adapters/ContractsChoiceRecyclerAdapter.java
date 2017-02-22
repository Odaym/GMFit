package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ContractsChoiceRecyclerAdapter
    extends RecyclerView.Adapter<ContractsChoiceRecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<InsuranceContract> contracts;

  public ContractsChoiceRecyclerAdapter(ArrayList<InsuranceContract> contracts) {
    this.contracts = contracts;
  }

  @Override public int getItemCount() {
    return contracts.size();
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_contract_choice, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    if (contracts.get(position).isSelected()) {
      holder.contractCheckedLayout.setVisibility(View.VISIBLE);
    } else {
      holder.contractCheckedLayout.setVisibility(View.GONE);
    }

    holder.contractTitleTV.setText(
        (contracts.get(position)).getNumber() + " - " + contracts.get(position).getHoldername());
    holder.contractInsuranceCompanyTV.setText(contracts.get(position).getInsuranceCompany());

    if (contracts.get(position).getExpiryDate() != null) {
      Calendar expiryCal = contracts.get(position).getExpiryDate();
      holder.contractExpiryDateTV.setText(
          "Expiry date: " + new SimpleDateFormat("dd MMMM yyyy").format(expiryCal.getTime()));
    }
  }

  private InsuranceContract getItem(int position) {
    return contracts.get(position);
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView contractTitleTV;
    TextView contractInsuranceCompanyTV;
    TextView contractExpiryDateTV;
    LinearLayout contractCheckedLayout;

    RecyclerViewHolder(View itemView) {
      super(itemView);

      contractTitleTV = (TextView) itemView.findViewById(R.id.contractTitleTV);
      contractInsuranceCompanyTV =
          (TextView) itemView.findViewById(R.id.contractInsuranceCompanyTV);
      contractExpiryDateTV = (TextView) itemView.findViewById(R.id.contractExpiryDateTV);
      contractCheckedLayout = (LinearLayout) itemView.findViewById(R.id.contractCheckedLayout);

      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      for (int i = 0; i < getItemCount(); i++) {
        getItem(i).setSelected(false);
      }

      contracts.get(getAdapterPosition()).setSelected(true);
      contractCheckedLayout.setVisibility(View.VISIBLE);

      notifyDataSetChanged();
    }
  }
}
