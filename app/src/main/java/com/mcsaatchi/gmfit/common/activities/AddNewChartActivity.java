package com.mcsaatchi.gmfit.common.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.adapters.DataChartsListing_Adapter;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.architecture.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartsBySectionResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewChartActivity extends Base_Activity {
  private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
  private static final int ADD_NEW_NUTRITION_CHART_REQUEST_CODE = 2;

  @Bind(R.id.chartsList) ListView chartsList;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.topLayout) LinearLayout topLayout;

  private List<DataChart> chartItemsMap = new ArrayList<>();

  private ProgressDialog waitingDialog;
  private AlertDialog alertDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_chart);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.add_new_chart_activity_title), true);

    Bundle extras = getIntent().getExtras();

    waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.fetching_chart_data_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.fetching_chart_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    if (extras != null) {
      String CALL_PURPOSE = extras.getString(Constants.EXTRAS_ADD_CHART_WHAT_TYPE);

      if (CALL_PURPOSE != null) {
        switch (CALL_PURPOSE) {
          case Constants.EXTRAS_ADD_FITNESS_CHART:
            topLayout.setBackground(getResources().getDrawable(R.drawable.fitness_background));

            getChartsBySection("fitness", ADD_NEW_FITNESS_CHART_REQUEST_CODE);
            break;
          case Constants.EXTRAS_ADD_NUTRIITION_CHART:
            topLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));

            getChartsBySection("nutrition", ADD_NEW_NUTRITION_CHART_REQUEST_CODE);
            break;
        }
      }
    }
  }

  private void getChartsBySection(String sectionName, final int requestCodeToSendBack) {
    dataAccessHandler.getChartsBySection(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        sectionName, new Callback<ChartsBySectionResponse>() {
          @Override public void onResponse(Call<ChartsBySectionResponse> call,
              Response<ChartsBySectionResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                List<ChartsBySectionResponseDatum> chartsFromResponse =
                    response.body().getData().getBody().getData();

                for (int i = 0; i < chartsFromResponse.size(); i++) {
                  DataChart newChartToAdd = new DataChart();

                  newChartToAdd.setType(chartsFromResponse.get(i).getSlug());
                  newChartToAdd.setName(chartsFromResponse.get(i).getName());
                  newChartToAdd.setMeasurementUnit(chartsFromResponse.get(i).getUnit());
                  newChartToAdd.setChart_id(chartsFromResponse.get(i).getId());

                  chartItemsMap.add(newChartToAdd);
                }

                chartsList.setAdapter(new DataChartsListing_Adapter(chartItemsMap, AddNewChartActivity.this));

                chartsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int position,
                      long l) {

                    DataChart dataChart = chartItemsMap.get(position);

                    if (!dataChart.getName().equals("Number of Steps")) {

                      addMetricChart(dataChart.getChart_id());

                      Intent intent = new Intent();
                      intent.putExtra(Constants.EXTRAS_CHART_FULL_NAME, dataChart.getName());
                      intent.putExtra(Constants.EXTRAS_CHART_TYPE_SELECTED, dataChart.getType());
                      setResult(requestCodeToSendBack, intent);

                      finish();
                    } else {
                      Toast.makeText(AddNewChartActivity.this, R.string.duplicate_chart_error,
                          Toast.LENGTH_SHORT).show();
                    }
                  }
                });

                break;
            }
          }

          @Override public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void addMetricChart(int chart_id) {
    dataAccessHandler.addMetricChart(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        chart_id, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Timber.d("onResponse: Successfully add a new chart!");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }
}