package com.mcsaatchi.gmfit.nutrition.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MealMetricsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SpecifyMealAmountActivityPresenter extends BaseActivityPresenter {
  private SpecifyMealAmountActivityView view;
  private DataAccessHandler dataAccessHandler;

  SpecifyMealAmountActivityPresenter(SpecifyMealAmountActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getMealMetrics(int meal_id) {
    view.callDisplayWaitingDialog(R.string.fetching_meal_info_dialog_title);

    dataAccessHandler.getMealMetrics(Constants.BASE_URL_ADDRESS + "meals/" + meal_id,
        new Callback<MealMetricsResponse>() {
          @Override public void onResponse(Call<MealMetricsResponse> call,
              Response<MealMetricsResponse> response) {
            switch (response.code()) {
              case 200:
                view.displayMealMetrics(response.body().getData().getBody().getMetrics());
                break;
            }
          }

          @Override public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  void storeMealOnDate(int meal_id, float servingsAmount, String when, String date,
      int caloriesForThisMeal) {
    view.callDisplayWaitingDialog(R.string.adding_new_meal_dialog_title);

    dataAccessHandler.storeNewMeal(meal_id, servingsAmount, when, date,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.handleStoreMealOnDate(caloriesForThisMeal);
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface SpecifyMealAmountActivityView extends BaseActivityView {
    void displayMealMetrics(List<MealMetricsResponseDatum> mealMetricsResponseDatumList);

    void handleStoreMealOnDate(int caloriesForThisMeal);
  }
}
