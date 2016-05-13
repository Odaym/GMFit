package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewChart_Activity;
import com.mcsaatchi.gmfit.classes.Constants;

public class Nutrition_Fragment extends Fragment {

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

    private SparseArray<String[]> itemsMap;

    public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 1;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

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

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), AddNewChart_Activity.class), ADD_NEW_NUTRITION_CHART_REQUEST);
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}