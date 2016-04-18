package com.mcsaatchi.gmfit.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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

    private BarChart stepsChart;
    private HorizontalBarChart barLineChartBase;
    private DecoView dynamicArc;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        stepsChart = (BarChart) fragmentView.findViewById(R.id.chart_1);
        barLineChartBase = (HorizontalBarChart) fragmentView.findViewById(R.id.chart_2);
        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);

        setUpDecoViewArc();

        setChartData(stepsChart, 100, 100);
        setChartData(barLineChartBase, 50, 50);

        return fragmentView;
    }

    private void setUpDecoViewArc() {
//        dynamicArc.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
//                .setRange(0, 100, 100)
//                .setInitialVisibility(false)
//                .setLineWidth(32f)
//                .build());

//        Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setInterpolator(new OvershootInterpolator())
                .setLineWidth(32f)
                .build();

        int series1Index = dynamicArc.addSeries(seriesItem1);

//        dynamicArc.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
//                .setDelay(1000)
//                .setDuration(2000)
//                .build());

        dynamicArc.addEvent(new DecoEvent.Builder(75).setIndex(series1Index).build());
//        dynamicArc.addEvent(new DecoEvent.Builder(100).setIndex(series1Index).setDelay(8000).build());
//        dynamicArc.addEvent(new DecoEvent.Builder(10).setIndex(series1Index).setDelay(12000).build());
    }

    public void setChartData(BarChart chart, int xLimits, int yLimits) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < yLimits + 1; i++) {
            float mult = (yLimits + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new BarEntry((int) val1, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xLimits + 1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + "");
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Legend");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription("Chart Data");
        chart.setData(data);
        chart.invalidate();
    }
}