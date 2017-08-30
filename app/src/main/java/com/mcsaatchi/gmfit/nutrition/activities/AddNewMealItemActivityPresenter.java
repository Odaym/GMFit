package com.mcsaatchi.gmfit.nutrition.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.RecentMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.RecentMealsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AddNewMealItemActivityPresenter extends BaseActivityPresenter {
  private AddNewMealItemView view;
  private DataAccessHandlerImpl dataAccessHandler;

  AddNewMealItemActivityPresenter(AddNewMealItemView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void findMeals(String mealName) {
    dataAccessHandler.findMeals(mealName, new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayMealResults(mealName, response.body().getData().getBody().getData());
            break;
        }
      }

      @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void requestNewMeal(String mealName) {
    view.callDisplayWaitingDialog(R.string.requesting_meal_item_dialog_title);

    dataAccessHandler.requestNewMeal(mealName, new Callback<DefaultGetResponse>() {
      @Override public void onResponse(Call<DefaultGetResponse> call1,
          Response<DefaultGetResponse> response1) {
        switch (response1.code()) {
          case 200:
            view.displaySucceededMealRequest();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call1, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getRecentMeals(String mealType) {
    view.callDisplayWaitingDialog(R.string.fetching_available_meals_dialog_title);

    dataAccessHandler.getRecentMeals(
        Constants.BASE_URL_ADDRESS + "user/meals/recent?when=" + mealType.toLowerCase(),
        new Callback<RecentMealsResponse>() {
          @Override public void onResponse(Call<RecentMealsResponse> call,
              Response<RecentMealsResponse> response) {
            switch (response.code()) {
              case 200:
                view.displayRecentMeals(
                    (ArrayList<RecentMealsResponseBody>) response.body().getData().getBody(),
                    mealType);
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<RecentMealsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface AddNewMealItemView extends BaseActivityView {
    void displayMealResults(String mealName, List<SearchMealItemResponseDatum> mealsResponse);

    void displaySucceededMealRequest();

    void displayRecentMeals(ArrayList<RecentMealsResponseBody> recentMealsFromAPI, String mealType);
  }
}
