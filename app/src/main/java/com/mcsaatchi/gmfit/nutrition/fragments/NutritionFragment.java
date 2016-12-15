package com.mcsaatchi.gmfit.nutrition.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusPoster;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponseActiveCalories;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseBreakfast;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseDinner;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseLunch;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseSnack;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleSwipeItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.AddNewChartActivity;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.activities.SlugBreakdownActivity;
import com.mcsaatchi.gmfit.common.classes.AlarmReceiver;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealItemActivity;
import com.mcsaatchi.gmfit.nutrition.activities.BarcodeCaptureActivity;
import com.mcsaatchi.gmfit.nutrition.activities.SpecifyMealAmountActivity;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionWidgetsGridAdapter;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMealsRecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.common.Constants.EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED;

public class NutritionFragment extends Fragment {

  public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;
  private static final String TAG = "NutritionFragment";
  private static final int ITEM_VIEWTYPE = 2;
  private static final int BARCODE_CAPTURE_RC = 773;
  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject LocalDate dt;

  @Bind(R.id.widgetsGridView) GridView widgetsGridView;
  @Bind(R.id.metricCounterTV) FontTextView metricCounterTV;
  @Bind(R.id.metricProgressBar) ProgressBar metricProgressBar;

  /**
   * CHARTS
   */
  @Bind(R.id.cards_container) LinearLayout cards_container;
  /**
   * BREAKFAST CHART
   */
  @Bind(R.id.chartTitleTV_BREAKFAST) TextView chartTitleTV_BREAKFAST;
  @Bind(R.id.addEntryBTN_BREAKFAST) TextView addNewEntryBTN_BREAKFAST;
  @Bind(R.id.scanEntryBTN_BREAKFAST) TextView scanEntryBTN_BREAKFAST;
  @Bind(R.id.breakfastListView) RecyclerView breakfastListView;
  /**
   * LUNCH CHART
   */
  @Bind(R.id.chartTitleTV_LUNCH) TextView chartTitleTV_LUNCH;
  @Bind(R.id.addEntryBTN_LUNCH) TextView addNewEntryBTN_LUNCH;
  @Bind(R.id.scanEntryBTN_LUNCH) TextView scanEntryBTN_LUNCH;
  @Bind(R.id.lunchListView) RecyclerView lunchListView;
  /**
   * DINNER CHART
   */
  @Bind(R.id.chartTitleTV_DINNER) TextView chartTitleTV_DINNER;
  @Bind(R.id.addEntryBTN_DINNER) TextView addNewEntryBTN_DINNER;
  @Bind(R.id.scanEntryBTN_DINNER) TextView scanEntryBTN_DINNER;
  @Bind(R.id.dinnerListView) RecyclerView dinnerListView;
  /**
   * SNACKS CHART
   */
  @Bind(R.id.chartTitleTV_SNACKS) TextView chartTitleTV_SNACKS;
  @Bind(R.id.addEntryBTN_SNACKS) TextView addNewEntryBTN_SNACKS;
  @Bind(R.id.scanEntryBTN_SNACKS) TextView scanEntryBTN_SNACKS;
  @Bind(R.id.snacksListView) RecyclerView snacksListView;
  /**
   * ADD CHART BUTTON
   */
  @Bind(R.id.addChartBTN) Button addNewChartBTN;
  @Bind(R.id.loadingMetricProgressBar) ProgressBar loadingMetricProgressBar;
  @Bind(R.id.loadingWidgetsProgressBar) ProgressBar loadingWidgetsProgressBar;
  @Bind(R.id.dateCarousel) HorizontalScrollView dateCarousel;
  @Bind(R.id.dateCarouselContainer) LinearLayout dateCarouselContainer;

  @Bind(R.id.goalTV) FontTextView goalTV;
  @Bind(R.id.remainingTV) FontTextView remainingTV;
  @Bind(R.id.todayTV) FontTextView todayTV;
  @Bind(R.id.activeTV) FontTextView activeTV;

  private String finalDesiredDate;
  private boolean setDrawValuesDisabled = true;
  private UserMealsRecyclerAdapterDragSwipe userMealsRecyclerAdapter;
  private ArrayList<MealItem> finalBreakfastMeals = new ArrayList<>();
  private ArrayList<MealItem> finalLunchMeals = new ArrayList<>();
  private ArrayList<MealItem> finalDinnerMeals = new ArrayList<>();
  private ArrayList<MealItem> finalSnackMeals = new ArrayList<>();

  private ArrayList<NutritionWidget> widgetsMap;
  private ArrayList<NutritionWidget> finalWidgets;
  private ArrayList<DataChart> finalCharts;

  /**
   * TOP LAYOUT WITH WIDGETS
   */
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

    hookupMealSectionRowsClickListeners();

    finalDesiredDate = Helpers.prepareDateForAPIRequest(new LocalDate());

    getUserGoalMetrics(finalDesiredDate, "nutrition");

    getUserAddedMeals(finalDesiredDate);

    getUiForSection("nutrition", finalDesiredDate);

    setupDateCarousel();

    addNewChartBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddNewChartActivity.class);
        intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE,
            Constants.EXTRAS_ADD_NUTRIITION_CHART);
        startActivityForResult(intent, ADD_NEW_NUTRITION_CHART_REQUEST);
      }
    });

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
    Intent intent = new Intent(getActivity(), CustomizeWidgetsAndChartsActivity.class);
    intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_NUTRITION_FRAGMENT);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_WIDGETS_MAP, widgetsMap);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_CHARTS_MAP, finalCharts);
    startActivity(intent);

    return super.onOptionsItemSelected(item);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String scanContent;

    switch (requestCode) {
      case ADD_NEW_NUTRITION_CHART_REQUEST:
        if (data != null) {
          String chartTitle = data.getStringExtra(Constants.EXTRAS_CHART_FULL_NAME);

          addNewBarChart(chartTitle);
        }
        break;
      case BARCODE_CAPTURE_RC:
        if (resultCode == CommonStatusCodes.SUCCESS) {
          if (data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
            String mealType = data.getExtras().getString(Constants.MEAL_TYPE);

            scanContent = barcode.displayValue;

            searchForMealBarcode(scanContent, mealType);
          } else {
            Toast.makeText(getActivity(), getString(R.string.no_barcode_detected_here),
                Toast.LENGTH_LONG).show();
          }
        }
        break;
    }
  }

  private void searchForMealBarcode(String barcode, final String mealType) {
    dataAccessHandler.searchForMealBarcode(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        barcode, new Callback<SearchMealItemResponse>() {
          @Override public void onResponse(Call<SearchMealItemResponse> call,
              Response<SearchMealItemResponse> response) {
            switch (response.code()) {
              case 200:

                List<SearchMealItemResponseDatum> mealsResponse =
                    response.body().getData().getBody().getData();

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
                      Helpers.prepareDateForAPIRequest(new LocalDate()));
                  startActivity(intent);
                } else {
                  Toast.makeText(parentActivity, getString(R.string.scanned_meal_not_found),
                      Toast.LENGTH_LONG).show();
                }

                break;
            }
          }

          @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void getUserGoalMetrics(final String date, final String type) {
    dataAccessHandler.getUserGoalMetrics(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        date, type, new Callback<UserGoalMetricsResponse>() {
          @Override public void onResponse(Call<UserGoalMetricsResponse> call,
              Response<UserGoalMetricsResponse> response) {

            switch (response.code()) {
              case 200:
                UserGoalMetricsResponseActiveCalories activeCaloriesResponse =
                    response.body().getData().getBody().getMetrics().getActiveCalories();

                if (activeCaloriesResponse == null) {
                  String maxValue =
                      response.body().getData().getBody().getMetrics().getCalories().getMaxValue();

                  String currentValue =
                      response.body().getData().getBody().getMetrics().getCalories().getValue();

                  todayTV.setText(String.valueOf((int) Double.parseDouble(currentValue)));
                  goalTV.setText(maxValue);

                  getUserGoalMetrics(date, "fitness");
                } else {
                  String activeCalories = activeCaloriesResponse.getValue();

                  activeTV.setText(String.valueOf((int) Double.parseDouble(activeCalories)));
                }

                if (!activeTV.getText().toString().isEmpty()
                    && !goalTV.getText()
                    .toString()
                    .isEmpty()
                    && !todayTV.getText().toString().isEmpty()
                    && !metricCounterTV.getText().toString().isEmpty()) {
                  int remainingValue =
                      Integer.parseInt(goalTV.getText().toString()) + Integer.parseInt(
                          activeTV.getText().toString()) - Integer.parseInt(
                          todayTV.getText().toString());

                  if (remainingValue < 0) {
                    remainingTV.setText(String.valueOf(0));
                  } else {
                    remainingTV.setText(String.valueOf(remainingValue));
                  }

                  changeMetricProgressValue();
                }

                break;
            }
          }

          @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
          }
        });
  }

  private void setupDateCarousel() {
    int daysExtraToShow = 3;

    LocalDate dateToStartFrom = dt.plusDays(daysExtraToShow);

    for (int i = Constants.NUMBER_OF_DAYS_IN_DATE_CAROUSEL; i >= 0; i--) {
      final View itemDateCarouselLayout =
          getActivity().getLayoutInflater().inflate(R.layout.item_date_carousel, null);
      itemDateCarouselLayout.setPadding(
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0,
          getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0);

      final TextView dayOfMonthTV =
          (TextView) itemDateCarouselLayout.findViewById(R.id.dayOfMonthTV);
      final TextView monthOfYearTV =
          (TextView) itemDateCarouselLayout.findViewById(R.id.monthOfYearTV);

      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      dayOfMonthTV.setTypeface(null, Typeface.NORMAL);
      monthOfYearTV.setTypeface(null, Typeface.NORMAL);

      LocalDate dateAsLocal = dateToStartFrom.minusDays(i);
      DateTimeFormatter monthFormatter = DateTimeFormat.forPattern("MMM");

      dayOfMonthTV.setText(String.valueOf(dateAsLocal.getDayOfMonth()));
      monthOfYearTV.setText(String.valueOf(monthFormatter.print(dateAsLocal).toUpperCase()));

      dateCarouselContainer.addView(itemDateCarouselLayout);

      if (i == daysExtraToShow) {
        focusOnView(dateCarouselContainer, itemDateCarouselLayout);
      }

      if (i < daysExtraToShow) {
        fadeOutView(itemDateCarouselLayout);
      } else if (i >= daysExtraToShow) {
        itemDateCarouselLayout.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            focusOnView(dateCarouselContainer, view);

            showProgressBarsForLoading();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MMM/yyyy");
            DateTime formattedDate = formatter.parseDateTime(dayOfMonthTV.getText().toString()
                + "/"
                + monthOfYearTV.getText().toString()
                + "/"
                + dt.year().getAsText());

            finalDesiredDate = Helpers.prepareDateForAPIRequest(formattedDate.toLocalDate());

            getUserGoalMetrics(finalDesiredDate, "nutrition");
            getUserAddedMeals(finalDesiredDate);
            getUiForSection("nutrition", finalDesiredDate);
          }
        });
      }
    }

    dateCarousel.post(new Runnable() {
      @Override public void run() {
        dateCarousel.setSmoothScrollingEnabled(true);
        dateCarousel.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
      }
    });
  }

  private void focusOnView(final LinearLayout dateCarouselContainer, final View view) {
    TextView dayOfMonthTV;
    TextView monthOfYearTV;
    LinearLayout dateEntryLayout;

    for (int i = 0; i < dateCarouselContainer.getChildCount(); i++) {

      dateEntryLayout =
          (LinearLayout) dateCarouselContainer.getChildAt(i).findViewById(R.id.dateEntryLayout);
      dayOfMonthTV = (TextView) dateCarouselContainer.getChildAt(i).findViewById(R.id.dayOfMonthTV);
      monthOfYearTV =
          (TextView) dateCarouselContainer.getChildAt(i).findViewById(R.id.monthOfYearTV);

      dateEntryLayout.setBackgroundColor(0);
      dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
      dayOfMonthTV.setTypeface(null, Typeface.NORMAL);
      monthOfYearTV.setTypeface(null, Typeface.NORMAL);
    }

    dateEntryLayout = (LinearLayout) view.findViewById(R.id.dateEntryLayout);
    dayOfMonthTV = (TextView) view.findViewById(R.id.dayOfMonthTV);
    monthOfYearTV = (TextView) view.findViewById(R.id.monthOfYearTV);

    if (isAdded()) {
      dateEntryLayout.setBackgroundColor(getResources().getColor(R.color.offwhite_transparent));
    }
    dayOfMonthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    monthOfYearTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    dayOfMonthTV.setTypeface(null, Typeface.BOLD);
    monthOfYearTV.setTypeface(null, Typeface.BOLD);
  }

  private void fadeOutView(View view) {
    view.setAlpha(0.5f);
  }

  private void getUiForSection(String section, String desiredDate) {
    String finalUrl;

    if (desiredDate == null) {
      finalUrl = "http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section;
    } else {
      finalUrl =
          "http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section + "&date=" + desiredDate;
    }

    dataAccessHandler.getUiForSection(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        finalUrl, new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                List<AuthenticationResponseWidget> widgetsMapFromAPI =
                    response.body().getData().getBody().getWidgets();
                List<AuthenticationResponseChart> chartsMapFromAPI =
                    response.body().getData().getBody().getCharts();

                /**
                 * Update or create widgets and datacharts into the DB
                 */
                finalWidgets = new ArrayList<>();

                for (int i = 0; i < widgetsMapFromAPI.size(); i++) {
                  NutritionWidget nutritionWidget = new NutritionWidget();
                  nutritionWidget.setTitle(widgetsMapFromAPI.get(i).getName());
                  nutritionWidget.setPosition(
                      Integer.parseInt(widgetsMapFromAPI.get(i).getPosition()));
                  nutritionWidget.setMeasurementUnit(widgetsMapFromAPI.get(i).getUnit());
                  nutritionWidget.setWidget_id(widgetsMapFromAPI.get(i).getWidgetId());
                  nutritionWidget.setValue(Double.parseDouble(widgetsMapFromAPI.get(i).getTotal()));

                  finalWidgets.add(nutritionWidget);
                }

                finalCharts = new ArrayList<>();

                for (int i = 0; i < chartsMapFromAPI.size(); i++) {
                  DataChart nutritionDataChart = new DataChart();
                  nutritionDataChart.setName(chartsMapFromAPI.get(i).getName());
                  nutritionDataChart.setPosition(
                      Integer.parseInt(chartsMapFromAPI.get(i).getPosition()));
                  nutritionDataChart.setType(chartsMapFromAPI.get(i).getSlug());
                  nutritionDataChart.setUsername(chartsMapFromAPI.get(i).getSlug());
                  nutritionDataChart.setChart_id(chartsMapFromAPI.get(i).getChartId());
                  nutritionDataChart.setChartData(
                      (ArrayList<AuthenticationResponseChartData>) chartsMapFromAPI.get(i)
                          .getData());

                  finalCharts.add(nutritionDataChart);
                }

                if (isAdded()) {
                  getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                      setupWidgetViews(finalWidgets);

                      setupChartViews(finalCharts);

                      hideProgressBarsForLoading();
                    }
                  });
                }

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

            alertDialog.setMessage(getActivity().getResources()
                .getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void updateUserWidgets(int[] widgetIds, int[] widgetPositions) {
    dataAccessHandler.updateUserWidgets(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        widgetIds, widgetPositions, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Log.d(TAG, "onResponse: User's widgets updated successfully");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

          }
        });
  }

  private void updateUserCharts(int[] chartIds, int[] chartPositions) {
    dataAccessHandler.updateUserCharts(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        chartIds, chartPositions, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Log.d(TAG, "onResponse: User's charts updated successfully");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

          }
        });
  }

  private void getUserAddedMeals(String desiredDate) {
    dataAccessHandler.getUserAddedMealsOnDate(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        desiredDate, new Callback<UserMealsResponse>() {
          @Override public void onResponse(Call<UserMealsResponse> call,
              Response<UserMealsResponse> response) {
            switch (response.code()) {
              case 200:

                /**
                 * Grab all meals from the API
                 */
                List<UserMealsResponseBreakfast> breakfastMeals =
                    response.body().getData().getBody().getData().getBreakfast();
                List<UserMealsResponseLunch> lunchMeals =
                    response.body().getData().getBody().getData().getLunch();
                List<UserMealsResponseDinner> dinnerMeals =
                    response.body().getData().getBody().getData().getDinner();
                List<UserMealsResponseSnack> snackMeals =
                    response.body().getData().getBody().getData().getSnack();

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

                  if (breakfastMeals.get(i).getTotalCalories() != null) {
                    breakfastMeal.setTotalCalories(breakfastMeals.get(i).getTotalCalories());
                  } else {
                    breakfastMeal.setTotalCalories(0);
                  }

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

                  if (lunchMeals.get(i).getTotalCalories() != null) {
                    lunchMeal.setTotalCalories(lunchMeals.get(i).getTotalCalories());
                  } else {
                    lunchMeal.setTotalCalories(0);
                  }

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

                  if (dinnerMeals.get(i).getTotalCalories() != null) {
                    dinnerMeal.setTotalCalories(dinnerMeals.get(i).getTotalCalories());
                  } else {
                    dinnerMeal.setTotalCalories(0);
                  }

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

                  if (snackMeals.get(i).getTotalCalories() != null) {
                    snackMeal.setTotalCalories(snackMeals.get(i).getTotalCalories());
                  } else {
                    snackMeal.setTotalCalories(0);
                  }

                  finalSnackMeals.add(snackMeal);
                }

                setupMealSectionsListView(finalSnackMeals, "Snack");

                break;
            }
          }

          @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

            alertDialog.setMessage(getActivity().getResources()
                .getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void hookupMealSectionRowsClickListeners() {
    addNewEntryBTN_BREAKFAST.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openMealEntryPickerActivity(chartTitleTV_BREAKFAST.getText().toString());
      }
    });
    scanEntryBTN_BREAKFAST.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        handleScanMealEntry("Breakfast");
      }
    });

    addNewEntryBTN_LUNCH.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openMealEntryPickerActivity(chartTitleTV_LUNCH.getText().toString());
      }
    });
    scanEntryBTN_LUNCH.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        handleScanMealEntry("Lunch");
      }
    });

    addNewEntryBTN_DINNER.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openMealEntryPickerActivity(chartTitleTV_DINNER.getText().toString());
      }
    });
    scanEntryBTN_DINNER.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        handleScanMealEntry("Dinner");
      }
    });

    addNewEntryBTN_SNACKS.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openMealEntryPickerActivity(chartTitleTV_SNACKS.getText().toString());
      }
    });
    scanEntryBTN_SNACKS.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        handleScanMealEntry("Snack");
      }
    });
  }

  private void setupWidgetViews(ArrayList<NutritionWidget> widgetsFromResponse) {

    for (int i = 0; i < widgetsFromResponse.size(); i++) {
      if (widgetsFromResponse.get(i).getTitle().equals("Calories")) {
        metricCounterTV.setText(String.valueOf((int) widgetsFromResponse.get(i).getValue()));
        todayTV.setText(String.valueOf((int) widgetsFromResponse.get(i).getValue()));
      }
    }

    loadingMetricProgressBar.setVisibility(View.GONE);

    widgetsMap = new ArrayList<>(widgetsFromResponse.subList(0, 4));

    NutritionWidgetsGridAdapter nutritionWidgets_GridAdapter =
        new NutritionWidgetsGridAdapter(getActivity(), widgetsMap);

    widgetsGridView.setAdapter(nutritionWidgets_GridAdapter);

    loadingWidgetsProgressBar.setVisibility(View.GONE);
  }

  private void setupChartViews(ArrayList<DataChart> chartsMap) {
    cards_container.removeAllViews();

    if (!chartsMap.isEmpty()) {
      for (DataChart chart : chartsMap) {

        addNewBarChart(chart.getName());
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
          hookUpMealSectionListViews(breakfastListView, mLayoutManager, touchHelper);
          break;
        case "Lunch":
          hookUpMealSectionListViews(lunchListView, mLayoutManager, touchHelper);
          break;
        case "Dinner":
          hookUpMealSectionListViews(dinnerListView, mLayoutManager, touchHelper);
          break;
        case "Snack":
          hookUpMealSectionListViews(snacksListView, mLayoutManager, touchHelper);
          break;
      }
    }
  }

  private void hookUpMealSectionListViews(RecyclerView mealListView,
      RecyclerView.LayoutManager layoutManager, ItemTouchHelper touchHelper) {
    mealListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
    mealListView.setNestedScrollingEnabled(false);
    mealListView.setLayoutManager(layoutManager);
    mealListView.setAdapter(userMealsRecyclerAdapter);
    touchHelper.attachToRecyclerView(mealListView);
  }

  @Subscribe public void handle_BusEvents(final EventBusPoster ebp) {
    String ebpMessage = ebp.getMessage();

    switch (ebpMessage) {
      case Constants.EXTRAS_DELETED_MEAL_ENTRY:
      case Constants.EXTRAS_UPDATED_MEAL_ENTRY:
      case Constants.EXTRAS_CREATED_NEW_MEAL_ENTRY:
      case Constants.EXTRAS_CREATED_NEW_MEAL_ENTRY_ON_DATE:
        getUserAddedMeals(finalDesiredDate);
        getUiForSection("nutrition", finalDesiredDate);

        metricProgressBar.setProgress(
            ((Integer.parseInt(metricCounterTV.getText().toString()) + Integer.parseInt(
                activeTV.getText().toString())) * 100) / Integer.parseInt(
                goalTV.getText().toString()));

        cancelAllPendingAlarms();
        break;
      case Constants.EXTRAS_NUTRITION_WIDGETS_ORDER_ARRAY_CHANGED:
        if (ebp.getNutritionWidgetsMap() != null) {
          widgetsMap = ebp.getNutritionWidgetsMap();
          setupWidgetViews(ebp.getNutritionWidgetsMap());

          int[] widgets = new int[widgetsMap.size()];
          int[] positions = new int[widgetsMap.size()];

          for (int i = 0; i < widgetsMap.size(); i++) {
            widgets[i] = widgetsMap.get(i).getWidget_id();
            positions[i] = widgetsMap.get(i).getPosition();
          }

          updateUserWidgets(widgets, positions);
        }

        break;
      case EXTRAS_NUTRITION_CHARTS_ORDER_ARRAY_CHANGED:
        List<DataChart> allDataCharts = ebp.getDataChartsListExtra();

        cards_container.removeAllViews();

        for (DataChart chart : allDataCharts) {
          addNewBarChart(chart.getName());
        }

        int[] charts = new int[allDataCharts.size()];
        int[] chartPositions = new int[allDataCharts.size()];

        for (int i = 0; i < allDataCharts.size(); i++) {
          charts[i] = allDataCharts.get(i).getChart_id();
          chartPositions[i] = allDataCharts.get(i).getPosition();
        }

        updateUserCharts(charts, chartPositions);

        break;
      case Constants.EXTRAS_NUTRITION_CHART_DELETED:

        break;
    }
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
    int progressValue = ((Integer.parseInt(metricCounterTV.getText().toString()) + Integer.parseInt(
        activeTV.getText().toString())) * 100) / Integer.parseInt(goalTV.getText().toString());

    if (progressValue > 100) progressValue = 100;

    metricProgressBar.setProgress(progressValue);
  }

  private void addNewBarChart(final String chartTitle) {
    final TextView dateTV_1, dateTV_2, dateTV_3, dateTV_4;

    final View barChartLayout =
        parentActivity.getLayoutInflater().inflate(R.layout.view_barchart_container, null);

    dateTV_1 = (TextView) barChartLayout.findViewById(R.id.dateTV_1);
    dateTV_2 = (TextView) barChartLayout.findViewById(R.id.dateTV_2);
    dateTV_3 = (TextView) barChartLayout.findViewById(R.id.dateTV_3);
    dateTV_4 = (TextView) barChartLayout.findViewById(R.id.dateTV_4);

    TextView chartTitleTV = (TextView) barChartLayout.findViewById(R.id.chartTitleTV);
    final BarChart barChart = (BarChart) barChartLayout.findViewById(R.id.barChart);
    Button showChartValuesBTN = (Button) barChartLayout.findViewById(R.id.showChartValuesBTN);

    showChartValuesBTN.setBackgroundResource(R.drawable.ic_format_list_numbered_white_24dp);

    if (chartTitle != null) chartTitleTV.setText(chartTitle);

    getDefaultChartMonthlyBreakdown(barChart, dateTV_1, dateTV_2, dateTV_3, dateTV_4, chartTitle);

    if (isAdded()) {
      LinearLayout.LayoutParams lp =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              getResources().getDimensionPixelSize(R.dimen.chart_height_2));
      lp.topMargin = getResources().getDimensionPixelSize(R.dimen.default_margin_2);
      barChartLayout.setLayoutParams(lp);

      cards_container.addView(barChartLayout);

      /**
       * Open the breakdown for the chart
       */
      barChart.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          getSlugBreakdownForChart(chartTitle, chartTitle);
        }
      });

      showChartValuesBTN.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (setDrawValuesDisabled) {
            barChart.getBarData().setDrawValues(true);
          } else {
            barChart.getBarData().setDrawValues(false);
          }

          setDrawValuesDisabled = !setDrawValuesDisabled;

          barChart.invalidate();
        }
      });
    }
  }

  private void getSlugBreakdownForChart(final String chartTitle, final String chartType) {
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

    dataAccessHandler.getSlugBreakdownForChart(chartType,
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        new Callback<SlugBreakdownResponse>() {
          @Override public void onResponse(Call<SlugBreakdownResponse> call,
              Response<SlugBreakdownResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Intent intent = new Intent(getActivity(), SlugBreakdownActivity.class);
                intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE,
                    Constants.EXTRAS_NUTRITION_FRAGMENT);
                intent.putExtra(Constants.EXTRAS_CHART_FULL_NAME, chartTitle);
                intent.putExtra(Constants.EXTRAS_CHART_TYPE_SELECTED, chartType);
                intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA,
                    response.body().getData().getBody().getData());
                getActivity().startActivity(intent);
                break;
            }
          }

          @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void getDefaultChartMonthlyBreakdown(final BarChart barchart, final TextView dateTV_1,
      final TextView dateTV_2, final TextView dateTV_3, final TextView dateTV_4,
      final String chart_slug) {
    final String todayDate;
    todayDate = dt.toString();

    dataAccessHandler.getPeriodicalChartData(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        dt.minusMonths(1).toString(), todayDate, "nutrition", chart_slug,
        new Callback<ChartMetricBreakdownResponse>() {
          @Override public void onResponse(Call<ChartMetricBreakdownResponse> call,
              Response<ChartMetricBreakdownResponse> response) {
            if (response.body().getData().getBody() != null) {
              List<ChartMetricBreakdownResponseDatum> rawChartData =
                  response.body().getData().getBody().getData();

              if (rawChartData != null && rawChartData.size() > 0) {
                List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

                for (int i = 0; i < rawChartData.size(); i++) {
                  newChartData.add(
                      new AuthenticationResponseChartData(rawChartData.get(i).getDate(),
                          rawChartData.get(i).getValue()));
                }

                DateTime date;

                Collections.reverse(newChartData);

                for (int i = 0; i < newChartData.size(); i++) {
                  date = new DateTime(newChartData.get(i).getDate());

                  switch (i) {
                    case 5:
                      dateTV_1.setText(date.getDayOfMonth() + " " + date.monthOfYear()
                          .getAsText()
                          .substring(0, 3));
                      break;
                    case 12:
                      dateTV_2.setText(date.getDayOfMonth() + " " + date.monthOfYear()
                          .getAsText()
                          .substring(0, 3));
                      break;
                    case 19:
                      dateTV_3.setText(date.getDayOfMonth() + " " + date.monthOfYear()
                          .getAsText()
                          .substring(0, 3));
                      break;
                    case 26:
                      dateTV_4.setText(date.getDayOfMonth() + " " + date.monthOfYear()
                          .getAsText()
                          .substring(0, 3));
                      break;
                  }
                }

                Helpers.setBarChartData(barchart, newChartData);
              }
            }
          }

          @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
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