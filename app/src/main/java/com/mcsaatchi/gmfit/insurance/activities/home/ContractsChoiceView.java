package com.mcsaatchi.gmfit.insurance.activities.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ContractsChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ContractsChoiceView extends LinearLayout {
  @Bind(R.id.contractsRecyclerView) RecyclerView contractsRecyclerView;
  @Inject SharedPreferences prefs;

  private Context context;
  private List<InsuranceContract> contracts;

  public ContractsChoiceView(Context context, List<InsuranceContract> contracts) {
    super(context);
    this.context = context;
    this.contracts = contracts;

    ((GMFitApplication) context).getAppComponent().inject(this);

    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.view_contracts_choice, this, true);

    ButterKnife.bind(this, v);

    setupContractsChoiceList((ArrayList<InsuranceContract>) contracts);
  }

  private void setupContractsChoiceList(ArrayList<InsuranceContract> contracts) {
    ContractsChoiceRecyclerAdapter contractsChoiceRecyclerAdapter =
        new ContractsChoiceRecyclerAdapter(contracts);
    contractsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    contractsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
    contractsRecyclerView.setAdapter(contractsChoiceRecyclerAdapter);
  }

  @OnClick(R.id.updateContractBTN) public void handleUpdateContract() {
    for (InsuranceContract contract : contracts) {
      if (contract.isSelected()) {
        prefs.edit()
            .putString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, contract.getNumber())
            .apply();
      }
    }
  }
}
