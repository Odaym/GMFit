package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.CustomizeWidget_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class Fitness_Fragment extends Fragment {

    private DecoView dynamicArc;
    private NestedScrollView parentScrollView;
    private LinearLayout cards_container;

    private TextView firstMetricTV;
    private ImageView firstMetricIMG;

    private TextView secondMetricTV;
    private ImageView secondMetricIMG;

    private TextView thirdMetricTV;
    private ImageView thirdMetricIMG;

    private TextView fourthMetricTV;
    private ImageView fourthMetricIMG;

    public static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;

    private List<Integer> itemIndeces;
    private SparseArray<String[]> itemsMap;

    private SharedPreferences prefs;

    private final String numberOfStepsChartType = "Number of Steps";
    private final String walkingDistanceChartType = "Walking and Running Distance";
    private final String cyclingDistanceChartType = "Cycling Distance";
    private final String totalDistanceChartType = "Total Distance Traveled";
    private final String flightsClimbedChartType = "Flights Climbed";
    private final String activeCaloriesChartType = "Active Calories";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus_Singleton.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);
        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);
        cards_container = (LinearLayout) fragmentView.findViewById(R.id.cards_container);

        firstMetricTV = (TextView) fragmentView.findViewById(R.id.firstMetricTV);
        firstMetricIMG = (ImageView) fragmentView.findViewById(R.id.firstMetricIMG);

        secondMetricTV = (TextView) fragmentView.findViewById(R.id.secondMetricTV);
        secondMetricIMG = (ImageView) fragmentView.findViewById(R.id.secondMetricIMG);

        thirdMetricTV = (TextView) fragmentView.findViewById(R.id.thirdMetricTV);
        thirdMetricIMG = (ImageView) fragmentView.findViewById(R.id.thirdMetricIMG);

        fourthMetricTV = (TextView) fragmentView.findViewById(R.id.fourthMetricTV);
        fourthMetricIMG = (ImageView) fragmentView.findViewById(R.id.fourthMetricIMG);

        BarChart barChart = (BarChart) fragmentView.findViewById(R.id.bar_chart);
        Button addNewChartBTN = (Button) fragmentView.findViewById(R.id.addChartBTN);

        prefs = getActivity().getSharedPreferences(Constants.EXTRAS_PREFS, Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        setUpDecoViewArc();

        setChartData(barChart, 20, 20);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddNewChart_Activity.class), ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_NEW_FITNESS_CHART_REQUEST_CODE:
                if (data != null) {
                    switch (data.getStringExtra(Constants.EXTRAS_CHART_TYPE_SELECTED)) {
                        case numberOfStepsChartType:
                            addNewChart(numberOfStepsChartType);
                            break;
                        case walkingDistanceChartType:
                            addNewChart(walkingDistanceChartType);
                            break;
                        case cyclingDistanceChartType:
                            addNewChart(cyclingDistanceChartType);
                            break;
                        case totalDistanceChartType:
                            addNewChart(totalDistanceChartType);
                            break;
                        case flightsClimbedChartType:
                            addNewChart(flightsClimbedChartType);
                            break;
                        case activeCaloriesChartType:
                            addNewChart(activeCaloriesChartType);
                            break;
                    }
                }
                break;
        }
    }

    public void addNewChart(String chartTitle) {
        final View barChartLayout = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        Button removeChartBTN = (Button) barChartLayout.findViewById(R.id.removeChartBTN);
        final CardView cardLayout = (CardView) barChartLayout.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        setChartData(barChart, 10, 10);

        removeChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards_container.removeView(cardLayout);
            }
        });

        barChartLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.chart_height)));

        cards_container.addView(barChartLayout);

        parentScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                parentScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 500);
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Constants.EXTRAS_WIDGETS_ORDER_ARRAY_CHANGED:
                if (ebp.getSparseArrayExtra() != null) {
                    itemsMap = ebp.getSparseArrayExtra();

                    firstMetricTV.setText(itemsMap.get(0)[0].split(" ")[0]);
                    firstMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.walking));
                    secondMetricTV.setText(itemsMap.get(1)[0].split(" ")[0]);
                    secondMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.biking));
                    thirdMetricTV.setText(itemsMap.get(2)[0].split(" ")[0]);
                    thirdMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.calories));
                    fourthMetricTV.setText(itemsMap.get(3)[0].split(" ")[0]);
                    fourthMetricIMG.setImageDrawable(getResources().getDrawable(R.drawable.stairs));
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), CustomizeWidget_Activity.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
    }

    private void setUpDecoViewArc() {
        SeriesItem seriesItem1 = new SeriesItem.Builder(getResources().getColor(android.R.color.holo_orange_dark))
                .setRange(0, 100, 0)
                .setSpinDuration(2500)
                .setInterpolator(new BounceInterpolator())
                .setLineWidth(35f)
                .build();

        int series1Index = dynamicArc.addSeries(seriesItem1);

        dynamicArc.addEvent(new DecoEvent.Builder(75).setIndex(series1Index).build());
    }

    public void setChartData(BarChart chart, int xLimits, int yLimits) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < yLimits + 1; i++) {
            float mult = (yLimits + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new BarEntry((int) val1, i));
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < xLimits + 1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + "");
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Legend");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setBarShadowColor(R.color.bpblack);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription("");
        chart.setData(data);
        chart.invalidate();
    }
}