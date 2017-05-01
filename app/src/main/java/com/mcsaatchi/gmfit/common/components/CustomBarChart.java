package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.common.classes.CustomBarChartRenderer;
import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;

import static com.mcsaatchi.gmfit.common.Constants.EXTRAS_FITNESS_FRAGMENT;
import static com.mcsaatchi.gmfit.common.Constants.EXTRAS_NUTRITION_FRAGMENT;

public class CustomBarChart extends BarChart {

  @Bind(R.id.barChart) BarChart barChart;
  @Bind(R.id.chartTitleTV) TextView chartTitleTV;
  @Bind(R.id.dateTV_1) TextView dateTV_1;
  @Bind(R.id.dateTV_2) TextView dateTV_2;
  @Bind(R.id.dateTV_3) TextView dateTV_3;
  @Bind(R.id.dateTV_4) TextView dateTV_4;
  @Bind(R.id.dateTV_5) TextView dateTV_5;
  @Bind(R.id.chartHeader) RelativeLayout chartHeader;

  private View barChartLayout;

  private DataChart chartObject;

  private Context context;
  private ArrayList<CustomBarChartClickListener> clickListeners = new ArrayList<>();

  public CustomBarChart(Context context, DataChart chartObject) {
    super(context);
    this.context = context;

    this.chartObject = chartObject;

    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    barChartLayout = mInflater.inflate(R.layout.view_barchart_container, null);

    ButterKnife.bind(this, barChartLayout);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            getResources().getDimensionPixelSize(R.dimen.chart_height_2));
    lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);

    barChartLayout.setLayoutParams(lp);

    chartTitleTV.setText(chartObject.getName());
  }

  private static double findLargestNumber(List<AuthenticationResponseChartData> rawChartData) {
    double smallest = Double.parseDouble(rawChartData.get(0).getValue());
    double largest = Double.parseDouble(rawChartData.get(0).getValue());

    for (int i = 1; i < rawChartData.size(); i++) {
      double currentValue = Double.parseDouble(rawChartData.get(i).getValue());
      if (currentValue > largest) {
        largest = currentValue;
      } else if (currentValue < smallest) smallest = currentValue;
    }

    return largest;
  }

  public void addClickListener(CustomBarChartClickListener listener) {
    this.clickListeners.add(listener);
  }

  public void setBarChartDataAndDates(LinearLayout barContainer,
      List<AuthenticationResponseChartData> newChartData, String whichFragment) {

    DateTime date;

    Collections.reverse(newChartData);

    for (int i = 0; i < newChartData.size(); i++) {
      date = new DateTime(newChartData.get(i).getDate());

      switch (i) {
        case 3:
          dateTV_1.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 10:
          dateTV_2.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 17:
          dateTV_3.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 24:
          dateTV_4.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 31:
          dateTV_5.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
      }
    }

    setBarChartData(barContainer, newChartData, whichFragment);
  }

  private void setBarChartData(LinearLayout barContainer,
      List<AuthenticationResponseChartData> chartData, String whichFragment) {
    ArrayList<BarEntry> valsMetrics = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();

    int k = 0;

    for (int i = 0; i < chartData.size(); i++) {
      xVals.add("");

      BarEntry val1 = new BarEntry(Float.parseFloat(chartData.get(i).getValue()), k);
      valsMetrics.add(val1);

      k++;
    }

    if (whichFragment.equals(EXTRAS_NUTRITION_FRAGMENT)) {
      chartHeader.setBackgroundColor(context.getResources().getColor(R.color.nutrition_red));
    } else if (whichFragment.equals(EXTRAS_FITNESS_FRAGMENT)) {
      chartHeader.setBackgroundColor(context.getResources().getColor(R.color.fitness_pink));
    }

    BarDataSet set1;
    set1 = new BarDataSet(valsMetrics, null);
    set1.setColor(R.color.fitness_pink);
    set1.setHighLightAlpha(1);
    set1.setValueTextSize(7f);

    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    dataSets.add(set1);

    BarData data = new BarData(xVals, dataSets);

    barChart.setScaleEnabled(false);
    barChart.setDescription(null);
    barChart.setDrawGridBackground(false);
    barChart.getLegend().setEnabled(false);

    barChart.getAxisRight().setEnabled(false);
    barChart.getAxisLeft().setEnabled(false);

    barChart.getXAxis().setDrawGridLines(false);

    barChart.getXAxis().setEnabled(false);

    barChart.getAxisLeft().setAxisMaxValue((float) findLargestNumber(chartData));
    barChart.getAxisLeft().setShowOnlyMinMax(true);
    barChart.getAxisRight().setShowOnlyMinMax(true);

    barChart.getAxisLeft().setDrawLabels(false);
    barChart.getAxisRight().setDrawLabels(false);

    barChart.getAxisRight().setAxisMinValue(0);
    barChart.getAxisLeft().setAxisMinValue(0);

    barChart.setData(data);

    barChart.getBarData().setDrawValues(false);

    barChart.setRenderer(
        new CustomBarChartRenderer(context, whichFragment, barChart, barChart.getAnimator(),
            barChart.getViewPortHandler()));

    barChart.invalidate();

    for (final CustomBarChartClickListener listener : clickListeners) {
      barChart.setOnClickListener(view -> listener.handleClick(chartObject));
    }

    barContainer.addView(barChartLayout);
  }

  public interface CustomBarChartClickListener {
    void handleClick(DataChart chartObject);
  }
}
