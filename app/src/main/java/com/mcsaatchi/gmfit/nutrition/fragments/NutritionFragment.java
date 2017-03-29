package com.mcsaatchi.gmfit.nutrition.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.DataChartDeletedEvent;
import com.mcsaatchi.gmfit.architecture.otto.DataChartsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MealEntryManipulatedEvent;
import com.mcsaatchi.gmfit.architecture.otto.NutritionWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponseInnerData;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseInner;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleSwipeItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.AddNewChartActivity;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.activities.SlugBreakdownActivity;
import com.mcsaatchi.gmfit.common.classes.AlarmReceiver;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.components.CustomBarChart;
import com.mcsaatchi.gmfit.common.components.DateCarousel;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealItemActivity;
import com.mcsaatchi.gmfit.nutrition.activities.BarcodeCaptureActivity;
import com.mcsaatchi.gmfit.nutrition.activities.SpecifyMealAmountActivity;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionWidgetsRecyclerAdapter;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMealsRecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import com.mcsaatchi.gmfit.nutrition.presenters.NutritionFragmentPresenter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalDate;

public class NutritionFragment extends BaseFragment
    implements NutritionFragmentPresenter.NutritionFragmentView {

  public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;
  private static final int BARCODE_CAPTURE_RC = 773;

  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
  @Bind(R.id.metricCounterTV) FontTextView metricCounterTV;
  @Bind(R.id.metricProgressBar) ProgressBar metricProgressBar;
  @Bind(R.id.cards_container) LinearLayout cards_container;
  @Bind(R.id.chartTitleTV_BREAKFAST) TextView chartTitleTV_BREAKFAST;
  @Bind(R.id.breakfastListView) RecyclerView breakfastListView;
  @Bind(R.id.chartTitleTV_LUNCH) TextView chartTitleTV_LUNCH;
  @Bind(R.id.lunchListView) RecyclerView lunchListView;
  @Bind(R.id.chartTitleTV_DINNER) TextView chartTitleTV_DINNER;
  @Bind(R.id.dinnerListView) RecyclerView dinnerListView;
  @Bind(R.id.chartTitleTV_SNACKS) TextView chartTitleTV_SNACKS;
  @Bind(R.id.snacksListView) RecyclerView snacksListView;
  @Bind(R.id.loadingMetricProgressBar) ProgressBar loadingMetricProgressBar;
  @Bind(R.id.loadingWidgetsProgressBar) ProgressBar loadingWidgetsProgressBar;
  @Bind(R.id.dateCarouselLayout) DateCarousel dateCarouselLayout;
  @Bind(R.id.goalTV) FontTextView goalTV;
  @Bind(R.id.remainingTV) FontTextView remainingTV;
  @Bind(R.id.todayTV) FontTextView todayTV;
  @Bind(R.id.activeTV) FontTextView activeTV;
  @Bind(R.id.goalStatusWordTV) TextView goalStatusWordTV;
  @Bind(R.id.goalStatusIconIV) ImageView goalStatusIconIV;
  @Bind(R.id.breakfastMealsEmptyLayout) LinearLayout breakfastMealsEmptyLayout;
  @Bind(R.id.lunchMealsEmptyLayout) LinearLayout lunchMealsEmptyLayout;
  @Bind(R.id.dinnerMealsEmptyLayout) LinearLayout dinnerMealsEmptyLayout;
  @Bind(R.id.snackMealsEmptyLayout) LinearLayout snackMealsEmptyLayout;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject LocalDate dt;

  private ArrayList<MealItem> finalBreakfastMeals = new ArrayList<>();
  private ArrayList<MealItem> finalLunchMeals = new ArrayList<>();
  private ArrayList<MealItem> finalDinnerMeals = new ArrayList<>();
  private ArrayList<MealItem> finalSnackMeals = new ArrayList<>();
  private UserMealsRecyclerAdapterDragSwipe userMealsRecyclerAdapter;
  private ArrayList<NutritionWidget> finalWidgets;
  private ArrayList<NutritionWidget> widgetsMap;
  private ArrayList<NutritionWidget> allWidgets;
  private NutritionFragmentPresenter presenter;
  private ArrayList<DataChart> finalCharts;
  private String finalDesiredDate;
  private Activity parentActivity;

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    parentActivity = (Activity) context;

    EventBusSingleton.getInstance().register(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_nutrition, container, false);

    ((AppCompatActivity) getActivity()).getSupportActionBar()
        .setTitle(R.string.nutrition_tab_title);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    setHasOptionsMenu(true);

    finalDesiredDate = Helpers.formatDateToDefault(new LocalDate());

    presenter = new NutritionFragmentPresenter(this, dataAccessHandler);

    presenter.getUserGoalMetrics(finalDesiredDate, "nutrition");

    presenter.getUserAddedMeals(finalDesiredDate);

    presenter.getUiForSection("nutrition", finalDesiredDate);

    setupDateCarousel();

    return fragmentView;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings:
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndChartsActivity.class);
        intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_NUTRITION_FRAGMENT);
        intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_WIDGETS_MAP, allWidgets);
        intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_CHARTS_MAP, finalCharts);
        startActivity(intent);
        break;
      case R.id.calendarToday:
        LinearLayout dateCarouselContainer =
            (LinearLayout) dateCarouselLayout.findViewById(R.id.dateCarouselContainer);

        dateCarouselContainer.removeAllViews();
        dateCarouselLayout.setupDateCarousel();

        showProgressBarsForLoading();

        presenter.getUserGoalMetrics(finalDesiredDate, "nutrition");
        presenter.getUserAddedMeals(finalDesiredDate);

        presenter.getUiForSection("nutrition", finalDesiredDate);

        dateCarouselLayout.post(() -> {
          dateCarouselLayout.setSmoothScrollingEnabled(true);
          dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        });

        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String scanContent;

    switch (requestCode) {
      case ADD_NEW_NUTRITION_CHART_REQUEST:
        if (data != null) {
          DataChart chartObject = data.getParcelableExtra(Constants.EXTRAS_CHART_OBJECT);

          finalCharts.add(chartObject);

          presenter.getChartMonthlyBreakdown(chartObject, dt);
        }
        break;
      case BARCODE_CAPTURE_RC:
        if (resultCode == CommonStatusCodes.SUCCESS) {
          if (data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            String mealType = data.getExtras().getString(Constants.MEAL_TYPE);

            scanContent = barcode.displayValue;

            presenter.searchForMealBarcode(scanContent, mealType);
          } else {
            Toast.makeText(getActivity(), getString(R.string.no_barcode_detected_here),
                Toast.LENGTH_LONG).show();
          }
        }
        break;
    }
  }

  @Override public void displayUserGoalMetrics(String maxValue, String currentValue) {
    todayTV.setText(Helpers.getFormattedString((int) Double.parseDouble(currentValue)));
    goalTV.setText(Helpers.getFormattedString(Integer.parseInt(maxValue)));

    activeTV.setText(String.valueOf((int) prefs.getFloat("calories_spent", 0)));

    if (!activeTV.getText().toString().isEmpty()
        && !goalTV.getText().toString().isEmpty()
        && !todayTV.getText().toString().isEmpty()) {

      int remainingValue = Helpers.getNumberFromFromattedString(goalTV.getText().toString())
          + Helpers.getNumberFromFromattedString(activeTV.getText().toString())
          - Helpers.getNumberFromFromattedString(todayTV.getText().toString());

      if (remainingValue < 0) {
        goalStatusWordTV.setText(getResources().getString(R.string.goal_nutrition_excess));
        goalStatusIconIV.setVisibility(View.VISIBLE);
      } else {
        goalStatusIconIV.setVisibility(View.GONE);
        if (isAdded()) {
          goalStatusWordTV.setText(getResources().getString(R.string.remaining_title));
        }
      }

      remainingTV.setText(Helpers.getFormattedString(Math.abs(remainingValue)));

      changeMetricProgressValue();
    }
  }

  @Override public void displayUserAddedMeals(List<UserMealsResponseInner> breakfastMeals,
      List<UserMealsResponseInner> lunchMeals, List<UserMealsResponseInner> dinnerMeals,
      List<UserMealsResponseInner> snackMeals) {
    finalBreakfastMeals.clear();
    finalLunchMeals.clear();
    finalDinnerMeals.clear();
    finalSnackMeals.clear();

    /**
     * Insert Breakfast meals
     */
    for (int i = 0; i < breakfastMeals.size(); i++) {
      MealItem breakfastMeal = new MealItem();
      breakfastMeal.setMeal_id(breakfastMeals.get(i).getId());
      breakfastMeal.setCreated_at(breakfastMeals.get(i).getCreatedAt());
      breakfastMeal.setInstance_id(breakfastMeals.get(i).getInstance_id());
      breakfastMeal.setType("Breakfast");
      breakfastMeal.setName(breakfastMeals.get(i).getName());
      breakfastMeal.setMeasurementUnit(breakfastMeals.get(i).getMeasurementUnit());
      breakfastMeal.setAmount(breakfastMeals.get(i).getAmount());
      breakfastMeal.setSectionType(2);

      breakfastMeal.setTotalCalories(breakfastMeals.get(i).getTotalCalories());

      finalBreakfastMeals.add(breakfastMeal);
    }

    setupMealSectionsListView(finalBreakfastMeals, "Breakfast");

    /**
     * Insert Lunch meals
     */
    for (int i = 0; i < lunchMeals.size(); i++) {
      MealItem lunchMeal = new MealItem();
      lunchMeal.setMeal_id(lunchMeals.get(i).getId());
      lunchMeal.setCreated_at(lunchMeals.get(i).getCreatedAt());
      lunchMeal.setInstance_id(lunchMeals.get(i).getInstance_id());
      lunchMeal.setType("Lunch");
      lunchMeal.setName(lunchMeals.get(i).getName());
      lunchMeal.setMeasurementUnit(lunchMeals.get(i).getMeasurementUnit());
      lunchMeal.setAmount(lunchMeals.get(i).getAmount());
      lunchMeal.setSectionType(2);

      lunchMeal.setTotalCalories(lunchMeals.get(i).getTotalCalories());

      finalLunchMeals.add(lunchMeal);
    }

    setupMealSectionsListView(finalLunchMeals, "Lunch");

    /**
     * Insert Dinner meals
     */
    for (int i = 0; i < dinnerMeals.size(); i++) {
      MealItem dinnerMeal = new MealItem();
      dinnerMeal.setMeal_id(dinnerMeals.get(i).getId());
      dinnerMeal.setCreated_at(dinnerMeals.get(i).getCreatedAt());
      dinnerMeal.setInstance_id(dinnerMeals.get(i).getInstance_id());
      dinnerMeal.setType("Dinner");
      dinnerMeal.setName(dinnerMeals.get(i).getName());
      dinnerMeal.setMeasurementUnit(dinnerMeals.get(i).getMeasurementUnit());
      dinnerMeal.setAmount(dinnerMeals.get(i).getAmount());
      dinnerMeal.setSectionType(2);

      dinnerMeal.setTotalCalories(dinnerMeals.get(i).getTotalCalories());

      finalDinnerMeals.add(dinnerMeal);
    }

    setupMealSectionsListView(finalDinnerMeals, "Dinner");

    /**
     * Insert Snack meals
     */
    for (int i = 0; i < snackMeals.size(); i++) {
      MealItem snackMeal = new MealItem();
      snackMeal.setMeal_id(snackMeals.get(i).getId());
      snackMeal.setInstance_id(snackMeals.get(i).getInstance_id());
      snackMeal.setCreated_at(snackMeals.get(i).getCreatedAt());
      snackMeal.setType("Snack");
      snackMeal.setName(snackMeals.get(i).getName());
      snackMeal.setMeasurementUnit(snackMeals.get(i).getMeasurementUnit());
      snackMeal.setAmount(snackMeals.get(i).getAmount());
      snackMeal.setSectionType(2);

      snackMeal.setTotalCalories(snackMeals.get(i).getTotalCalories());

      finalSnackMeals.add(snackMeal);
    }

    setupMealSectionsListView(finalSnackMeals, "Snack");
  }

  @Override public void displayUiForSection(List<AuthenticationResponseWidget> widgetsMapFromAPI,
      List<AuthenticationResponseChart> chartsMapFromAPI) {
    finalWidgets = new ArrayList<>();

    for (int i = 0; i < widgetsMapFromAPI.size(); i++) {
      NutritionWidget nutritionWidget = new NutritionWidget();
      nutritionWidget.setTitle(widgetsMapFromAPI.get(i).getName());
      nutritionWidget.setPosition(i);
      nutritionWidget.setMeasurementUnit(widgetsMapFromAPI.get(i).getUnit());
      nutritionWidget.setWidget_id(widgetsMapFromAPI.get(i).getWidgetId());
      nutritionWidget.setValue(Double.parseDouble(widgetsMapFromAPI.get(i).getTotal()));

      finalWidgets.add(nutritionWidget);
    }

    finalCharts = new ArrayList<>();

    for (int i = 0; i < chartsMapFromAPI.size(); i++) {
      DataChart nutritionDataChart = new DataChart();
      nutritionDataChart.setName(chartsMapFromAPI.get(i).getName());
      nutritionDataChart.setPosition(Integer.parseInt(chartsMapFromAPI.get(i).getPosition()));
      nutritionDataChart.setType(chartsMapFromAPI.get(i).getSlug());
      nutritionDataChart.setUsername(chartsMapFromAPI.get(i).getSlug());
      nutritionDataChart.setChart_id(chartsMapFromAPI.get(i).getChartId());
      nutritionDataChart.setChartData(
          (ArrayList<AuthenticationResponseChartData>) chartsMapFromAPI.get(i).getData());

      finalCharts.add(nutritionDataChart);
    }

    if (isAdded()) {
      getActivity().runOnUiThread(() -> {
        setupWidgetViews(finalWidgets);

        setupChartViews(finalCharts);

        hideProgressBarsForLoading();
      });
    }
  }

  @Override public void displayBarcodeSearchResults(List<SearchMealItemResponseDatum> mealsResponse,
      String mealType) {
    if (!mealsResponse.isEmpty()) {
      MealItem item = new MealItem();
      item.setMeal_id(mealsResponse.get(0).getId());
      item.setMeasurementUnit(mealsResponse.get(0).getMeasurementUnit());
      item.setName(mealsResponse.get(0).getName());
      item.setType(mealType);

      Intent intent = new Intent(getActivity(), SpecifyMealAmountActivity.class);
      intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, item);
      intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, true);
      intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON,
          Helpers.formatDateToDefault(new LocalDate()));
      startActivity(intent);
    } else {
      Toast.makeText(parentActivity, getString(R.string.scanned_meal_not_found), Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override public void setupChartsAfterDeletion(DataChart chartObject) {
    Toast.makeText(getActivity(), "Chart deleted successfully", Toast.LENGTH_SHORT).show();

    cards_container.removeAllViews();

    for (int i = 0; i < finalCharts.size(); i++) {
      if (finalCharts.get(i).getName().equals(chartObject.getName())) {
        finalCharts.remove(i);
      }
    }

    setupChartViews(finalCharts);
  }

  @Override public void openSlugBreakdownActivity(SlugBreakdownResponseInnerData breakdownData,
      DataChart chartObject) {
    Intent intent = new Intent(getActivity(), SlugBreakdownActivity.class);
    intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_NUTRITION_FRAGMENT);
    intent.putExtra(Constants.EXTRAS_CHART_OBJECT, chartObject);
    intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA, breakdownData);
    getActivity().startActivity(intent);
  }

  @Override public void createNewChartWithData(List<AuthenticationResponseChartData> chartData,
      DataChart chartObject) {
    if (getActivity() != null) {
      CustomBarChart customBarChart =
          new CustomBarChart(getActivity().getApplication(), chartObject);

      customBarChart.addClickListener(
          chartObjectInner -> presenter.getSlugBreakdownForChart(chartObjectInner));

      customBarChart.setBarChartDataAndDates(cards_container, chartData,
          Constants.EXTRAS_NUTRITION_FRAGMENT);
    }
  }

  @Subscribe public void chartDeleted(DataChartDeletedEvent event) {

    DataChart chartObject = event.getChartObject();

    presenter.deleteUserChart(chartObject);
  }

  @Subscribe public void reflectMealEntryChanged(MealEntryManipulatedEvent event) {
    presenter.getUserAddedMeals(finalDesiredDate);
    presenter.getUiForSection("nutrition", finalDesiredDate);

    metricProgressBar.setProgress(
        ((Helpers.getNumberFromFromattedString(metricCounterTV.getText().toString())
            + Helpers.getNumberFromFromattedString(activeTV.getText().toString())) * 100)
            / Helpers.getNumberFromFromattedString(goalTV.getText().toString()));

    presenter.getUserGoalMetrics(finalDesiredDate, "nutrition");

    cancelAllPendingAlarms();
  }

  @Subscribe public void updateWidgetsOrder(NutritionWidgetsOrderChangedEvent event) {
    widgetsMap = event.getWidgetsMapNutrition();
    setupWidgetViews(event.getWidgetsMapNutrition());

    int[] widgets = new int[widgetsMap.size()];
    int[] positions = new int[widgetsMap.size()];

    for (int i = 0; i < widgetsMap.size(); i++) {
      widgets[i] = widgetsMap.get(i).getWidget_id();
      positions[i] = widgetsMap.get(i).getPosition();
    }

    presenter.updateUserWidgets(widgets, positions);
  }

  @Subscribe public void updateChartsOrder(DataChartsOrderChangedEvent event) {
    List<DataChart> allDataCharts = event.getDataChartsListExtra();

    cards_container.removeAllViews();

    for (DataChart chart : allDataCharts) {
      presenter.getChartMonthlyBreakdown(chart, dt);
    }

    int[] charts = new int[allDataCharts.size()];
    int[] chartPositions = new int[allDataCharts.size()];

    for (int i = 0; i < allDataCharts.size(); i++) {
      charts[i] = allDataCharts.get(i).getChart_id();
      chartPositions[i] = allDataCharts.get(i).getPosition();
    }

    presenter.updateUserCharts(charts, chartPositions);
  }

  @OnClick(R.id.addChartBTN) public void handleAddNewChart() {
    Intent intent = new Intent(getActivity(), AddNewChartActivity.class);
    intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE, Constants.EXTRAS_ADD_NUTRIITION_CHART);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_CHARTS_MAP, finalCharts);
    startActivityForResult(intent, ADD_NEW_NUTRITION_CHART_REQUEST);
  }

  @OnClick(R.id.scanEntryBTN_BREAKFAST) public void handleScanBreakfast() {
    handleScanMealEntry("Breakfast");
  }

  @OnClick(R.id.addEntryBTN_BREAKFAST) public void addEntryBreakfast() {
    openMealEntryPickerActivity(chartTitleTV_BREAKFAST.getText().toString());
  }

  @OnClick(R.id.scanEntryBTN_LUNCH) public void handleScanLunch() {
    handleScanMealEntry("Lunch");
  }

  @OnClick(R.id.addEntryBTN_LUNCH) public void addLunchEntry() {
    openMealEntryPickerActivity(chartTitleTV_LUNCH.getText().toString());
  }

  @OnClick(R.id.scanEntryBTN_DINNER) public void handleScanDinner() {
    handleScanMealEntry("Dinner");
  }

  @OnClick(R.id.addEntryBTN_DINNER) public void addDinnerEntry() {
    openMealEntryPickerActivity(chartTitleTV_DINNER.getText().toString());
  }

  @OnClick(R.id.scanEntryBTN_SNACKS) public void handleScanSnacks() {
    handleScanMealEntry("Snack");
  }

  @OnClick(R.id.addEntryBTN_SNACKS) public void addSnackEntry() {
    openMealEntryPickerActivity(chartTitleTV_SNACKS.getText().toString());
  }

  private void setupDateCarousel() {
    dateCarouselLayout.addClickListener((todayDate, finalDate) -> {
      showProgressBarsForLoading();

      presenter.getUserGoalMetrics(finalDate, "nutrition");
      presenter.getUserAddedMeals(finalDate);
      presenter.getUiForSection("nutrition", finalDate);
    });

    dateCarouselLayout.post(() -> {
      dateCarouselLayout.setSmoothScrollingEnabled(true);
      dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    });
  }

  private void setupWidgetViews(ArrayList<NutritionWidget> widgetsFromResponse) {

    allWidgets = widgetsFromResponse;

    for (int i = 0; i < widgetsFromResponse.size(); i++) {
      if (widgetsFromResponse.get(i).getTitle().equals("Calories")) {
        metricCounterTV.setText(
            Helpers.getFormattedString((int) widgetsFromResponse.get(i).getValue()));
        todayTV.setText(Helpers.getFormattedString((int) widgetsFromResponse.get(i).getValue()));
      }
    }

    loadingMetricProgressBar.setVisibility(View.GONE);

    widgetsMap = new ArrayList<>(widgetsFromResponse.subList(0, 2));

    NutritionWidgetsRecyclerAdapter nutritionWidgets_GridAdapter =
        new NutritionWidgetsRecyclerAdapter(getActivity(), widgetsMap,
            R.layout.grid_item_nutrition_widgets);
    widgetsGridView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    widgetsGridView.setAdapter(nutritionWidgets_GridAdapter);

    loadingWidgetsProgressBar.setVisibility(View.GONE);
  }

  private void setupChartViews(ArrayList<DataChart> chartsMap) {
    cards_container.removeAllViews();

    if (!chartsMap.isEmpty()) {
      for (DataChart chart : chartsMap) {
        presenter.getChartMonthlyBreakdown(chart, dt);
      }
    }
  }

  private void setupMealSectionsListView(ArrayList<MealItem> mealItems, String mealType) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;

    if (isAdded()) {
      userMealsRecyclerAdapter = new UserMealsRecyclerAdapterDragSwipe(getActivity(), mealItems);
      callback = new SimpleSwipeItemTouchHelperCallback(userMealsRecyclerAdapter);
      touchHelper = new ItemTouchHelper(callback);

      switch (mealType) {
        case "Breakfast":
          if (mealItems.isEmpty()) {
            breakfastMealsEmptyLayout.setVisibility(View.VISIBLE);
            writeAppropriateEmptyText(breakfastMealsEmptyLayout, "Breakfast");

            ImageView mealsEmptyIconIV =
                (ImageView) breakfastMealsEmptyLayout.findViewById(R.id.mealsEmptyIconIV);
            Picasso.with(getActivity()).load(R.drawable.ic_empty_meals).into(mealsEmptyIconIV);
          } else {
            breakfastMealsEmptyLayout.setVisibility(View.GONE);
            hookUpMealSectionListViews(breakfastListView, mLayoutManager, touchHelper);
          }
          break;
        case "Lunch":
          if (mealItems.isEmpty()) {
            lunchMealsEmptyLayout.setVisibility(View.VISIBLE);
            writeAppropriateEmptyText(lunchMealsEmptyLayout, "Lunch");

            ImageView mealsEmptyIconIV =
                (ImageView) lunchMealsEmptyLayout.findViewById(R.id.mealsEmptyIconIV);
            Picasso.with(getActivity()).load(R.drawable.ic_empty_meals).into(mealsEmptyIconIV);
          } else {
            lunchMealsEmptyLayout.setVisibility(View.GONE);
            hookUpMealSectionListViews(lunchListView, mLayoutManager, touchHelper);
          }
          break;
        case "Dinner":
          if (mealItems.isEmpty()) {
            dinnerMealsEmptyLayout.setVisibility(View.VISIBLE);
            writeAppropriateEmptyText(dinnerMealsEmptyLayout, "Dinner");

            ImageView mealsEmptyIconIV =
                (ImageView) dinnerMealsEmptyLayout.findViewById(R.id.mealsEmptyIconIV);
            Picasso.with(getActivity()).load(R.drawable.ic_empty_meals).into(mealsEmptyIconIV);
          } else {
            dinnerMealsEmptyLayout.setVisibility(View.GONE);
            hookUpMealSectionListViews(dinnerListView, mLayoutManager, touchHelper);
          }
          break;
        case "Snack":
          if (mealItems.isEmpty()) {
            snackMealsEmptyLayout.setVisibility(View.VISIBLE);
            writeAppropriateEmptyText(snackMealsEmptyLayout, "Snacks");

            ImageView mealsEmptyIconIV =
                (ImageView) snackMealsEmptyLayout.findViewById(R.id.mealsEmptyIconIV);
            Picasso.with(getActivity()).load(R.drawable.ic_empty_snacks).into(mealsEmptyIconIV);
          } else {
            snackMealsEmptyLayout.setVisibility(View.GONE);
            hookUpMealSectionListViews(snacksListView, mLayoutManager, touchHelper);
          }
          break;
      }
    }
  }

  private void writeAppropriateEmptyText(LinearLayout emptyMealsLayout, String mealType) {
    TextView typeOfEmptyMealTV = (TextView) emptyMealsLayout.findViewById(R.id.typeOfEmptyMealTV);
    typeOfEmptyMealTV.setText("No " + mealType + " added");
  }

  private void hookUpMealSectionListViews(RecyclerView mealListView,
      RecyclerView.LayoutManager layoutManager, ItemTouchHelper touchHelper) {
    mealListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    mealListView.setNestedScrollingEnabled(false);
    mealListView.setLayoutManager(layoutManager);
    mealListView.setAdapter(userMealsRecyclerAdapter);
    touchHelper.attachToRecyclerView(mealListView);
  }

  private void cancelAllPendingAlarms() {
    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
    PendingIntent breakfastPendingAlarm =
        PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent lunchPendingAlarm =
        PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent dinnerPendingAlarm =
        PendingIntent.getBroadcast(getActivity(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    breakfastPendingAlarm.cancel();
    lunchPendingAlarm.cancel();
    dinnerPendingAlarm.cancel();
  }

  public void handleScanMealEntry(String mealType) {
    Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
    intent.putExtra(Constants.MEAL_TYPE, mealType);

    startActivityForResult(intent, BARCODE_CAPTURE_RC);
  }

  private void openMealEntryPickerActivity(String mainMealName) {
    Intent intent = new Intent(parentActivity, AddNewMealItemActivity.class);
    intent.putExtra(Constants.EXTRAS_MAIN_MEAL_NAME, mainMealName);
    intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, finalDesiredDate);
    startActivity(intent);
  }

  private void changeMetricProgressValue() {
    int progressValue = ((Helpers.getNumberFromFromattedString(metricCounterTV.getText().toString())
        + Helpers.getNumberFromFromattedString(activeTV.getText().toString())) * 100)
        / Helpers.getNumberFromFromattedString(goalTV.getText().toString());

    if (progressValue > 100) progressValue = 100;

    metricProgressBar.setProgress(progressValue);
  }

  private void showProgressBarsForLoading() {
    widgetsGridView.setVisibility(View.INVISIBLE);
    metricCounterTV.setVisibility(View.INVISIBLE);

    loadingMetricProgressBar.setVisibility(View.VISIBLE);
    loadingWidgetsProgressBar.setVisibility(View.VISIBLE);
  }

  private void hideProgressBarsForLoading() {
    widgetsGridView.setVisibility(View.VISIBLE);
    metricCounterTV.setVisibility(View.VISIBLE);

    loadingMetricProgressBar.setVisibility(View.INVISIBLE);
    loadingWidgetsProgressBar.setVisibility(View.INVISIBLE);
  }
}