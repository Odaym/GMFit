package com.mcsaatchi.gmfit.common.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartsBySectionResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.adapters.DataChartsListingAdapter;
import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.ArrayList;
import java.util.List;

public class AddNewChartActivity extends BaseActivity
    implements AddNewChartActivityPresenter.AddNewChartActivityView {
  private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
  private static final int ADD_NEW_NUTRITION_CHART_REQUEST_CODE = 2;

  @Bind(R.id.chartsList) ListView chartsList;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.topLayout) LinearLayout topLayout;

  private List<DataChart> chartItemsMap = new ArrayList<>();

  private AddNewChartActivityPresenter presenter;
  private ArrayList<DataChart> chartsMap;
  private ProgressDialog waitingDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_chart);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.add_new_chart_activity_title), true);

    presenter = new AddNewChartActivityPresenter(this, dataAccessHandler);

    Bundle extras = getIntent().getExtras();

    waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.fetching_chart_data_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.fetching_chart_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();

      if (waitingDialog.isShowing()) waitingDialog.dismiss();
    });

    if (extras != null) {
      String CALL_PURPOSE = extras.getString(Constants.EXTRAS_ADD_CHART_WHAT_TYPE);

      if (CALL_PURPOSE != null) {
        switch (CALL_PURPOSE) {
          case Constants.EXTRAS_ADD_FITNESS_CHART:
            topLayout.setBackground(getResources().getDrawable(R.drawable.fitness_background));

            chartsMap =
                getIntent().getExtras().getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);

            presenter.getChartsBySection("fitness", ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            break;
          case Constants.EXTRAS_ADD_NUTRIITION_CHART:
            topLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));

            chartsMap = getIntent().getExtras()
                .getParcelableArrayList(Constants.BUNDLE_NUTRITION_CHARTS_MAP);

            presenter.getChartsBySection("nutrition", ADD_NEW_NUTRITION_CHART_REQUEST_CODE);
            break;
        }
      }
    }
  }

  @Override
  public void displayChartsBySection(List<ChartsBySectionResponseDatum> chartsFromResponse,
      int requestCodeToSendBack) {
    for (int i = 0; i < chartsFromResponse.size(); i++) {
      DataChart newChartToAdd = new DataChart();

      newChartToAdd.setType(chartsFromResponse.get(i).getSlug());
      newChartToAdd.setName(chartsFromResponse.get(i).getName());
      newChartToAdd.setMeasurementUnit(chartsFromResponse.get(i).getUnit());
      newChartToAdd.setChart_id(chartsFromResponse.get(i).getId());

      chartItemsMap.add(newChartToAdd);
    }

    chartsList.setAdapter(new DataChartsListingAdapter(chartItemsMap, AddNewChartActivity.this));

    chartsList.setOnItemClickListener((adapterView, view, position, l) -> {

      DataChart dataChart = chartItemsMap.get(position);

      boolean chartExists = false;

      for (int i = 0; i < chartsMap.size(); i++) {
        if (dataChart.getName().equals(chartsMap.get(i).getName())) {
          chartExists = true;
        }
      }

      if (chartExists) {
        Toast.makeText(AddNewChartActivity.this, R.string.duplicate_chart_error, Toast.LENGTH_SHORT)
            .show();
      } else {
        presenter.addMetricChart(dataChart.getChart_id());

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRAS_CHART_OBJECT, dataChart);
        setResult(requestCodeToSendBack, intent);

        finish();
      }
    });
  }

  @Override public void dismissWaitingDialog() {
    waitingDialog.dismiss();
  }
}
