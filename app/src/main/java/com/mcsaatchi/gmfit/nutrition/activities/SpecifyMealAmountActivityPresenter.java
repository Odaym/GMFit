package com.mcsaatchi.gmfit.nutrition.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MealMetricsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SpecifyMealAmountActivityPresenter extends BaseActivityPresenter {
  private SpecifyMealAmountActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SpecifyMealAmountActivityPresenter(SpecifyMealAmountActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
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

            view.callDismissWaitingDialog();
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

  void updateMealOnDate(int instance_id, float amount, int caloriesForThisMeal) {
    view.callDisplayWaitingDialog(R.string.adding_new_meal_dialog_title);

    dataAccessHandler.updateUserMeals(instance_id, amount, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.handleUpdateMealOnDate(caloriesForThisMeal);
            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void updateUserMeals(int instance_id, float amount, int caloriesForThisMeal) {
    dataAccessHandler.updateUserMeals(instance_id, amount, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.handleUpdateUserMeals(caloriesForThisMeal);
            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface SpecifyMealAmountActivityView extends BaseActivityView {
    void displayMealMetrics(List<MealMetricsResponseDatum> mealMetricsResponseDatumList);

    void handleStoreMealOnDate(int caloriesForThisMeal);

    void handleUpdateMealOnDate(int caloriesForThisMeal);

    void handleUpdateUserMeals(int caloriesForThisMeal);
  }
}
