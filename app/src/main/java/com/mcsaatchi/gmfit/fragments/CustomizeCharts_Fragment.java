package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.CustomizeChartsRecyclerAdapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.touch_helpers.SimpleDragItemTouchHelperCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeCharts_Fragment extends Fragment {

  @Bind(R.id.chartsListView) RecyclerView chartsListView;

  private List<DataChart> dataChartsMap;

  private String CHARTS_ORDER_ARRAY_CHANGED_EVENT;

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
            CHARTS_ORDER_ARRAY_CHANGED_EVENT = Constants.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED;
            dataChartsMap =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);
            break;
          case Constants.EXTRAS_NUTRITION_FRAGMENT:
            CHARTS_ORDER_ARRAY_CHANGED_EVENT =
                Constants.EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED;
            dataChartsMap =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_NUTRITION_CHARTS_MAP);
            break;
          case Constants.EXTRAS_HEALTH_FRAGMENT:
            CHARTS_ORDER_ARRAY_CHANGED_EVENT = Constants.EXTRAS_HEALTH_CHARTS_ORDER_ARRAY_CHANGED;
            break;
        }
      }
    }

    if (dataChartsMap != null) {
      CustomizeChartsRecyclerAdapter customizeChartsRecyclerAdapter =
          new CustomizeChartsRecyclerAdapter(dataChartsMap, R.drawable.ic_menu_black_24dp,
              CHARTS_ORDER_ARRAY_CHANGED_EVENT);
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