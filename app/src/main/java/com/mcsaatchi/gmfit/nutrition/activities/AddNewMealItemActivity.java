package com.mcsaatchi.gmfit.nutrition.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.RecentMealsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.nutrition.adapters.SimpleSectionedListAdapter;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import java.util.ArrayList;
import java.util.List;

public class AddNewMealItemActivity extends BaseActivity
    implements AddNewMealItemActivityPresenter.AddNewMealItemView {

  public static final int MEAL_AMOUNT_SPECIFIED = 536;
  private static final int SECTION_VIEWTYPE = 1;
  private static final int ITEM_VIEWTYPE = 2;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.mealItemsList) ListView mealItemsList;
  @Bind(R.id.searchMealsAutoCompleTV) EditText searchMealsAutoCompleTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;
  @Bind(R.id.searchResultsHintTV) TextView searchResultsHintTV;
  @Bind(R.id.pb_loading_indicator) ProgressBar pb_loading_indicator;
  @Bind(R.id.searchResultsListLayout) LinearLayout searchResultsListLayout;
  @Bind(R.id.requestMealLayout) LinearLayout requestMealLayout;
  @Bind(R.id.meal_not_found_meal_title) TextView mealNotFoundTitleTV;
  @Bind(R.id.requestMealBTN) Button requestMealBTN;

  private String mealType;
  private boolean purposeIsToAddMealToDate = false;
  private String chosenDate;

  private AddNewMealItemActivityPresenter presenter;

  private List<MealItem> mealItems = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

    presenter = new AddNewMealItemActivityPresenter(this, dataAccessHandler);

    String actionBarTitle;

    if (getIntent().getExtras() != null) {
      mealType = getIntent().getExtras().getString(Constants.EXTRAS_MAIN_MEAL_NAME);
      purposeIsToAddMealToDate = getIntent().getExtras()
          .getBoolean(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, false);
      chosenDate = getIntent().getExtras().getString(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, "");

      actionBarTitle = getString(R.string.add_new_meal_item_activity_title) + " " + mealType;
    } else {
      actionBarTitle = getString(R.string.app_name);
      mealType = getString(R.string.meal_headline_title_breakfast);
    }

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_meal_item);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, actionBarTitle, true);

    loadRecentMealsFromServer(mealType);

    mealItemsList.setOnItemClickListener((adapterView, view, position, l) -> {
      if (((MealItem) adapterView.getItemAtPosition(position)).getSectionType() == ITEM_VIEWTYPE) {
        Intent intent = new Intent(AddNewMealItemActivity.this, SpecifyMealAmountActivity.class);

        if (purposeIsToAddMealToDate) {
          intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, true);
        }

        intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, chosenDate);
        intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItems.get(position));
        startActivityForResult(intent, MEAL_AMOUNT_SPECIFIED);
      }
    });

    mealItemsList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView absListView, int i) {

        //Hide keyboard

        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
      }

      @Override public void onScroll(AbsListView absListView, int i, int i1, int i2) {
      }
    });

    searchMealsAutoCompleTV.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
          searchResultsHintTV.setVisibility(View.GONE);

          searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
          searchIconIV.setOnClickListener(null);

          searchResultsListLayout.setVisibility(View.VISIBLE);
          requestMealLayout.setVisibility(View.GONE);

          loadRecentMealsFromServer(mealType);
        } else if (charSequence.toString().length() > 2) {

          pb_loading_indicator.setVisibility(View.VISIBLE);

          new Handler().postDelayed(() -> {
            searchIconIV.setImageResource(R.drawable.ic_clear_search);
            searchIconIV.setOnClickListener(view -> {
              searchMealsAutoCompleTV.setText("");

              InputMethodManager imm =
                  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            });

            presenter.findMeals(charSequence.toString());
          }, 500);
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (data != null) {
      MealItem mealItem = data.getParcelableExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS);

      if (mealItem != null) {
        switch (resultCode) {
          case MEAL_AMOUNT_SPECIFIED:
            //Meal amount specified from SpecifyMealAmountActivity and this is a new meal
            finish();
            break;
        }

        finish();
      }
    }
  }

  @Override
  public void displayMealResults(String mealName, List<SearchMealItemResponseDatum> mealsResponse) {
    final List<MealItem> mealsReturned = new ArrayList<>();

    for (int i3 = 0; i3 < mealsResponse.size(); i3++) {
      MealItem mealItem = new MealItem();

      mealItem.setName(mealsResponse.get(i3).getName());
      mealItem.setMeasurementUnit(mealsResponse.get(i3).getMeasurementUnit());
      mealItem.setMeal_id(mealsResponse.get(i3).getId());
      if (mealType.equals("Snacks")) {
        mealItem.setType("Snack");
      } else {
        mealItem.setType(mealType);
      }
      mealItem.setSectionType(ITEM_VIEWTYPE);

      mealsReturned.add(mealItem);
    }

    mealItems.clear();

    mealItems = mealsReturned;

    if (mealItems.isEmpty()) {
      searchResultsListLayout.setVisibility(View.GONE);
      requestMealLayout.setVisibility(View.VISIBLE);
      mealNotFoundTitleTV.setText("\"" + mealName + "\"");

      requestMealBTN.setText(getResources().getString(R.string.request_new_meal_button));
      requestMealBTN.setAlpha(1);
      requestMealBTN.setEnabled(true);

      requestMealBTN.setOnClickListener(view -> presenter.requestNewMeal(mealName));
    } else {
      initMealsList(mealsReturned);
    }

    searchResultsHintTV.setVisibility(View.VISIBLE);
    pb_loading_indicator.setVisibility(View.INVISIBLE);
  }

  @Override public void displaySucceededMealRequest() {
    requestMealBTN.setText(getResources().getString(R.string.request_new_meal_sent_thanks));
    requestMealBTN.setAlpha(0.5f);
    requestMealBTN.setEnabled(false);
  }

  @Override public void displayRecentMeals(ArrayList<RecentMealsResponseBody> recentMealsFromAPI,
      String mealType) {
    MealItem recentlyAddedMealItem = new MealItem();
    recentlyAddedMealItem.setName("Recently Added");
    recentlyAddedMealItem.setSectionType(1);

    mealItems.clear();

    mealItems.add(recentlyAddedMealItem);

    for (int i = 0; i < recentMealsFromAPI.size(); i++) {
      MealItem item = new MealItem();
      item.setType(mealType);
      item.setMeal_id(recentMealsFromAPI.get(i).getId());
      item.setSectionType(2);
      item.setMeasurementUnit(recentMealsFromAPI.get(i).getMeasurementUnit());
      item.setName(recentMealsFromAPI.get(i).getName());

      mealItems.add(item);
    }

    initMealsList(mealItems);
  }

  private void loadRecentMealsFromServer(String mealType) {
    if (mealType.equals("Snacks")) mealType = "Snack";

    final String finalMealType = mealType;

    presenter.getRecentMeals(finalMealType);
  }

  private void initMealsList(List<MealItem> mealsToShow) {
    SimpleSectionedListAdapter simpleSectionedListAdapter =
        new SimpleSectionedListAdapter(this, mealsToShow);

    mealItemsList.setAdapter(simpleSectionedListAdapter);
  }
}
