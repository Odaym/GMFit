package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.hookedonplay.decoviewlib.DecoView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.activities.AddNewMealItem_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Nutrition_Fragment extends Fragment {

    private NestedScrollView parentScrollView;

    @Bind(R.id.dynamicArcView)
    DecoView dynamicArc;
    @Bind(R.id.cards_container)
    LinearLayout cards_container;
    @Bind(R.id.addChartBTN)
    Button addNewChartBTN;
    @Bind(R.id.chartTitleTV)
    TextView chartTitleTV;

    @Bind(R.id.firstMetricTitleTV)
    TextView firstMetricTitleTV;
    @Bind(R.id.firstMetricValueTV)
    TextView firstMetricValueTV;
    @Bind(R.id.firstMetricPercentagesTV)
    TextView firstMetricPercentagesTV;

    @Bind(R.id.secondMetricTitleTV)
    TextView secondMetricTitleTV;
    @Bind(R.id.secondMetricValueTV)
    TextView secondMetricValueTV;
    @Bind(R.id.secondMetricPercentagesTV)
    TextView secondMetricPercentagesTV;

    @Bind(R.id.thirdMetricTitleTV)
    TextView thirdMetricTitleTV;
    @Bind(R.id.thirdMetricValueTV)
    TextView thirdMetricValueTV;
    @Bind(R.id.thirdMetricPercentagesTV)
    TextView thirdMetricPercentagesTV;

    @Bind(R.id.fourthMetricTitleTV)
    TextView fourthMetricTitleTV;
    @Bind(R.id.fourthMetricValueTV)
    TextView fourthMetricValueTV;
    @Bind(R.id.fourthMetricPercentagesTV)
    TextView fourthMetricPercentagesTV;

    @Bind(R.id.addEntryBTN)
    Button addNewEntryBTN;

    private SparseArray<String[]> itemsMap;

    public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;

    private SharedPreferences prefs;

    private final String caloriesChartType = "Calories";
    private final String biotinChartType = "Biotin";
    private final String caffeineChartType = "Caffeine";
    private final String calciumChartType = "Calcium";
    private final String carbohydratesChartType = "Carbohydrates";
    private final String chlorideChartType = "Chloride";
    private final String chromiumChartType = "Chromium";
    private final String copperChartType = "Copper";
    private final String dietary_cholesterolChartType = "Dietary Cholesterol";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Constants.EXTRAS_PREFS, Context.MODE_PRIVATE);

        setHasOptionsMenu(true);

        Helpers.setUpDecoViewArc(getActivity(), dynamicArc);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewChart_Activity.class);
                intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_NUTRIITION_CHART);
                startActivityForResult(intent,
                        ADD_NEW_NUTRITION_CHART_REQUEST);
            }
        });

        addNewEntryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewNutritionElement(chartTitleTV.getText().toString());
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ADD_NEW_NUTRITION_CHART_REQUEST:
                if (data != null) {
                    addNewBarChart(data.getStringExtra(Constants.EXTRAS_CHART_TYPE_SELECTED));
                }
                break;
        }
    }

    private void addNewNutritionElement(String mainMealName) {
        Intent intent = new Intent(getActivity(), AddNewMealItem_Activity.class);
        intent.putExtra(Constants.EXTRAS_MAIN_MEAL_NAME, mainMealName);
        startActivity(intent);
    }

    private void addNewBarChart(String chartTitle) {
        final View barChartLayout = getActivity().getLayoutInflater().inflate(R.layout.view_barchart_container, null);

        Button removeChartBTN = (Button) barChartLayout.findViewById(R.id.removeChartBTN);
        final CardView cardLayout = (CardView) barChartLayout.findViewById(R.id.cardLayoutContainer);
        TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
        BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);

        if (chartTitle != null)
            chartTitleTV.setText(chartTitle);

        Helpers.setChartData(barChart, 10, 10);

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

}