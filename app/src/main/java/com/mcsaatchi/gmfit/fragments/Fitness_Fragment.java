package com.mcsaatchi.gmfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    private NestedScrollView parentScrollView;
    private LinearLayout cards_container;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);
        parentScrollView = (NestedScrollView) getActivity().findViewById(R.id.myScrollingContent);
        cards_container = (LinearLayout) fragmentView.findViewById(R.id.cards_container);

        BarChart barChart = (BarChart) fragmentView.findViewById(R.id.bar_chart);
        Button addNewChartBTN = (Button) fragmentView.findViewById(R.id.addChartBTN);
        setHasOptionsMenu(true);

        setUpDecoViewArc();

        setChartData(barChart, 50, 50);

        addNewChartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CardView cardView = new CardView(getActivity());
                cardView.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDimensionPixelSize(R.dimen.chart_height)));
                cardView.setUseCompatPadding(true);
                cardView.setCardElevation(getResources().getDimensionPixelSize(R.dimen.cardview_default_elevation));
                cardView.setRadius(getResources().getDimensionPixelSize(R.dimen.cardview_default_radius));

                final BarChart newBarChart = new BarChart(getActivity());
                newBarChart.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
                setChartData(newBarChart, 10, 10);

                Button removeChartBTN = new Button(getActivity());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.remove_chart_button_dimens),
                        getResources().getDimensionPixelSize(R.dimen.remove_chart_button_dimens));

                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                removeChartBTN.setLayoutParams(lp);
                removeChartBTN.setText("X");

                cardView.addView(removeChartBTN);
                cardView.addView(newBarChart);

                removeChartBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cards_container.removeView(cardView);
                    }
                });

                cards_container.addView(cardView);

                parentScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parentScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 500);
            }
        });

        return fragmentView;
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
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription("Chart Data");
        chart.setData(data);
        chart.invalidate();
    }
}