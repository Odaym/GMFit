package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.NutritionalFactsList_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionalFact;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponseDatum;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecifyMealAmount_Activity extends Base_Activity {
  private static final int MEAL_AMOUNT_SPECIFIED = 536;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.nutritionFactsList) ListView nutritionFactsList;
  @Bind(R.id.addToDiaryBTN) Button addToDiaryBTN;
  @Bind(R.id.mealAmountET) FormEditText mealAmountET;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private SharedPreferences prefs;
  private NutritionalFactsList_Adapter nutritionFactsListAdapter;

  private List<NutritionalFact> nutritionalFacts = new ArrayList<>();

  private MealItem mealItem;
  private boolean purposeIsEditMeal = false;
  private boolean purposeIsToAddMealToDate = false;
  private String chosenDate;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_specify_meal_amount);

    ButterKnife.bind(this);

    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    allFields.add(mealAmountET);

    if (getIntent().getExtras() != null) {
      mealItem = getIntent().getExtras().getParcelable(Constants.EXTRAS_MEAL_OBJECT_DETAILS);
      purposeIsEditMeal =
          getIntent().getExtras().getBoolean(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING);
      purposeIsToAddMealToDate = getIntent().getExtras()
          .getBoolean(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, false);

      chosenDate = getIntent().getExtras().getString(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, "");

      if (mealItem != null) {
        setupToolbar(toolbar, mealItem.getName(), true);

        if (purposeIsEditMeal) {
          mealAmountET.setText(mealItem.getAmount());
        }

        mealAmountET.setSelection(mealAmountET.getText().toString().length());

        getMealMetrics(mealItem.getMeal_id());
      }
    }
  }

  private void getMealMetrics(int meal_id) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.fetching_meal_info_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.fetching_meal_info_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    DataAccessHandler.getInstance()
        .getMealMetrics(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
            "http://gmfit.mcsaatchi.me/api/v1/meals/" + meal_id,
            new Callback<MealMetricsResponse>() {
              @Override public void onResponse(Call<MealMetricsResponse> call,
                  Response<MealMetricsResponse> response) {
                switch (response.code()) {
                  case 200:
                    waitingDialog.dismiss();

                    List<MealMetricsResponseDatum> mealMetricsResponseDatumList =
                        response.body().getData().getBody().getMetrics();

                    for (int i = 0; i < mealMetricsResponseDatumList.size(); i++) {
                      NutritionalFact nutritionalFact = new NutritionalFact();

                      nutritionalFact.setName(mealMetricsResponseDatumList.get(i).getName());
                      nutritionalFact.setUnit(
                          (int) Double.parseDouble(mealMetricsResponseDatumList.get(i).getValue())
                              + " "
                              + mealMetricsResponseDatumList.get(i).getUnit());

                      nutritionalFacts.add(nutritionalFact);
                    }

                    if (nutritionalFacts.size() > 0) {
                      int caloriesForThisMeal = 0;

                      for (int i = 0; i < nutritionalFacts.size(); i++) {
                        if (nutritionalFacts.get(i).getName().equals("Calories")) {
                          caloriesForThisMeal =
                              Integer.parseInt(nutritionalFacts.get(i).getUnit().split(" ")[0]);
                        }
                      }

                      initNutritionFactsList(nutritionalFacts, caloriesForThisMeal);
                    }

                    break;
                }
              }

              @Override public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
              }
            });
  }

  private void initNutritionFactsList(List<NutritionalFact> nutritionalFacts,
      final int caloriesForThisMeal) {

    nutritionFactsListAdapter = new NutritionalFactsList_Adapter(this, nutritionalFacts);

    nutritionFactsList.setAdapter(nutritionFactsListAdapter);

    addToDiaryBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Helpers.validateFields(allFields)) {

          final ProgressDialog waitingDialog = new ProgressDialog(SpecifyMealAmount_Activity.this);
          waitingDialog.setTitle(getString(R.string.adding_new_meal_dialog_title));
          waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
          waitingDialog.show();

          final AlertDialog alertDialog =
              new AlertDialog.Builder(SpecifyMealAmount_Activity.this).create();
          alertDialog.setTitle(R.string.adding_new_meal_dialog_title);
          alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();

                  if (waitingDialog.isShowing()) waitingDialog.dismiss();
                }
              });

          if (purposeIsToAddMealToDate) {
            Log.d("TAG", "purpose add meal on specific date");
            if (purposeIsEditMeal) {
              updateUserMealOnCertainDate(waitingDialog, caloriesForThisMeal);
              Log.d("TAG", "onClick: Editing");
            } else {
              Log.d("TAG", "onClick: Storing new");
              storeMealOnCertainDate(waitingDialog, caloriesForThisMeal, chosenDate);
            }
          } else {
            Log.d("TAG", "purpose edit meal existing for TODAY");
            if (purposeIsEditMeal) {
              Log.d("TAG", "Editing");
              updateUserMeal(waitingDialog, caloriesForThisMeal);
            } else {
              Log.d("TAG", "Storing new");
              storeMealOnCertainDate(waitingDialog, caloriesForThisMeal, chosenDate);
            }
          }
        }
      }
    });
  }

  private void storeMealOnCertainDate(final ProgressDialog waitingDialog,
      final int caloriesForThisMeal, String chosenDate) {
    DataAccessHandler.getInstance()
        .storeNewMeal(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealItem.getMeal_id(),
            Integer.parseInt(mealAmountET.getText().toString()), mealItem.getType(), chosenDate,
            new Callback<DefaultGetResponse>() {
              @Override public void onResponse(Call<DefaultGetResponse> call,
                  Response<DefaultGetResponse> response) {
                switch (response.code()) {
                  case 200:
                    waitingDialog.dismiss();

                    mealItem.setAmount(mealAmountET.getText().toString());
                    mealItem.setTotalCalories(
                        Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItem);
                    setResult(MEAL_AMOUNT_SPECIFIED, resultIntent);

                    EventBus_Singleton.getInstance()
                        .post(new EventBus_Poster(Constants.EXTRAS_CREATED_NEW_MEAL_ENTRY_ON_DATE));

                    finish();

                    break;
                }
              }

              @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                waitingDialog.dismiss();
              }
            });
  }

  private void storeNewMeal(final ProgressDialog waitingDialog, final int caloriesForThisMeal) {
    LocalDateTime ld = new LocalDateTime();

    DateTime dateTime = new LocalDateTime(ld.toString()).toDateTime(DateTimeZone.UTC);

    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss");

    String finalDateTime = dateTimeFormatter.print(dateTime);

    DataAccessHandler.getInstance()
        .storeNewMeal(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealItem.getMeal_id(),
            Integer.parseInt(mealAmountET.getText().toString()), mealItem.getType(), finalDateTime,
            new Callback<DefaultGetResponse>() {
              @Override public void onResponse(Call<DefaultGetResponse> call,
                  Response<DefaultGetResponse> response) {
                switch (response.code()) {
                  case 200:
                    waitingDialog.dismiss();

                    mealItem.setAmount(mealAmountET.getText().toString());
                    mealItem.setTotalCalories(
                        Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItem);
                    setResult(MEAL_AMOUNT_SPECIFIED, resultIntent);

                    EventBus_Singleton.getInstance()
                        .post(new EventBus_Poster(Constants.EXTRAS_CREATED_NEW_MEAL_ENTRY));

                    finish();

                    break;
                }
              }

              @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                waitingDialog.dismiss();
              }
            });
  }

  private void updateUserMealOnCertainDate(final ProgressDialog waitingDialog,
      final int caloriesForThisMeal) {
    DataAccessHandler.getInstance()
        .updateUserMeals(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealItem.getInstance_id(),
            Integer.parseInt(mealAmountET.getText().toString()),
            new Callback<DefaultGetResponse>() {
              @Override public void onResponse(Call<DefaultGetResponse> call,
                  Response<DefaultGetResponse> response) {
                switch (response.code()) {
                  case 200:
                    waitingDialog.dismiss();

                    mealItem.setAmount(mealAmountET.getText().toString());
                    mealItem.setTotalCalories(
                        Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                    EventBus_Singleton.getInstance()
                        .post(new EventBus_Poster(Constants.EXTRAS_UPDATED_MEAL_ENTRY_ON_DATE));

                    finish();

                    break;
                }
              }

              @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                waitingDialog.dismiss();
              }
            });
  }

  private void updateUserMeal(final ProgressDialog waitingDialog, final int caloriesForThisMeal) {
    DataAccessHandler.getInstance()
        .updateUserMeals(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealItem.getInstance_id(),
            Integer.parseInt(mealAmountET.getText().toString()),
            new Callback<DefaultGetResponse>() {
              @Override public void onResponse(Call<DefaultGetResponse> call,
                  Response<DefaultGetResponse> response) {
                switch (response.code()) {
                  case 200:
                    waitingDialog.dismiss();

                    mealItem.setAmount(mealAmountET.getText().toString());
                    mealItem.setTotalCalories(
                        Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                    EventBus_Singleton.getInstance()
                        .post(new EventBus_Poster(Constants.EXTRAS_UPDATED_MEAL_ENTRY));

                    finish();

                    break;
                }
              }

              @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                waitingDialog.dismiss();
              }
            });
  }
}
