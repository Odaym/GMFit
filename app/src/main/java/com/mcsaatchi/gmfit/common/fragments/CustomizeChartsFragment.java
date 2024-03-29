package com.mcsaatchi.gmfit.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleDragItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.adapters.CustomizeChartsRecyclerAdapter;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.List;

public class CustomizeChartsFragment extends Fragment {

  @Bind(R.id.chartsListView) RecyclerView chartsListView;

  private List<DataChart> dataChartsMap;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_customize_charts, null);

    ButterKnife.bind(this, fragmentView);

    Bundle fragmentBundle = getArguments();

    String typeOfFragmentToCustomizeFor;

    if (fragmentBundle != null) {

      typeOfFragmentToCustomizeFor = fragmentBundle.getString(Constants.EXTRAS_FRAGMENT_TYPE);

      if (typeOfFragmentToCustomizeFor != null) {
        switch (typeOfFragmentToCustomizeFor) {
          case Constants.EXTRAS_FITNESS_FRAGMENT:
            dataChartsMap =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);
            break;
          case Constants.EXTRAS_NUTRITION_FRAGMENT:
            dataChartsMap =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_NUTRITION_CHARTS_MAP);
            break;
          case Constants.EXTRAS_HEALTH_FRAGMENT:
            break;
        }
      }
    }

    if (dataChartsMap != null) {
      CustomizeChartsRecyclerAdapter customizeChartsRecyclerAdapter =
          new CustomizeChartsRecyclerAdapter(dataChartsMap, R.drawable.ic_menu_black_24dp);
      ItemTouchHelper.Callback callback =
          new SimpleDragItemTouchHelperCallback(customizeChartsRecyclerAdapter);
      ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

      chartsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
      chartsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
      chartsListView.setAdapter(customizeChartsRecyclerAdapter);

      touchHelper.attachToRecyclerView(chartsListView);
    }

    return fragmentView;
  }
}