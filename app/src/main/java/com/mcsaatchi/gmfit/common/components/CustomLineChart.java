package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.WeightHistoryResponseDatum;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CustomLineChart extends LineChart {
  @Bind(R.id.lineChart) LineChart lineChart;
  @Bind(R.id.dateTV_1) TextView dateTV_1;
  @Bind(R.id.dateTV_2) TextView dateTV_2;
  @Bind(R.id.dateTV_3) TextView dateTV_3;
  @Bind(R.id.dateTV_4) TextView dateTV_4;
  @Bind(R.id.dateTV_5) TextView dateTV_5;
  @Bind(R.id.dateTV_6) TextView dateTV_6;

  private ArrayList<CustomBarChart.CustomBarChartClickListener> clickListeners = new ArrayList<>();
  private View lineChartLayout;
  private Context context;

  public CustomLineChart(Context context) {
    super(context);
    this.context = context;

    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    lineChartLayout = mInflater.inflate(R.layout.view_linechart_container, null);

    ButterKnife.bind(this, lineChartLayout);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            getResources().getDimensionPixelSize(R.dimen.weight_chart_height));
    lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_1);

    lineChartLayout.setLayoutParams(lp);
  }

  public View getView() {
    return lineChartLayout;
  }

  public void setLineChartData(LinearLayout lineChartContainer,
      List<WeightHistoryResponseDatum> weights) {

    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();

    for (int i = 0; i < weights.size(); i++) {
      DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-mm-dd HH:mm:ss");

      DateTime date = formatter.parseDateTime(weights.get(i).getDate());

      xVals.add("");

      entries.add(new Entry(Float.parseFloat(String.valueOf(weights.get(i).getWeight())), i));
      switch (i) {
        case 0:
          dateTV_1.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 1:
          dateTV_2.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 2:
          dateTV_3.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 3:
          dateTV_4.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
        case 4:
          dateTV_5.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
        case 5:
          dateTV_6.setText(
              date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));
          break;
      }
    }

    LineDataSet dataset = new LineDataSet(entries, null);

    LineData data = new LineData(xVals, dataset);
    dataset.setFillColor(R.color.health_greenish);
    dataset.setLineWidth(1);
    dataset.setCircleColorHole(0);
    dataset.setCircleRadius(6);

    lineChart.setData(data);
    lineChart.animateY(1500);

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
    lineChart.getLineData().setHighlightEnabled(false);

    lineChart.getLineData().setDrawValues(true);

    lineChartContainer.addView(lineChartLayout);
  }

  public void addClickListener(CustomBarChart.CustomBarChartClickListener listener) {
    this.clickListeners.add(listener);
  }

  public interface CustomBarChartClickListener {
    void handleClick(String chartTitle, String chartType);
  }
}
