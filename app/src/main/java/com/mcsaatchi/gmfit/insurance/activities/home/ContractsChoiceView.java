package com.mcsaatchi.gmfit.insurance.activities.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ContractsChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import java.util.ArrayList;
import java.util.List;

public class ContractsChoiceView extends LinearLayout {
  @Bind(R.id.contractsRecyclerView) RecyclerView contractsRecyclerView;

  private Context context;

  public ContractsChoiceView(Context context, List<InsuranceContract> contracts) {
    super(context);
    this.context = context;

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
}
