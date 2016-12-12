package com.mcsaatchi.gmfit.nutrition.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.RecentMealsResponse;
import com.mcsaatchi.gmfit.architecture.rest.RecentMealsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.nutrition.adapters.SimpleSectioned_ListAdapter;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewMealItem_Activity extends Base_Activity {

  private static final int SECTION_VIEWTYPE = 1;
  private static final int ITEM_VIEWTYPE = 2;

  private static final int MEAL_AMOUNT_SPECIFIED = 536;

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

  private List<MealItem> mealItems = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {

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

    setupToolbar(toolbar, actionBarTitle, true);

    loadRecentMealsFromServer(mealType);

    mealItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (((MealItem) adapterView.getItemAtPosition(position)).getSectionType()
            == ITEM_VIEWTYPE) {
          Intent intent =
              new Intent(AddNewMealItem_Activity.this, SpecifyMealAmount_Activity.class);

          if (purposeIsToAddMealToDate) {
            intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, true);
          }

          intent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, chosenDate);
          intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItems.get(position));
          startActivityForResult(intent, MEAL_AMOUNT_SPECIFIED);
        }
      }
    });

    mealItemsList.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView absListView, int i) {
        /**
         * Hide keyboard
         */
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
        /**
         * EditText is empty
         */
        if (charSequence.toString().isEmpty()) {
          searchResultsHintTV.setVisibility(View.GONE);

          searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
          searchIconIV.setOnClickListener(null);

          searchResultsListLayout.setVisibility(View.VISIBLE);
          requestMealLayout.setVisibility(View.GONE);

          loadRecentMealsFromServer(mealType);
        } else if (charSequence.toString().length() > 2) {
          /**
           * If the search box includes enough characters to warrant a search
           */

          pb_loading_indicator.setVisibility(View.VISIBLE);

          new Handler().postDelayed(new Runnable() {
            @Override public void run() {
              searchIconIV.setImageResource(R.drawable.ic_clear_search);
              searchIconIV.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                  searchMealsAutoCompleTV.setText("");

                  /**
                   * Hide keyboard
                   */
                  InputMethodManager imm =
                      (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
              });

              findMeals(charSequence.toString(), new Callback<SearchMealItemResponse>() {
                @Override public void onResponse(Call<SearchMealItemResponse> call,
                    Response<SearchMealItemResponse> response) {

                  final List<MealItem> mealsReturned = new ArrayList<>();

                  List<SearchMealItemResponseDatum> mealsResponse =
                      response.body().getData().getBody().getData();

                  for (int i = 0; i < mealsResponse.size(); i++) {
                    MealItem mealItem = new MealItem();

                    mealItem.setName(mealsResponse.get(i).getName());
                    mealItem.setMeasurementUnit(mealsResponse.get(i).getMeasurementUnit());
                    mealItem.setMeal_id(mealsResponse.get(i).getId());
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
                    mealNotFoundTitleTV.setText("\"" + charSequence.toString() + "\"");

                    requestMealBTN.setText(
                        getResources().getString(R.string.request_new_meal_button));
                    requestMealBTN.setAlpha(1);
                    requestMealBTN.setEnabled(true);

                    requestMealBTN.setOnClickListener(new View.OnClickListener() {
                      @Override public void onClick(View view) {
                        final ProgressDialog waitingDialog =
                            new ProgressDialog(AddNewMealItem_Activity.this);
                        waitingDialog.setTitle(
                            getResources().getString(R.string.requesting_meal_item_dialog_title));
                        waitingDialog.setMessage(
                            getResources().getString(R.string.please_wait_dialog_message));
                        waitingDialog.show();

                        final AlertDialog alertDialog =
                            new AlertDialog.Builder(AddNewMealItem_Activity.this).create();
                        alertDialog.setTitle(R.string.requesting_meal_item_dialog_title);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                            getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (waitingDialog.isShowing()) waitingDialog.dismiss();
                              }
                            });

                        dataAccessHandler.requestNewMeal(
                            prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
                                Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), charSequence.toString(),
                            new Callback<DefaultGetResponse>() {
                              @Override public void onResponse(Call<DefaultGetResponse> call,
                                  Response<DefaultGetResponse> response) {
                                switch (response.code()) {
                                  case 200:
                                    waitingDialog.dismiss();

                                    requestMealBTN.setText(getResources().getString(
                                        R.string.request_new_meal_sent_thanks));
                                    requestMealBTN.setAlpha(0.5f);
                                    requestMealBTN.setEnabled(false);
                                    break;
                                }
                              }

                              @Override
                              public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                                Timber.d("Call failed with error : %s", t.getMessage());
                                final AlertDialog alertDialog =
                                    new AlertDialog.Builder(AddNewMealItem_Activity.this).create();
                                alertDialog.setMessage(
                                    getString(R.string.error_response_from_server_incorrect));
                                alertDialog.show();
                              }
                            });
                      }
                    });
                  } else {
                    initMealsList(mealsReturned);
                  }

                  searchResultsHintTV.setVisibility(View.VISIBLE);
                  pb_loading_indicator.setVisibility(View.INVISIBLE);
                }

                @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
                }
              });
            }
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
            /**
             * Meal amount specified from SpecifyMealAmount_Activity and this is a new meal
             */
            finish();

            break;
        }

        finish();
      }
    }
  }

  private void findMeals(String mealName,
      final Callback<SearchMealItemResponse> mealItemsResponse) {
    dataAccessHandler.findMeals(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        mealName, new Callback<SearchMealItemResponse>() {
          @Override public void onResponse(Call<SearchMealItemResponse> call,
              Response<SearchMealItemResponse> response) {
            switch (response.code()) {
              case 200:
                mealItemsResponse.onResponse(null, response);
                break;
            }
          }

          @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(AddNewMealItem_Activity.this).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void loadRecentMealsFromServer(String mealType) {
    if (mealType.equals("Snacks")) mealType = "Snack";

    final String finalMealType = mealType;

    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(
        getResources().getString(R.string.fetching_available_meals_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.fetching_available_meals_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getRecentMeals(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        "http://gmfit.mcsaatchi.me/api/v1/user/meals/recent?when=" + mealType.toLowerCase(),
        new Callback<RecentMealsResponse>() {
          @Override public void onResponse(Call<RecentMealsResponse> call,
              Response<RecentMealsResponse> response) {
            ArrayList<RecentMealsResponseBody> recentMealsFromAPI =
                (ArrayList<RecentMealsResponseBody>) response.body().getData().getBody();

            MealItem recentlyAddedMealItem = new MealItem();
            recentlyAddedMealItem.setName("Recently Added");
            recentlyAddedMealItem.setSectionType(1);

            mealItems.clear();

            mealItems.add(recentlyAddedMealItem);

            for (int i = 0; i < recentMealsFromAPI.size(); i++) {
              MealItem item = new MealItem();
              item.setType(finalMealType);
              item.setMeal_id(recentMealsFromAPI.get(i).getId());
              item.setSectionType(2);
              item.setMeasurementUnit(recentMealsFromAPI.get(i).getMeasurementUnit());
              item.setName(recentMealsFromAPI.get(i).getName());

              mealItems.add(item);
            }

            initMealsList(mealItems);

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<RecentMealsResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void initMealsList(List<MealItem> mealsToShow) {
    SimpleSectioned_ListAdapter simpleSectionedListAdapter =
        new SimpleSectioned_ListAdapter(this, mealsToShow);

    mealItemsList.setAdapter(simpleSectionedListAdapter);
  }
}
