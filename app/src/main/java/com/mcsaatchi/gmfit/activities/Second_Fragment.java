package com.mcsaatchi.gmfit.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mcsaatchi.gmfit.R;

public class Second_Fragment extends Fragment {

    //    @Bind(R.id.stepsChart)
    private BarChart stepsChart;
    //    @Bind(R.id.dynamicArcView)
    private DecoView dynamicArc;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

        stepsChart = (BarChart) fragmentView.findViewById(R.id.stepsChart);
        dynamicArc = (DecoView) fragmentView.findViewById(R.id.dynamicArcView);

        setUpDecoViewArc();


        return fragmentView;
    }

    private void setUpDecoViewArc(){
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

        stepsChart.setDescription("Number of Steps");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        stepsChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        stepsChart.setPinchZoom(true);

        stepsChart.setDrawBarShadow(false);
        stepsChart.setDrawGridBackground(false);

        XAxis xAxis = stepsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        stepsChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        stepsChart.animateY(2500);

        stepsChart.getLegend().setEnabled(false);

        Legend l = stepsChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

//         stepsChart.setDrawLegend(false);

    }

//    public void setBarData() {
//        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
//
//        for (int i = 0; i < 100 + 1; i++) {
//            float mult = (100 + 1);
//            float val1 = (float) (Math.random() * mult) + mult / 3;
//            yVals1.add(new BarEntry((int) val1, i));
//        }
//
//        ArrayList<String> xVals = new ArrayList<String>();
//        for (int i = 0; i < 100 + 1; i++) {
//            xVals.add((int) yVals1.get(i).getVal() + "");
//        }
//
//        BarDataSet set1;
//
//        if (stepsChart.getData() != null &&
//                stepsChart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) stepsChart.getData().getDataSetByIndex(0);
//            set1.setsetYVals(yVals1);
//            stepsChart.getData().setXVals(xVals);
//            stepsChart.notifyDataSetChanged();
//        } else {
//            set1 = new BarDataSet(yVals1, "Data Set");
//            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
//            set1.setDrawValues(false);
//
//            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//            dataSets.add(set1);
//
//            BarData data = new BarData(xVals, dataSets);
//
//            mChart.setData(data);
//            mChart.invalidate();
//        }
//    }
}