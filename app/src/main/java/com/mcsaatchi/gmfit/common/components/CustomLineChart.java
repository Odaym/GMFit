package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mcsaatchi.gmfit.R;
import java.util.ArrayList;

public class CustomLineChart extends LineChart {
  private ArrayList<CustomBarChart.CustomBarChartClickListener> clickListeners = new ArrayList<>();
  private View lineChartLayout;

  @Bind(R.id.lineChart) LineChart lineChart;

  public CustomLineChart(Context context) {
    super(context);

    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    lineChartLayout = mInflater.inflate(R.layout.view_linechart_container, null);

    ButterKnife.bind(this, lineChartLayout);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            getResources().getDimensionPixelSize(R.dimen.chart_height_2));
    lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);

    lineChartLayout.setLayoutParams(lp);
  }

  public void setLineChartData(LinearLayout lineChartContainer) {
    ArrayList<Entry> entries = new ArrayList<>();
    entries.add(new Entry(4f, 0));
    entries.add(new Entry(8f, 1));
    entries.add(new Entry(6f, 2));
    entries.add(new Entry(2f, 3));
    entries.add(new Entry(18f, 4));
    entries.add(new Entry(9f, 5));

    LineDataSet dataset = new LineDataSet(entries, "# of Calls");

    ArrayList<String> labels = new ArrayList<String>();
    labels.add("January");
    labels.add("February");
    labels.add("March");
    labels.add("April");
    labels.add("May");
    labels.add("June");

    LineData data = new LineData(labels, dataset);
    dataset.setFillColor(R.color.health_green);
    dataset.setLineWidth(2);
    //dataset.setCircleColorHole(R.color.health_green);
    dataset.setCircleRadius(5);

    lineChart.setScaleEnabled(false);
    lineChart.setDescription(null);
    lineChart.setDrawGridBackground(false);
    lineChart.getLegend().setEnabled(false);
    lineChart.getAxisRight().setEnabled(false);
    lineChart.getAxisLeft().setEnabled(false);
    lineChart.getXAxis().setDrawGridLines(false);
    lineChart.getXAxis().setEnabled(false);
    lineChart.getAxisLeft().setDrawLabels(false);
    lineChart.getAxisRight().setDrawLabels(false);

    lineChart.setData(data);
    lineChart.animateY(1500);

    lineChartContainer.addView(lineChartLayout);
  }

  public void addClickListener(CustomBarChart.CustomBarChartClickListener listener) {
    this.clickListeners.add(listener);
  }

  public interface CustomBarChartClickListener {
    void handleClick(String chartTitle, String chartType);
  }
}
