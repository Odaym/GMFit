package com.mcsaatchi.gmfit.fitness.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.j256.ormlite.dao.RuntimeExceptionDao;
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
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponseInnerData;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.AddNewChartActivity;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.activities.SlugBreakdownActivity;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.components.CustomBarChart;
import com.mcsaatchi.gmfit.common.components.DateCarousel;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.fitness.adapters.FitnessWidgetsRecyclerAdapter;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.fitness.presenters.FitnessFragmentPresenter;
import com.squareup.otto.Subscribe;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import timber.log.Timber;

public class FitnessFragment extends BaseFragment
    implements FitnessFragmentPresenter.FitnessFragmentView {

  private static final int ADD_NEW_FITNESS_CHART_REQUEST_CODE = 1;
  @Bind(R.id.dateCarouselLayout) DateCarousel dateCarouselLayout;
  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
  @Bind(R.id.cards_container) LinearLayout cards_container;
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
  @Inject RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO;
  private FitnessWidgetsRecyclerAdapter widgetsGridAdapter;
  private ArrayList<FitnessWidget> widgetsMap;
  private ArrayList<DataChart> chartsMap;
  private String todayDate;
  private FitnessFragmentPresenter presenter;

  @RequiresApi(api = Build.VERSION_CODES.KITKAT) @Override public void onAttach(Context context) {
    super.onAttach(context);

    EventBusSingleton.getInstance().register(this);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    todayDate = dt.toString();

    presenter = new FitnessFragmentPresenter(this, fitnessWidgetsDAO, dataAccessHandler);

    presenter.getUserGoalMetrics(todayDate, "fitness", false);

    if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
      ((AppCompatActivity) getActivity()).getSupportActionBar()
          .setTitle(R.string.fitness_tab_title);
    }

    if (getArguments() != null) {
      chartsMap = getArguments().getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);
    }

    widgetsMap = presenter.getWidgetsFromDB();
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

    dateCarouselLayout.addClickListener((todayDate1, finalDate) -> {
      DateTime finalTodayDateTime = new DateTime(todayDate1);
      DateTime finalDesiredDateTime = new DateTime(finalDate);

      presenter.getUserGoalMetrics(finalDate, "fitness",
          !finalTodayDateTime.isEqual(finalDesiredDateTime));

      presenter.getWidgetsWithDate(finalDate);

      setupChartViews(chartsMap, finalDate);
    });

    dateCarouselLayout.post(() -> {
      dateCarouselLayout.setSmoothScrollingEnabled(true);
      dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    });

    Timber.d("onCreateView: Device info : %s %s (%s) - %s", Build.MANUFACTURER, Build.MODEL,
        Build.DEVICE, Build.VERSION.RELEASE);

    return fragmentView;
  }

  @OnClick(R.id.addChartBTN) public void handleAddNewChart() {
    Intent intent = new Intent(getActivity(), AddNewChartActivity.class);
    intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_FITNESS_CHART);
    intent.putExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, chartsMap);
    startActivityForResult(intent, ADD_NEW_FITNESS_CHART_REQUEST_CODE);
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

        presenter.getUserGoalMetrics(todayDate, "fitness", false);

        presenter.getWidgetsWithDate(todayDate);

        setupChartViews(chartsMap, todayDate);

        dateCarouselLayout.post(() -> {
          dateCarouselLayout.setSmoothScrollingEnabled(true);
          dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        });

        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void showMetricsLoadingBar() {
    if (loadingMetricProgressBar != null) {
      loadingMetricProgressBar.setVisibility(View.VISIBLE);
      metricCounterTV.setVisibility(View.INVISIBLE);
    }
  }

  @Override public void displayUserGoalMetrics(String maxValue, String currentValue,
      boolean requestingPreviousData) {
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
      metricCounterTV.setText(Helpers.getFormattedString((int) Double.parseDouble(currentValue)));
      todayTV.setText(Helpers.getFormattedString((int) Double.parseDouble(currentValue)));

      remainingValue = (int) (Integer.parseInt(maxValue) - Double.parseDouble(currentValue));
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
  }

  @Override
  public void getBreakDownDataForChart(List<ChartMetricBreakdownResponseDatum> rawChartData,
      DataChart chartObject) {
    List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

    for (ChartMetricBreakdownResponseDatum responseDatum : rawChartData) {
      newChartData.add(
          new AuthenticationResponseChartData(responseDatum.getDate(), responseDatum.getValue()));
    }

    if (getActivity() != null) {
      CustomBarChart customBarChart = new CustomBarChart(getActivity(), chartObject);

      /**
       * Open the breakdown for the chart
       */
      customBarChart.addClickListener(
          chartObjectInner -> presenter.getBreakdownDataForChart(chartObjectInner));

      customBarChart.setBarChartDataAndDates(cards_container, newChartData,
          Constants.EXTRAS_FITNESS_FRAGMENT);
    }
  }

  @Override public void openSlugBreakDownActivity(SlugBreakdownResponseInnerData breakDownData,
      DataChart chartObject) {
    Intent intent = new Intent(getActivity(), SlugBreakdownActivity.class);
    intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_FITNESS_FRAGMENT);
    intent.putExtra(Constants.EXTRAS_CHART_OBJECT, chartObject);
    intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA, breakDownData);
    getActivity().startActivity(intent);
  }

  @Override public void callSetupWidgetViews(List<WidgetsResponseDatum> widgetsFromResponse) {
    ArrayList<FitnessWidget> newWidgetsMap = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
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
  }

  @Override public void callSetupChartViews(DataChart chartObject, String todayDate) {
    Toast.makeText(getActivity(), "Chart deleted successfully", Toast.LENGTH_SHORT)
        .show();

    cards_container.removeAllViews();

    for (int i = 0; i < chartsMap.size(); i++) {
      if (chartsMap.get(i).getName().equals(chartObject.getName())) {
        chartsMap.remove(i);
      }
    }

    setupChartViews(chartsMap, todayDate);
  }

  public void addNewBarChart(DataChart chartObject, String desiredDate) {
    switch (chartObject.getType()) {
      case "steps-count":
      case "active-calories":
      case "distance-traveled":
        presenter.getPeriodicalChartData(chartObject, desiredDate, dt.minusMonths(1).toString());
        break;
    }
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
    presenter.deleteUserChart(chartObject, todayDate);
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

    presenter.updateUserCharts(charts, chartPositions);
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
}