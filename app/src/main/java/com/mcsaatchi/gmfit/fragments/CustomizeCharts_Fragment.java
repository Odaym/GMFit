package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.OneItemWithIcon_ListAdapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeCharts_Fragment extends Fragment {

    @Bind(R.id.chartsListView)
    DragSortListView chartsListView;

    private OneItemWithIcon_ListAdapter customizeChartsAdapter;

    private List<DataChart> dataChartsMap;

    private String CHARTS_ORDER_ARRAY_CHANGED_EVENT;

    private Activity parentActivity;

    private List<String> chartNames = new ArrayList<>();

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    EventBus_Singleton.getInstance().post(new EventBus_Poster(CHARTS_ORDER_ARRAY_CHANGED_EVENT, dataChartsMap));

                    customizeChartsAdapter.notifyData();
                }
            };

    private DragSortListView.DragListener onDrag = new DragSortListView.DragListener() {
        @Override
        public void drag(int from, int to) {
            if (to < dataChartsMap.size() && from < dataChartsMap.size()) {
                Collections.swap(chartNames, from, to);
                Collections.swap(dataChartsMap, from, to);
                int tempNumber = dataChartsMap.get(from).getPosition();
                dataChartsMap.get(from).setPosition(dataChartsMap.get(to).getPosition());
                dataChartsMap.get(to).setPosition(tempNumber);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_customize_charts, null);

        ButterKnife.bind(this, fragmentView);

        Bundle fragmentBundle = getArguments();

        String typeOfFragmentToCustomizeFor;

        if (fragmentBundle != null) {

            typeOfFragmentToCustomizeFor = fragmentBundle.getString(Constants.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE);

            if (typeOfFragmentToCustomizeFor != null) {
                switch (typeOfFragmentToCustomizeFor) {
                    case Constants.EXTRAS_FITNESS_FRAGMENT:
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Constants.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED;
                        break;
                    case Constants.EXTRAS_NUTRITION_FRAGMENT:
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Constants.EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED;
                        dataChartsMap = fragmentBundle.getParcelableArrayList(Constants.BUNDLE_NUTRITION_CHARTS_MAP);
                        break;
                    case Constants.EXTRAS_HEALTH_FRAGMENT:
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Constants.EXTRAS_HEALTH_CHARTS_ORDER_ARRAY_CHANGED;
                        break;
                }
            }
        }

        if (dataChartsMap != null) {
            for (DataChart dataChart :
                    dataChartsMap) {
                chartNames.add(dataChart.getName());
            }

            hookupListWithItems(chartNames);
        }

        return fragmentView;
    }

    private void hookupListWithItems(List<String> items) {
        chartsListView.setDragListener(onDrag);
        chartsListView.setDropListener(onDrop);

        customizeChartsAdapter = new OneItemWithIcon_ListAdapter(parentActivity, items, R.drawable.ic_menu_black_24dp);
        chartsListView.setAdapter(customizeChartsAdapter);
    }
}