package com.mcsaatchi.gmfit.nutrition.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MealEntryManipulatedEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MealMetricsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionalFactsListAdapter;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionalFact;
import java.util.ArrayList;
import java.util.List;

public class SpecifyMealAmountActivity extends BaseActivity
    implements SpecifyMealAmountActivityPresenter.SpecifyMealAmountActivityView {
  private static final int MEAL_AMOUNT_SPECIFIED = 536;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.nutritionFactsList) ListView nutritionFactsList;
  @Bind(R.id.addToDiaryBTN) Button addToDiaryBTN;
  @Bind(R.id.mealAmountET) FormEditText mealAmountET;
  @Bind(R.id.measurementUnitTV) TextView measurementUnitTV;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private NutritionalFactsListAdapter nutritionFactsListAdapter;

  private List<NutritionalFact> nutritionalFacts = new ArrayList<>();

  private SpecifyMealAmountActivityPresenter presenter;
  private MealItem mealItem;
  private boolean purposeIsEditMeal = false;
  private boolean purposeIsToAddMealToDate = false;
  private String chosenDate;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_specify_meal_amount);

    ButterKnife.bind(this);

    presenter = new SpecifyMealAmountActivityPresenter(this, dataAccessHandler);

    allFields.add(mealAmountET);

    if (getIntent().getExtras() != null) {
      mealItem = getIntent().getExtras().getParcelable(Constants.EXTRAS_MEAL_OBJECT_DETAILS);
      purposeIsEditMeal =
          getIntent().getExtras().getBoolean(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING);
      purposeIsToAddMealToDate = getIntent().getExtras()
          .getBoolean(Constants.EXTRAS_MEAL_ITEM_PURPOSE_ADDING_TO_DATE, false);

      chosenDate = getIntent().getExtras().getString(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON, "");

      if (mealItem != null) {
        setupToolbar(getClass().getSimpleName(), toolbar, mealItem.getName(), true);

        if (purposeIsEditMeal) {
          mealAmountET.setText(mealItem.getAmount());
        }

        mealAmountET.setSelection(mealAmountET.getText().toString().length());

        measurementUnitTV.setText(mealItem.getMeasurementUnit());

        presenter.getMealMetrics(mealItem.getMeal_id());
      }
    }
  }

  @Override
  public void displayMealMetrics(List<MealMetricsResponseDatum> mealMetricsResponseDatumList) {
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
          caloriesForThisMeal = Integer.parseInt(nutritionalFacts.get(i).getUnit().split(" ")[0]);
        }
      }

      initNutritionFactsList(nutritionalFacts, caloriesForThisMeal);
    }
  }

  @Override public void handleStoreMealOnDate(int caloriesForThisMeal) {
    mealItem.setAmount(mealAmountET.getText().toString());
    mealItem.setTotalCalories((int) (Float.parseFloat(mealItem.getAmount()) * caloriesForThisMeal));

    Intent resultIntent = new Intent();
    resultIntent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItem);
    setResult(MEAL_AMOUNT_SPECIFIED, resultIntent);

    EventBusSingleton.getInstance().post(new MealEntryManipulatedEvent());

    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mealAmountET.getWindowToken(), 0);

    finish();
  }

  @Override public void handleUpdateMealOnDate(int caloriesForThisMeal) {
    mealItem.setAmount(mealAmountET.getText().toString());
    mealItem.setTotalCalories(Integer.parseInt(mealItem.getAmount()) * caloriesForThisMeal);

    EventBusSingleton.getInstance().post(new MealEntryManipulatedEvent());

    finish();
  }

  @Override public void handleUpdateUserMeals(int caloriesForThisMeal) {
    mealItem.setAmount(mealAmountET.getText().toString());
    mealItem.setTotalCalories(
        (int) (Float.parseFloat(mealItem.getAmount()) * caloriesForThisMeal));

    EventBusSingleton.getInstance().post(new MealEntryManipulatedEvent());

    finish();
  }

  private void initNutritionFactsList(List<NutritionalFact> nutritionalFacts,
      final int caloriesForThisMeal) {

    nutritionFactsListAdapter = new NutritionalFactsListAdapter(this, nutritionalFacts);

    nutritionFactsList.setAdapter(nutritionFactsListAdapter);

    addToDiaryBTN.setOnClickListener(view -> {
      if (Helpers.validateFields(allFields)) {
        if (purposeIsToAddMealToDate) {
          Log.d("TAG", "purpose add meal on specific date");
          if (purposeIsEditMeal) {
            presenter.updateMealOnDate(mealItem.getInstance_id(),
                Integer.parseInt(mealAmountET.getText().toString()), caloriesForThisMeal);
            Log.d("TAG", "onClick: Editing");
          } else {
            Log.d("TAG", "onClick: Storing new");
            presenter.storeMealOnDate(mealItem.getMeal_id(),
                Float.parseFloat(mealAmountET.getText().toString()), mealItem.getType(), chosenDate,
                caloriesForThisMeal);
          }
        } else {
          Log.d("TAG", "purpose edit meal existing for TODAY");
          if (purposeIsEditMeal) {
            Log.d("TAG", "Editing");
            presenter.updateUserMeals(mealItem.getInstance_id(),
                Integer.parseInt(mealAmountET.getText().toString()), caloriesForThisMeal);
          } else {
            Log.d("TAG", "Storing new");
            presenter.storeMealOnDate(mealItem.getMeal_id(),
                Float.parseFloat(mealAmountET.getText().toString()), mealItem.getType(), chosenDate,
                caloriesForThisMeal);
          }
        }
      }
    });
  }
}
