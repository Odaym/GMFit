package com.mcsaatchi.gmfit.insurance.activities.home;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ContractsChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import java.util.List;
import javax.inject.Inject;

public class ContractsChoiceView extends LinearLayout {
  @Bind(R.id.contractsRecyclerView) RecyclerView contractsRecyclerView;
  @Inject SharedPreferences prefs;

  private GMFitApplication appObject;
  private Context context;
  private List<InsuranceContract> contracts;
  private Dialog parentDialog;

  public ContractsChoiceView(GMFitApplication appObject, Context context,
      List<InsuranceContract> contracts) {
    super(context);
    this.appObject = appObject;
    this.context = context;
    this.contracts = contracts;

    appObject.getAppComponent().inject(this);

    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.view_contracts_choice, this, true);

    ButterKnife.bind(this, v);
  }

  public void setupContractsChoiceList() {
    ContractsChoiceRecyclerAdapter contractsChoiceRecyclerAdapter =
        new ContractsChoiceRecyclerAdapter(appObject, parentDialog, contracts);
    contractsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    contractsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
    contractsRecyclerView.setAdapter(contractsChoiceRecyclerAdapter);
  }

  public void setParentDialog(Dialog dialog) {
    parentDialog = dialog;
    setupContractsChoiceList();
  }
}
