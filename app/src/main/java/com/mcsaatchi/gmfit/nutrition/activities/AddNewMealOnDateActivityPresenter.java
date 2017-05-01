package com.mcsaatchi.gmfit.nutrition.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponseInner;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class AddNewMealOnDateActivityPresenter extends BaseActivityPresenter {
  private AddNewMealOnDateActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  AddNewMealOnDateActivityPresenter(AddNewMealOnDateActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getUserAddedMealsOnDate(String chosenDate) {
    dataAccessHandler.getUserAddedMealsOnDate(chosenDate, new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayUserAddedMeals(response.body().getData().getBody().getData().getBreakfast(),
                response.body().getData().getBody().getData().getLunch(),
                response.body().getData().getBody().getData().getDinner(),
                response.body().getData().getBody().getData().getSnack());
            break;
        }
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  interface AddNewMealOnDateActivityView extends BaseActivityView {
    void displayUserAddedMeals(List<UserMealsResponseInner> breakfastMeals,
        List<UserMealsResponseInner> lunchMeals, List<UserMealsResponseInner> dinnerMeals,
        List<UserMealsResponseInner> snackMeals);
  }
}
