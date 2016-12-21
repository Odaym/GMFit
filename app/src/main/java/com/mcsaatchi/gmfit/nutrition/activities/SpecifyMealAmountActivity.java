package com.mcsaatchi.gmfit.nutrition.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusPoster;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MealMetricsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionalFactsListAdapter;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionalFact;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SpecifyMealAmountActivity extends BaseActivity {
  private static final int MEAL_AMOUNT_SPECIFIED = 536;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.nutritionFactsList) ListView nutritionFactsList;
  @Bind(R.id.addToDiaryBTN) Button addToDiaryBTN;
  @Bind(R.id.mealAmountET) FormEditText mealAmountET;
  @Bind(R.id.measurementUnitTV) TextView measurementUnitTV;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private NutritionalFactsListAdapter nutritionFactsListAdapter;

  private List<NutritionalFact> nutritionalFacts = new ArrayList<>();

  private MealItem mealItem;
  private boolean purposeIsEditMeal = false;
  private boolean purposeIsToAddMealToDate = false;
  private String chosenDate;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_specify_meal_amount);

    ButterKnife.bind(this);

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

        measurementUnitTV.setText(mealItem.getMeasurementUnit());

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

    dataAccessHandler.getMealMetrics("http://gmfit.mcsaatchi.me/api/v1/meals/" + meal_id,
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
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void initNutritionFactsList(List<NutritionalFact> nutritionalFacts,
      final int caloriesForThisMeal) {

    nutritionFactsListAdapter = new NutritionalFactsListAdapter(this, nutritionalFacts);

    nutritionFactsList.setAdapter(nutritionFactsListAdapter);

    addToDiaryBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Helpers.validateFields(allFields)) {

          final ProgressDialog waitingDialog = new ProgressDialog(SpecifyMealAmountActivity.this);
          waitingDialog.setTitle(getString(R.string.adding_new_meal_dialog_title));
          waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
          waitingDialog.show();

          final AlertDialog alertDialog =
              new AlertDialog.Builder(SpecifyMealAmountActivity.this).create();
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
    dataAccessHandler.storeNewMeal(mealItem.getMeal_id(),
        Float.parseFloat(mealAmountET.getText().toString()), mealItem.getType(), chosenDate,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                mealItem.setAmount(mealAmountET.getText().toString());
                mealItem.setTotalCalories(
                    (int) (Float.parseFloat(mealItem.getAmount()) * caloriesForThisMeal));

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItem);
                setResult(MEAL_AMOUNT_SPECIFIED, resultIntent);

                EventBusSingleton.getInstance()
                    .post(new EventBusPoster(Constants.EXTRAS_CREATED_NEW_MEAL_ENTRY_ON_DATE));

                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mealAmountET.getWindowToken(), 0);

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(SpecifyMealAmountActivity.this).create();
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void updateUserMealOnCertainDate(final ProgressDialog waitingDialog,
      final int caloriesForThisMeal) {
    dataAccessHandler.updateUserMeals(mealItem.getInstance_id(),
        Integer.parseInt(mealAmountET.getText().toString()), new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                mealItem.setAmount(mealAmountET.getText().toString());
                mealItem.setTotalCalories(
                    Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

                EventBusSingleton.getInstance()
                    .post(new EventBusPoster(Constants.EXTRAS_UPDATED_MEAL_ENTRY_ON_DATE));

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(SpecifyMealAmountActivity.this).create();
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void updateUserMeal(final ProgressDialog waitingDialog, final int caloriesForThisMeal) {
    dataAccessHandler.updateUserMeals(mealItem.getInstance_id(),
        Float.parseFloat(mealAmountET.getText().toString()), new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                mealItem.setAmount(mealAmountET.getText().toString());
                mealItem.setTotalCalories(
                    (int) (Float.parseFloat(mealItem.getAmount()) * caloriesForThisMeal));

                EventBusSingleton.getInstance()
                    .post(new EventBusPoster(Constants.EXTRAS_UPDATED_MEAL_ENTRY));

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog =
                new AlertDialog.Builder(SpecifyMealAmountActivity.this).create();
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }
}
