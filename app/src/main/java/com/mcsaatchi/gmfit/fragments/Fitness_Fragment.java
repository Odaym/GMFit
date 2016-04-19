package com.mcsaatchi.gmfit.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

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

import java.util.ArrayList;

public class Fitness_Fragment extends Fragment {

    private DecoView dynamicArc;
    private LinearLayout cards_container;
    private Button addNewChartBTN;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        BarChart barChart = (BarChart) fragmentView.findViewById(R.id.bar_chart);
        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);
        cards_container = (LinearLayout) fragmentView.findViewById(R.id.cards_container);
        addNewChartBTN = (Button) fragmentView.findViewById(R.id.addChartBTN);

        setUpDecoViewArc();

        setChartData(barChart, 50, 50);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardView cardView = new CardView(getActivity());
                cardView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.chart_height)));
                cardView.setUseCompatPadding(true);
                cardView.setCardElevation(getResources().getDimensionPixelSize(R.dimen.cardview_default_elevation));
                cardView.setRadius(getResources().getDimensionPixelSize(R.dimen.cardview_default_radius));

                BarChart barChart2 = new BarChart(getActivity());
                barChart2.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                setChartData(barChart2, 10, 10);

                cardView.addView(barChart2);

                cards_container.addView(cardView);
                cards_container.invalidate();
            }
        });

        return fragmentView;
    }

    private void setUpDecoViewArc() {
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setInterpolator(new OvershootInterpolator())
                .setLineWidth(32f)
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
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription("Chart Data");
        chart.setData(data);
        chart.invalidate();
    }
}