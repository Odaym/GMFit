package com.mcsaatchi.gmfit.insurance.adapters;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

public class ContractsChoiceRecyclerAdapter
    extends RecyclerView.Adapter<ContractsChoiceRecyclerAdapter.RecyclerViewHolder> {

  @Inject SharedPreferences prefs;
  private List<InsuranceContract> contracts;
  private Dialog dialogView;

  public ContractsChoiceRecyclerAdapter(GMFitApplication appObject, Dialog dialogView,
      List<InsuranceContract> contracts) {
    this.dialogView = dialogView;
    this.contracts = contracts;

    appObject.getAppComponent().inject(this);
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

    if (Objects.equals(contracts.get(position).getNumber(),
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""))) {
      contracts.get(position).setSelected(true);
      holder.contractCheckedLayout.setVisibility(View.VISIBLE);
    }

    holder.contractTitleTV.setText(
        (contracts.get(position)).getNumber() + " - " + contracts.get(position).getHoldername());
    holder.contractInsuranceCompanyTV.setText(contracts.get(position).getInsuranceCompany());

    if (contracts.get(position).getExpiryDate() != null) {
      String expiryCal = contracts.get(position).getExpiryDate();
      holder.contractExpiryDateTV.setText("Expiry date: " + expiryCal.replace("\\", ""));
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

      contractTitleTV = itemView.findViewById(R.id.contractTitleTV);
      contractInsuranceCompanyTV = itemView.findViewById(R.id.contractInsuranceCompanyTV);
      contractExpiryDateTV = itemView.findViewById(R.id.contractExpiryDateTV);
      contractCheckedLayout = itemView.findViewById(R.id.contractCheckedLayout);

      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      for (int i = 0; i < getItemCount(); i++) {
        getItem(i).setSelected(false);
      }

      contracts.get(getAdapterPosition()).setSelected(true);
      prefs.edit()
          .putString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER,
              contracts.get(getAdapterPosition()).getNumber())
          .apply();
      prefs.edit()
          .putString(Constants.EXTRAS_INSURANCE_COMPANY_NAME,
              contracts.get(getAdapterPosition()).getInsuranceCompany())
          .apply();
      prefs.edit()
          .putString(Constants.EXTRAS_INSURANCE_FULL_NAME,
              contracts.get(getAdapterPosition()).getFullName())
          .apply();
      contractCheckedLayout.setVisibility(View.VISIBLE);

      notifyDataSetChanged();

      dialogView.dismiss();
    }
  }
}
