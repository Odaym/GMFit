package com.mcsaatchi.gmfit.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mcsaatchi.gmfit.R;

import java.util.ArrayList;

public class Fitness_Fragment extends Fragment {

    private BarChart stepsChart;
    private DecoView dynamicArc;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        stepsChart = (BarChart) fragmentView.findViewById(R.id.stepsChart);
        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);

        setUpDecoViewArc();

        setBarData(stepsChart);

        return fragmentView;
    }

    private void setUpDecoViewArc() {
        dynamicArc.addSeries(new SeriesItem.Builder(Color.argb(255, 200, 200, 218))
                .setRange(0, 100, 80)
                .setCapRounded(false)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_OUTER, Color.parseColor("#22000000"), 0.4f))
                .setInterpolator(new OvershootInterpolator())
                .setInitialVisibility(true)
                .setLineWidth(32f)
                .build());

        dynamicArc.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                .setDuration(1500)
                .build());
    }

    public void setBarData(BarChart stepsChart) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 100 + 1; i++) {
            float mult = (100 + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new BarEntry((int) val1, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 100 + 1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + "");
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Data Set");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        stepsChart.setData(data);
        stepsChart.invalidate();
    }
}