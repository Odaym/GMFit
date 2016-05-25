package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.Base_Activity;
import com.mcsaatchi.gmfit.adapters.OneItemWithIcon_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.reorderable_listview.DragSortListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeCharts_Fragment extends Fragment {
    @Bind(R.id.chartsListView)
    DragSortListView chartsListView;

    private OneItemWithIcon_ListAdapter customizeChartsAdapter;

    private RuntimeExceptionDao<DataChart, Integer> dataChartDAO;
    private PreparedQuery<DataChart> pq;

    private List<DataChart> dataChartsMap;

    private String CHARTS_ORDER_ARRAY_CHANGED_EVENT;

    private Activity parentActivity;

    private List<String> chartNames = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            parentActivity = (Activity) context;
            dataChartDAO = ((Base_Activity) parentActivity).getDBHelper().getDataChartDAO();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_customize_charts, null);

        ButterKnife.bind(this, fragmentView);

        Bundle fragmentBundle = getArguments();

        if (fragmentBundle != null) {
            String typeOfFragmentToCustomizeFor = fragmentBundle.getString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE);

            if (typeOfFragmentToCustomizeFor != null) {
                switch (typeOfFragmentToCustomizeFor) {
                    case Cons.EXTRAS_FITNESS_FRAGMENT:
                        prepareQueryForFragmentType(Cons.EXTRAS_FITNESS_FRAGMENT);
                        dataChartsMap = dataChartDAO.query(pq);
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_FITNESS_CHARTS_ORDER_ARRAY_CHANGED;
                        break;
                    case Cons.EXTRAS_NUTRITION_FRAGMENT:
                        prepareQueryForFragmentType(Cons.EXTRAS_NUTRITION_FRAGMENT);
                        dataChartsMap = dataChartDAO.query(pq);
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED;
                        break;
                    case Cons.EXTRAS_HEALTH_FRAGMENT:
                        prepareQueryForFragmentType(Cons.EXTRAS_HEALTH_FRAGMENT);
                        dataChartsMap = dataChartDAO.query(pq);
                        CHARTS_ORDER_ARRAY_CHANGED_EVENT = Cons.EXTRAS_HEALTH_CHARTS_ORDER_ARRAY_CHANGED;
                        break;
                }
            }
        }

        for (DataChart dataChart :
                dataChartsMap) {
            chartNames.add(dataChart.getName());
        }

        hookupListWithItems(chartNames);

        return fragmentView;
    }

    public void prepareQueryForFragmentType(String fragmentType) {
        try {
            QueryBuilder<DataChart, Integer> chartsQueryBuilder = dataChartDAO.queryBuilder();

            Where where = chartsQueryBuilder.where();
            where.eq("whichFragment", fragmentType);
            chartsQueryBuilder.orderBy("order", true);
            pq = chartsQueryBuilder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    dataChartsMap = dataChartDAO.query(pq);

                    EventBus_Singleton.getInstance().post(new EventBus_Poster(CHARTS_ORDER_ARRAY_CHANGED_EVENT, dataChartsMap));

                    customizeChartsAdapter.notifyData();
                }
            };

    private DragSortListView.DragListener onDrag = new DragSortListView.DragListener() {
        @Override
        public void drag(int from, int to) {
            swap(from, to);
        }
    };

    public void swap(int from, int to) {
        if (to < dataChartsMap.size() && from < dataChartsMap.size()) {
            Collections.swap(chartNames, from, to);
            Collections.swap(dataChartsMap, from, to);
            int tempNumber = dataChartsMap.get(from).getOrder();
            dataChartsMap.get(from).setOrder(dataChartsMap.get(to).getOrder());
            dataChartsMap.get(to).setOrder(tempNumber);
            dataChartDAO.update(dataChartsMap.get(from));
            dataChartDAO.update(dataChartsMap.get(to));
        }
    }


    private void hookupListWithItems(List<String> items) {
        chartsListView.setDragListener(onDrag);
        chartsListView.setDropListener(onDrop);

        customizeChartsAdapter = new OneItemWithIcon_ListAdapter(parentActivity, items, R.drawable.ic_menu_black_24dp);
        chartsListView.setAdapter(customizeChartsAdapter);
    }
}