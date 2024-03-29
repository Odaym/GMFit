package com.mcsaatchi.gmfit.nutrition.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MealEntryManipulatedEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponseInner;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleSwipeItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMealsRecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.squareup.otto.Subscribe;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AddNewMealOnDateActivity extends BaseActivity
    implements AddNewMealOnDateActivityPresenter.AddNewMealOnDateActivityView {
  private static final int BARCODE_CAPTURE_RC = 773;
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
  @Bind(R.id.toolbar) Toolbar toolbar;

  private String chosenDate;
  private ArrayList<MealItem> finalBreakfastMeals = new ArrayList<>();
  private ArrayList<MealItem> finalLunchMeals = new ArrayList<>();
  private ArrayList<MealItem> finalDinnerMeals = new ArrayList<>();
  private ArrayList<MealItem> finalSnackMeals = new ArrayList<>();
  private UserMealsRecyclerAdapterDragSwipe userMealsRecyclerAdapter;

  private AddNewMealOnDateActivityPresenter presenter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_meal_on_date);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    presenter = new AddNewMealOnDateActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      chosenDate = getIntent().getExtras().getString(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, "");
      try {
        chosenDate = new SimpleDateFormat("dd MMMM, yyyy").format(
            new SimpleDateFormat("yyyy-MM-dd").parse(chosenDate));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    setupToolbar(getClass().getSimpleName(), toolbar, chosenDate, true);

    hookupMealSectionRowsClickListeners();

    presenter.getUserAddedMealsOnDate(chosenDate);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void displayUserAddedMeals(List<UserMealsResponseInner> breakfastMeals,
      List<UserMealsResponseInner> lunchMeals, List<UserMealsResponseInner> dinnerMeals,
      List<UserMealsResponseInner> snackMeals) {
    finalBreakfastMeals.clear();
    finalLunchMeals.clear();
    finalDinnerMeals.clear();
    finalSnackMeals.clear();

    //Insert Breakfast meals
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

      //if (breakfastMeals.get(i).getTotalCalories() != null) {
      breakfastMeal.setTotalCalories(breakfastMeals.get(i).getTotalCalories());
      //} else {
      //  breakfastMeal.setTotalCalories(0);
      //}

      finalBreakfastMeals.add(breakfastMeal);
    }

    setupMealSectionsListView(finalBreakfastMeals, "Breakfast");

    //Insert Lunch meals
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

      //if (lunchMeals.get(i).getTotalCalories() != null) {
      lunchMeal.setTotalCalories(lunchMeals.get(i).getTotalCalories());
      //} else {
      //  lunchMeal.setTotalCalories(0);
      //}

      finalLunchMeals.add(lunchMeal);
    }

    setupMealSectionsListView(finalLunchMeals, "Lunch");

    //Insert Dinner meals
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

      //if (dinnerMeals.get(i).getTotalCalories() != null) {
      dinnerMeal.setTotalCalories(dinnerMeals.get(i).getTotalCalories());
      //} else {
      //  dinnerMeal.setTotalCalories(0);
      //}

      finalDinnerMeals.add(dinnerMeal);
    }

    setupMealSectionsListView(finalDinnerMeals, "Dinner");

    //Insert Snack meals
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

      //if (snackMeals.get(i).getTotalCalories() != null) {
      snackMeal.setTotalCalories(snackMeals.get(i).getTotalCalories());
      //} else {
      //  snackMeal.setTotalCalories(0);
      //}

      finalSnackMeals.add(snackMeal);
    }

    setupMealSectionsListView(finalSnackMeals, "Snack");
  }

  @Subscribe public void reflectMealEntryChanged(MealEntryManipulatedEvent event) {
    presenter.getUserAddedMealsOnDate(chosenDate);
  }

  public void handleScanMealEntry() {
    Intent intent = new Intent(AddNewMealOnDateActivity.this, BarcodeCaptureActivity.class);
    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
    startActivityForResult(intent, BARCODE_CAPTURE_RC);
  }

  private void openMealEntryPickerActivity(String mainMealName) {
    Intent intent = new Intent(AddNewMealOnDateActivity.this, AddNewMealItemActivity.class);
    intent.putExtra(Constants.EXTRAS_MAIN_MEAL_NAME, mainMealName);
    intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, chosenDate);
    startActivity(intent);
  }

  private void hookupMealSectionRowsClickListeners() {
    addNewEntryBTN_BREAKFAST.setOnClickListener(
        view -> openMealEntryPickerActivity(chartTitleTV_BREAKFAST.getText().toString()));
    scanEntryBTN_BREAKFAST.setOnClickListener(view -> handleScanMealEntry());

    addNewEntryBTN_LUNCH.setOnClickListener(
        view -> openMealEntryPickerActivity(chartTitleTV_LUNCH.getText().toString()));
    scanEntryBTN_LUNCH.setOnClickListener(view -> handleScanMealEntry());

    addNewEntryBTN_DINNER.setOnClickListener(
        view -> openMealEntryPickerActivity(chartTitleTV_DINNER.getText().toString()));
    scanEntryBTN_DINNER.setOnClickListener(view -> handleScanMealEntry());

    addNewEntryBTN_SNACKS.setOnClickListener(
        view -> openMealEntryPickerActivity(chartTitleTV_SNACKS.getText().toString()));
    scanEntryBTN_SNACKS.setOnClickListener(view -> handleScanMealEntry());
  }

  private void setupMealSectionsListView(ArrayList<MealItem> mealItems, String mealType) {
    RecyclerView.LayoutManager mLayoutManager =
        new LinearLayoutManager(AddNewMealOnDateActivity.this);

    ItemTouchHelper.Callback callback;
    ItemTouchHelper touchHelper;

    userMealsRecyclerAdapter = new UserMealsRecyclerAdapterDragSwipe(this, mealItems);
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

  private void hookUpMealSectionListViews(RecyclerView mealListView,
      RecyclerView.LayoutManager layoutManager, ItemTouchHelper touchHelper) {
    mealListView.addItemDecoration(new SimpleDividerItemDecoration(AddNewMealOnDateActivity.this));
    mealListView.setNestedScrollingEnabled(false);
    mealListView.setLayoutManager(layoutManager);
    mealListView.setAdapter(userMealsRecyclerAdapter);
    touchHelper.attachToRecyclerView(mealListView);
  }
}
