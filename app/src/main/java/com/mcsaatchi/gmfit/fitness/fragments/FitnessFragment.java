package com.mcsaatchi.gmfit.fitness.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.CaloriesCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.otto.DataChartDeletedEvent;
import com.mcsaatchi.gmfit.architecture.otto.DataChartsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.DistanceCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.FitnessWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.StepCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.AddNewChartActivity;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.activities.SlugBreakdownActivity;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.components.CustomBarChart;
import com.mcsaatchi.gmfit.common.components.DateCarousel;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.fitness.adapters.FitnessWidgetsRecyclerAdapter;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.squareup.otto.Subscribe;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class FitnessFragment extends Fragment {

  private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;

  @Bind(R.id.dateCarouselLayout) DateCarousel dateCarouselLayout;
  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
  @Bind(R.id.cards_container) LinearLayout cards_container;
  @Bind(R.id.addChartBTN) Button addNewChartBTN;
  @Bind(R.id.metricCounterTV) FontTextView metricCounterTV;
  @Bind(R.id.goalTV) FontTextView goalTV;
  @Bind(R.id.goalStatusWordTV) TextView goalStatusWordTV;
  @Bind(R.id.remainingTV) FontTextView remainingTV;
  @Bind(R.id.todayTV) FontTextView todayTV;
  @Bind(R.id.metricProgressBar) ProgressBar metricProgressBar;
  @Bind(R.id.loadingMetricProgressBar) ProgressBar loadingMetricProgressBar;

  @Inject SharedPreferences prefs;
  @Inject LocalDate dt;
  @Inject DataAccessHandler dataAccessHandler;
  private String todayDate;

  private FitnessWidgetsRecyclerAdapter widgetsGridAdapter;
  private ArrayList<FitnessWidget> widgetsMap;
  private ArrayList<DataChart> chartsMap;
  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO;
  private QueryBuilder<FitnessWidget, Integer> fitnessQB;

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override public void onAttach(Context context) {
    super.onAttach(context);

    fitnessWidgetsDAO = ((BaseActivity) getActivity()).dbHelper.getFitnessWidgetsDAO();
    fitnessQB = fitnessWidgetsDAO.queryBuilder();

    EventBusSingleton.getInstance().register(this);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    todayDate = dt.toString();

    getUserGoalMetrics(todayDate, "fitness", false);

    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(R.string.fitness_tab_title);
    }

    if (getArguments() != null) {
      chartsMap = getArguments().getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);
    }

    try {
      widgetsMap = (ArrayList<FitnessWidget>) fitnessQB.orderBy("position", true).query();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);

    ButterKnife.bind(this, fragmentView);

    setHasOptionsMenu(true);

    metricCounterTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));
    todayTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));

    setupWidgetViews(widgetsMap);

    setupChartViews(chartsMap, todayDate);

    dateCarouselLayout.addClickListener(new DateCarousel.CarouselClickListener() {
      @Override public void handleClick(String todayDate, String finalDate) {
        DateTime finalTodayDateTime = new DateTime(todayDate);
        DateTime finalDesiredDateTime = new DateTime(finalDate);

        getUserGoalMetrics(finalDate, "fitness", !finalTodayDateTime.isEqual(finalDesiredDateTime));

        getWidgetsWithDate(finalDate);
        setupChartViews(chartsMap, finalDate);
      }
    });

    dateCarouselLayout.post(new Runnable() {
      @Override public void run() {
        dateCarouselLayout.setSmoothScrollingEnabled(true);
        dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
      }
    });

    Timber.d("onCreateView: Device info : %s %s (%s) - %s", Build.MANUFACTURER, Build.MODEL,
        Build.DEVICE, Build.VERSION.RELEASE);

    addNewChartBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddNewChartActivity.class);
        intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_FITNESS_CHART);
        intent.putExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, chartsMap);
        startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
      }
    });

    return fragmentView;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ADD_NEW_FITNESS_CHART_REQUEST_CODE) {
      if (data != null) {

        DataChart chartObjectAdded = data.getParcelableExtra(Constants.EXTRAS_CHART_OBJECT);

        chartsMap.add(chartObjectAdded);

        addNewBarChart(chartObjectAdded, todayDate);
      }
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.main, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings:
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndChartsActivity.class);
        intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_FITNESS_FRAGMENT);
        intent.putExtra(Constants.BUNDLE_FITNESS_WIDGETS_MAP, widgetsMap);
        intent.putExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, chartsMap);
        startActivity(intent);
        break;
      case R.id.calendarToday:
        LinearLayout dateCarouselContainer =
            (LinearLayout) dateCarouselLayout.findViewById(R.id.dateCarouselContainer);

        dateCarouselContainer.removeAllViews();
        dateCarouselLayout.setupDateCarousel();

        getUserGoalMetrics(todayDate, "fitness", false);

        getWidgetsWithDate(todayDate);
        setupChartViews(chartsMap, todayDate);

        dateCarouselLayout.post(new Runnable() {
          @Override public void run() {
            dateCarouselLayout.setSmoothScrollingEnabled(true);
            dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
          }
        });

        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  private void getPeriodicalChartData(final DataChart chartObject, String desiredDate) {
    dataAccessHandler.getPeriodicalChartData(dt.minusMonths(1).toString(), desiredDate, "fitness",
        chartObject.getType(), new Callback<ChartMetricBreakdownResponse>() {
          @Override public void onResponse(Call<ChartMetricBreakdownResponse> call,
              Response<ChartMetricBreakdownResponse> response) {
            if (response.code() == 200) {
              List<ChartMetricBreakdownResponseDatum> rawChartData =
                  response.body().getData().getBody().getData();

              List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

              for (ChartMetricBreakdownResponseDatum responseDatum : rawChartData) {
                newChartData.add(new AuthenticationResponseChartData(responseDatum.getDate(),
                    responseDatum.getValue()));
              }

              if (getActivity() != null) {
                CustomBarChart customBarChart = new CustomBarChart(getActivity(), chartObject);

                /**
                 * Open the breakdown for the chart
                 */
                customBarChart.addClickListener(new CustomBarChart.CustomBarChartClickListener() {
                  @Override public void handleClick(DataChart chartObject) {
                    getSlugBreakdownForChart(chartObject);
                  }
                });

                customBarChart.setBarChartDataAndDates(cards_container, newChartData,
                    Constants.EXTRAS_FITNESS_FRAGMENT);
              }
            }
          }

          @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
          }
        });
  }

  public void getWidgetsWithDate(final String finalDate) {
    dataAccessHandler.getWidgetsWithDate("fitness", finalDate, new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        switch (response.code()) {
          case 200:
            List<WidgetsResponseDatum> widgetsFromResponse =
                response.body().getData().getBody().getData();

            ArrayList<FitnessWidget> newWidgetsMap = new ArrayList<>();

            for (int i = 0; i < widgetsFromResponse.size(); i++) {
              FitnessWidget widget = new FitnessWidget();

              if (!widgetsFromResponse.get(i).getName().equals("Steps Count")) {
                widget.setId(widgetsFromResponse.get(i).getWidgetId());
                widget.setMeasurementUnit(widgetsFromResponse.get(i).getUnit());
                widget.setPosition(i);
                widget.setValue(Float.parseFloat(widgetsFromResponse.get(i).getTotal()));
                widget.setTitle(widgetsFromResponse.get(i).getName());

                newWidgetsMap.add(widget);
              }
            }

            setupWidgetViews(newWidgetsMap);

            break;
        }
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
      }
    });
  }

  public void getUserGoalMetrics(String date, String type, final boolean requestingPreviousData) {
    if (loadingMetricProgressBar != null) {
      loadingMetricProgressBar.setVisibility(View.VISIBLE);
      metricCounterTV.setVisibility(View.INVISIBLE);
    }

    dataAccessHandler.getUserGoalMetrics(date, type, new Callback<UserGoalMetricsResponse>() {
      @Override public void onResponse(Call<UserGoalMetricsResponse> call,
          Response<UserGoalMetricsResponse> response) {

        switch (response.code()) {
          case 200:
            String maxValue =
                response.body().getData().getBody().getMetrics().getStepsCount().getMaxValue();

            String currentValue =
                response.body().getData().getBody().getMetrics().getStepsCount().getValue();

            int remainingValue = 0;

            goalTV.setText(Helpers.getFormattedString(Integer.parseInt(maxValue)));

            metricProgressBar.setProgress(
                (int) ((Double.parseDouble(currentValue) * 100) / Double.parseDouble(maxValue)));

            /**
             * Requesting today's data
             */
            if (!requestingPreviousData) {
              metricCounterTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));
              todayTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));

              remainingValue = Integer.parseInt(maxValue) - Helpers.getNumberFromFromattedString(
                  metricCounterTV.getText().toString());
            } else {
              /**
               * Requesting data from previous days
               */
              metricCounterTV.setText(
                  Helpers.getFormattedString((int) Double.parseDouble(currentValue)));
              todayTV.setText(Helpers.getFormattedString((int) Double.parseDouble(currentValue)));

              remainingValue =
                  (int) (Integer.parseInt(maxValue) - Double.parseDouble(currentValue));
            }

            if (loadingMetricProgressBar != null) {
              loadingMetricProgressBar.setVisibility(View.INVISIBLE);
            }

            metricCounterTV.setVisibility(View.VISIBLE);

            if (remainingValue < 0) {
              goalStatusWordTV.setText(getResources().getString(R.string.goal_exceeded_tv));
            } else {
              if (isAdded()) {
                goalStatusWordTV.setText(getResources().getString(R.string.remaining_title));
              }
            }

            remainingTV.setText(Helpers.getFormattedString(Math.abs(remainingValue)));

            break;
        }
      }

      @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
      }
    });
  }

  public void addNewBarChart(DataChart chartObject, String desiredDate) {
    switch (chartObject.getType()) {
      case "steps-count":
        getPeriodicalChartData(chartObject, desiredDate);
        break;
      case "active-calories":
        getPeriodicalChartData(chartObject, desiredDate);
        break;
      case "distance-traveled":
        getPeriodicalChartData(chartObject, desiredDate);
        break;
    }
  }

  private void getSlugBreakdownForChart(final DataChart chartObject) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(
        getActivity().getResources().getString(R.string.grabbing_breakdown_data_dialog_title));
    waitingDialog.setMessage(
        getActivity().getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.grabbing_breakdown_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        getActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getSlugBreakdownForChart(chartObject.getType(),
        new Callback<SlugBreakdownResponse>() {
          @Override public void onResponse(Call<SlugBreakdownResponse> call,
              Response<SlugBreakdownResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Intent intent = new Intent(getActivity(), SlugBreakdownActivity.class);
                intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_FITNESS_FRAGMENT);
                intent.putExtra(Constants.EXTRAS_CHART_OBJECT, chartObject);
                intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA,
                    response.body().getData().getBody().getData());
                getActivity().startActivity(intent);
                break;
            }
          }

          @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
          }
        });
  }

  public void setupChartViews(List<DataChart> dataChartsMap, String desiredDate) {
    cards_container.removeAllViews();

    if (!dataChartsMap.isEmpty()) {
      for (DataChart chart : dataChartsMap) {
        addNewBarChart(chart, desiredDate);
      }
    }
  }

  private void setupWidgetViews(ArrayList<FitnessWidget> widgetsMap) {
    widgetsGridAdapter = new FitnessWidgetsRecyclerAdapter(getActivity(), widgetsMap,
        R.layout.grid_item_fitness_widgets);

    widgetsGridView.setLayoutManager(new GridLayoutManager(getActivity(), widgetsMap.size()));
    widgetsGridView.setAdapter(widgetsGridAdapter);
  }

  private TextView findWidgetInGrid(String widgetName) {
    RecyclerView.ViewHolder fitnessWidgetViewHolder;
    TextView metricCountTextView = null;

    for (int i = 0; i < widgetsGridAdapter.getItemCount(); i++) {
      if (widgetsGridAdapter.getItem(i).getTitle().equals(widgetName)) {
        fitnessWidgetViewHolder = widgetsGridView.findViewHolderForAdapterPosition(i);

        if (fitnessWidgetViewHolder != null) {
          metricCountTextView =
              (TextView) fitnessWidgetViewHolder.itemView.findViewById(R.id.metricTV);
        }
      }
    }
    return metricCountTextView;
  }

  @Subscribe public void updateWidgetsOrder(FitnessWidgetsOrderChangedEvent event) {
    widgetsMap = event.getWidgetsMapFitness();
    setupWidgetViews(widgetsMap);
  }

  @Subscribe public void chartDeleted(DataChartDeletedEvent event) {

    DataChart chartObject = event.getChartObject();

    deleteUserChart(chartObject);
  }

  @Subscribe public void updateChartsOrder(DataChartsOrderChangedEvent event) {
    List<DataChart> allDataCharts = event.getDataChartsListExtra();

    cards_container.removeAllViews();

    chartsMap = (ArrayList<DataChart>) allDataCharts;

    setupChartViews(chartsMap, todayDate);

    int[] charts = new int[allDataCharts.size()];
    int[] chartPositions = new int[allDataCharts.size()];

    for (int i = 0; i < allDataCharts.size(); i++) {
      charts[i] = allDataCharts.get(i).getChart_id();
      chartPositions[i] = allDataCharts.get(i).getPosition();
    }

    updateUserCharts(charts, chartPositions);
  }

  @Subscribe public void incrementStepCounter(StepCounterIncrementedEvent event) {
    metricCounterTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));
    todayTV.setText(Helpers.getFormattedString(prefs.getInt("steps_taken", 0)));

    if (!goalTV.getText().toString().isEmpty()
        && !todayTV.getText().toString().isEmpty()
        && !metricCounterTV.getText().toString().isEmpty()) {

      int remainingValue;

      remainingValue = Helpers.getNumberFromFromattedString(goalTV.getText().toString())
          - Helpers.getNumberFromFromattedString(metricCounterTV.getText().toString());

      if (remainingValue < 0) {
        goalStatusWordTV.setText(getResources().getString(R.string.goal_exceeded_tv));
      } else {
        goalStatusWordTV.setText(getResources().getString(R.string.remaining_title));

        metricProgressBar.setProgress(
            (Helpers.getNumberFromFromattedString(todayTV.getText().toString()) * 100)
                / Helpers.getNumberFromFromattedString(goalTV.getText().toString()));
      }

      remainingTV.setText(Helpers.getFormattedString(Math.abs(remainingValue)));
    }
  }

  @Subscribe public void incrementDistanceCounter(DistanceCounterIncrementedEvent event) {
    TextView fitnessWidget = findWidgetInGrid("Distance Traveled");
    fitnessWidget.setText(
        new DecimalFormat("##.##").format((prefs.getFloat("distance_traveled", 0))));
  }

  @Subscribe public void incrementCaloriesCounter(CaloriesCounterIncrementedEvent event) {
    TextView fitnessWidget = findWidgetInGrid("Active Calories");
    fitnessWidget.setText(new DecimalFormat("##.#").format(prefs.getFloat("calories_spent", 0)));
  }

  private void updateUserCharts(int[] chartIds, int[] chartPositions) {
    dataAccessHandler.updateUserCharts(chartIds, chartPositions,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Timber.d("onResponse: User's charts updated successfully");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

          }
        });
  }

  private void deleteUserChart(final DataChart chartObject) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(
        getActivity().getResources().getString(R.string.deleting_chart_dialog_title));
    waitingDialog.setMessage(
        getActivity().getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.deleting_chart_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        getActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.deleteUserChart(String.valueOf(chartObject.getChart_id()),
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Toast.makeText(getActivity(), "Chart deleted successfully", Toast.LENGTH_SHORT)
                    .show();

                cards_container.removeAllViews();

                for (int i = 0; i < chartsMap.size(); i++) {
                  if (chartsMap.get(i).getName().equals(chartObject.getName())) {
                    chartsMap.remove(i);
                  }
                }

                setupChartViews(chartsMap, todayDate);

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
          }
        });
  }
}