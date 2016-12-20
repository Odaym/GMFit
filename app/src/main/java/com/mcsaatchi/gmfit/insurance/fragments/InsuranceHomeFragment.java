package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.adapters.InsuranceOperationWidgetsGridAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;

public class InsuranceHomeFragment extends Fragment {

  @Bind(R.id.insurancePathsGridView) RecyclerView insurancePathsGridView;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_home, container, false);

    ButterKnife.bind(this, fragmentView);

    setupInsurancePathsGrid(new ArrayList<InsuranceOperationWidget>() {{
      add(new InsuranceOperationWidget(R.drawable.ic_insurance_operations_submit,
          getString(R.string.widget_submit)));
      add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_track,
          getString(R.string.widget_track)));
      add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_member_guide,
          getString(R.string.widget_members_guide)));
      add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_coverage_desc,
          getString(R.string.widget_coverage_description)));
      add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_snapshot,
          getString(R.string.widget_snapshot)));
    }});

    return fragmentView;
  }

  private void setupInsurancePathsGrid(
      ArrayList<InsuranceOperationWidget> insuranceOperationWidgets) {

    InsuranceOperationWidgetsGridAdapter widgetsAdapter =
        new InsuranceOperationWidgetsGridAdapter(getActivity(), insuranceOperationWidgets);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
    gridLayoutManager.setSpanCount(4);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return 2;
      }
    });

    insurancePathsGridView.setHasFixedSize(true);
    insurancePathsGridView.setLayoutManager(gridLayoutManager);
    insurancePathsGridView.setAdapter(widgetsAdapter);
  }
}