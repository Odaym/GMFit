package com.mcsaatchi.gmfit.common.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WeightHistoryResponseDatum;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CustomLineChart extends LineChart {
  @Bind(R.id.lineChart) LineChart lineChart;

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
      DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");

      DateTime date = formatter.parseDateTime(weights.get(i).getDate());

      xVals.add(date.getDayOfMonth() + " " + date.monthOfYear().getAsText().substring(0, 3));

      entries.add(
          new Entry(Float.parseFloat(String.format("%.1f", weights.get(i).getWeight())), i));
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
    lineChart.getAxisLeft().setDrawLabels(true);
    lineChart.getAxisRight().setDrawLabels(false);
    lineChart.getLineData().setHighlightEnabled(false);

    lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    lineChart.getXAxis().setSpaceBetweenLabels(0);
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
