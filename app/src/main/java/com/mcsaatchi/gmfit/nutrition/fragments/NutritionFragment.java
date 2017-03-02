package com.mcsaatchi.gmfit.nutrition.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
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
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponse;
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
import com.mcsaatchi.gmfit.common.components.CustomBarChart;
import com.mcsaatchi.gmfit.common.components.DateCarousel;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealItemActivity;
import com.mcsaatchi.gmfit.nutrition.activities.BarcodeCaptureActivity;
import com.mcsaatchi.gmfit.nutrition.activities.SpecifyMealAmountActivity;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionWidgetsRecyclerAdapter;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMealsRecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NutritionFragment extends Fragment {

  public static final int ADD_NEW_NUTRITION_CHART_REQUEST = 2;
  private static final String TAG = "NutritionFragment";
  private static final int ITEM_VIEWTYPE = 2;
  private static final int BARCODE_CAPTURE_RC = 773;
  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject LocalDate dt;

  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
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

  private String finalDesiredDate;
  private UserMealsRecyclerAdapterDragSwipe userMealsRecyclerAdapter;
  private ArrayList<MealItem> finalBreakfastMeals = new ArrayList<>();
  private ArrayList<MealItem> finalLunchMeals = new ArrayList<>();
  private ArrayList<MealItem> finalDinnerMeals = new ArrayList<>();
  private ArrayList<MealItem> finalSnackMeals = new ArrayList<>();

  private ArrayList<NutritionWidget> widgetsMap;
  private ArrayList<NutritionWidget> finalWidgets;
  private ArrayList<DataChart> finalCharts;

  private ArrayList<NutritionWidget> allWidgets;

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

    finalDesiredDate = Helpers.formatDateToDefault(new LocalDate());

    getUserGoalMetrics(finalDesiredDate, "nutrition");

    getUserAddedMeals(finalDesiredDate);

    getUiForSection("nutrition", finalDesiredDate);

    setupDateCarousel();

    addNewChartBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(getActivity(), AddNewChartActivity.class);
        intent.putExtra(Constants.EXTRAS_ADD_CHART_WHAT_TYPE,
            Constants.EXTRAS_ADD_NUTRIITION_CHART);
        intent.putParcelableArrayListExtra(Constants.BUNDLE_NUTRITION_CHARTS_MAP, finalCharts);
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

        getUserGoalMetrics(finalDesiredDate, "nutrition");
        getUserAddedMeals(finalDesiredDate);
        getUiForSection("nutrition", finalDesiredDate);

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

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String scanContent;

    switch (requestCode) {
      case ADD_NEW_NUTRITION_CHART_REQUEST:
        if (data != null) {
          DataChart chartObject = data.getParcelableExtra(Constants.EXTRAS_CHART_OBJECT);

          finalCharts.add(chartObject);

          addNewBarChart(chartObject);
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

  private void setupDateCarousel() {
    dateCarouselLayout.addClickListener(new DateCarousel.CarouselClickListener() {
      @Override public void handleClick(String todayDate, String finalDate) {
        showProgressBarsForLoading();

        getUserGoalMetrics(finalDate, "nutrition");
        getUserAddedMeals(finalDate);
        getUiForSection("nutrition", finalDate);
      }
    });

    dateCarouselLayout.post(new Runnable() {
      @Override public void run() {
        dateCarouselLayout.setSmoothScrollingEnabled(true);
        dateCarouselLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
      }
    });
  }

  private void searchForMealBarcode(String barcode, final String mealType) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.searching_for_barcode_meal));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.searching_for_barcode_meal);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.searchForMealBarcode(barcode, new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        switch (response.code()) {
          case 200:

            waitingDialog.dismiss();

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
                  Helpers.formatDateToDefault(new LocalDate()));
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
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  private void getUserGoalMetrics(final String date, final String type) {
    dataAccessHandler.getUserGoalMetrics(date, type, new Callback<UserGoalMetricsResponse>() {
      @Override public void onResponse(Call<UserGoalMetricsResponse> call,
          Response<UserGoalMetricsResponse> response) {

        switch (response.code()) {
          case 200:
            String maxValue =
                response.body().getData().getBody().getMetrics().getCalories().getMaxValue();

            String currentValue =
                response.body().getData().getBody().getMetrics().getCalories().getValue();

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

            break;
        }
      }

      @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
      }
    });
  }

  private void getUiForSection(String section, String desiredDate) {
    String finalUrl;

    if (desiredDate == null) {
      finalUrl = Constants.BASE_URL_ADDRESS + "user/ui?section=" + section;
    } else {
      finalUrl = Constants.BASE_URL_ADDRESS + "user/ui?section=" + section + "&date=" + desiredDate;
    }

    dataAccessHandler.getUiForSection(finalUrl, new Callback<UiResponse>() {
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
              nutritionDataChart.setPosition(
                  Integer.parseInt(chartsMapFromAPI.get(i).getPosition()));
              nutritionDataChart.setType(chartsMapFromAPI.get(i).getSlug());
              nutritionDataChart.setUsername(chartsMapFromAPI.get(i).getSlug());
              nutritionDataChart.setChart_id(chartsMapFromAPI.get(i).getChartId());
              nutritionDataChart.setChartData(
                  (ArrayList<AuthenticationResponseChartData>) chartsMapFromAPI.get(i).getData());

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

        alertDialog.setMessage(
            getActivity().getResources().getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  private void updateUserWidgets(int[] widgetIds, int[] widgetPositions) {
    dataAccessHandler.updateUserWidgets(widgetIds, widgetPositions,
        new Callback<DefaultGetResponse>() {
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
    dataAccessHandler.updateUserCharts(chartIds, chartPositions,
        new Callback<DefaultGetResponse>() {
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
    dataAccessHandler.getUserAddedMealsOnDate(desiredDate, new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
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

            break;
        }
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setMessage(
            getActivity().getResources().getString(R.string.server_error_got_returned));
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

        addNewBarChart(chart);
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

  @Subscribe public void chartDeleted(DataChartDeletedEvent event) {

    DataChart chartObject = event.getChartObject();

    deleteUserChart(chartObject);
  }

  @Subscribe public void reflectMealEntryChanged(MealEntryManipulatedEvent event) {
    getUserAddedMeals(finalDesiredDate);
    getUiForSection("nutrition", finalDesiredDate);

    metricProgressBar.setProgress(
        ((Helpers.getNumberFromFromattedString(metricCounterTV.getText().toString())
            + Helpers.getNumberFromFromattedString(activeTV.getText().toString())) * 100)
            / Helpers.getNumberFromFromattedString(goalTV.getText().toString()));

    getUserGoalMetrics(finalDesiredDate, "nutrition");

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

    updateUserWidgets(widgets, positions);
  }

  @Subscribe public void updateChartsOrder(DataChartsOrderChangedEvent event) {
    List<DataChart> allDataCharts = event.getDataChartsListExtra();

    cards_container.removeAllViews();

    for (DataChart chart : allDataCharts) {
      addNewBarChart(chart);
    }

    int[] charts = new int[allDataCharts.size()];
    int[] chartPositions = new int[allDataCharts.size()];

    for (int i = 0; i < allDataCharts.size(); i++) {
      charts[i] = allDataCharts.get(i).getChart_id();
      chartPositions[i] = allDataCharts.get(i).getPosition();
    }

    updateUserCharts(charts, chartPositions);
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

  private void addNewBarChart(DataChart chartObject) {
    getDefaultChartMonthlyBreakdown(chartObject);
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
                intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE,
                    Constants.EXTRAS_NUTRITION_FRAGMENT);
                intent.putExtra(Constants.EXTRAS_CHART_OBJECT, chartObject);
                intent.putExtra(Constants.BUNDLE_SLUG_BREAKDOWN_DATA,
                    response.body().getData().getBody().getData());
                getActivity().startActivity(intent);
                break;
            }
          }

          @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void getDefaultChartMonthlyBreakdown(final DataChart chartObject) {

    final String todayDate;
    todayDate = dt.toString();

    dataAccessHandler.getPeriodicalChartData(dt.minusMonths(1).toString(), todayDate, "nutrition",
        chartObject.getName(), new Callback<ChartMetricBreakdownResponse>() {
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

                if (getActivity() != null) {
                  CustomBarChart customBarChart =
                      new CustomBarChart(getActivity().getApplication(), chartObject);

                  /**
                   * Open the breakdown for the chart
                   */
                  customBarChart.addClickListener(new CustomBarChart.CustomBarChartClickListener() {
                    @Override public void handleClick(DataChart chartObject) {
                      getSlugBreakdownForChart(chartObject);
                    }
                  });

                  customBarChart.setBarChartDataAndDates(cards_container, newChartData,
                      Constants.EXTRAS_NUTRITION_FRAGMENT);
                }
              }
            }
          }

          @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
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

                for (int i = 0; i < finalCharts.size(); i++) {
                  if (finalCharts.get(i).getName().equals(chartObject.getName())) {
                    finalCharts.remove(i);
                  }
                }

                setupChartViews(finalCharts);

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
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