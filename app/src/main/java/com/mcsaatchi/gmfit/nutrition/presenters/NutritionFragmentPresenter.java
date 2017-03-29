package com.mcsaatchi.gmfit.nutrition.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponseInnerData;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponseInner;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.common.presenters.BaseFragmentPresenter;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NutritionFragmentPresenter extends BaseFragmentPresenter {
  private NutritionFragmentView view;
  private DataAccessHandler dataAccessHandler;

  public NutritionFragmentPresenter(NutritionFragmentView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void getUserGoalMetrics(final String date, final String type) {
    dataAccessHandler.getUserGoalMetrics(date, type, new Callback<UserGoalMetricsResponse>() {
      @Override public void onResponse(Call<UserGoalMetricsResponse> call,
          Response<UserGoalMetricsResponse> response) {

        switch (response.code()) {
          case 200:
            String maxValue =
                response.body().getData().getBody().getMetrics().getCalories().getMaxValue();

            String currentValue =
                response.body().getData().getBody().getMetrics().getCalories().getValue();

            view.displayUserGoalMetrics(maxValue, currentValue);

            break;
        }
      }

      @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
      }
    });
  }

  public void getUserAddedMeals(String desiredDate) {
    dataAccessHandler.getUserAddedMealsOnDate(desiredDate, new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        switch (response.code()) {
          case 200:
            List<UserMealsResponseInner> breakfastMeals =
                response.body().getData().getBody().getData().getBreakfast();
            List<UserMealsResponseInner> lunchMeals =
                response.body().getData().getBody().getData().getLunch();
            List<UserMealsResponseInner> dinnerMeals =
                response.body().getData().getBody().getData().getDinner();
            List<UserMealsResponseInner> snackMeals =
                response.body().getData().getBody().getData().getSnack();

            view.displayUserAddedMeals(breakfastMeals, lunchMeals, dinnerMeals, snackMeals);

            break;
        }
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
        view.showRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void getUiForSection(String section, String desiredDate) {
    String finalUrl;

    if (desiredDate == null) {
      finalUrl = Constants.BASE_URL_ADDRESS + "user/ui?section=" + section;
    } else {
      finalUrl = Constants.BASE_URL_ADDRESS + "user/ui?section=" + section + "&date=" + desiredDate;
    }

    dataAccessHandler.getUiForSection(finalUrl, new Callback<UiResponse>() {
      @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
        switch (response.code()) {
          case 200:
            List<AuthenticationResponseWidget> widgetsMapFromAPI =
                response.body().getData().getBody().getWidgets();
            List<AuthenticationResponseChart> chartsMapFromAPI =
                response.body().getData().getBody().getCharts();

            view.displayUiForSection(widgetsMapFromAPI, chartsMapFromAPI);

            break;
        }
      }

      @Override public void onFailure(Call<UiResponse> call, Throwable t) {
        view.showRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void searchForMealBarcode(String barcode, String mealType) {
    view.callDisplayWaitingDialog(R.string.searching_for_barcode_meal);

    dataAccessHandler.searchForMealBarcode(barcode, new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        switch (response.code()) {
          case 200:

            List<SearchMealItemResponseDatum> mealsResponse =
                response.body().getData().getBody().getData();

            view.displayBarcodeSearchResults(mealsResponse, mealType);

            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
        view.showRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void updateUserWidgets(int[] widgetIds, int[] widgetPositions) {
    dataAccessHandler.updateUserWidgets(widgetIds, widgetPositions,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Timber.d("onResponse: User's widgets updated successfully");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

          }
        });
  }

  public void updateUserCharts(int[] chartIds, int[] chartPositions) {
    dataAccessHandler.updateUserCharts(chartIds, chartPositions,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                Timber.d("onResponse: User's charts updated successfully");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {

          }
        });
  }

  public void deleteUserChart(DataChart chartObject) {
    view.callDisplayWaitingDialog(R.string.deleting_chart_dialog_title);

    dataAccessHandler.deleteUserChart(String.valueOf(chartObject.getChart_id()),
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.setupChartsAfterDeletion(chartObject);

                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
          }
        });
  }

  public void getChartMonthlyBreakdown(DataChart chartObject, LocalDate dt) {
    dataAccessHandler.getPeriodicalChartData(dt.minusMonths(1).toString(), dt.toString(),
        "nutrition", chartObject.getName(), new Callback<ChartMetricBreakdownResponse>() {
          @Override public void onResponse(Call<ChartMetricBreakdownResponse> call,
              Response<ChartMetricBreakdownResponse> response) {
            if (response.body().getData().getBody() != null) {
              List<ChartMetricBreakdownResponseDatum> rawChartData =
                  response.body().getData().getBody().getData();

              if (rawChartData != null && rawChartData.size() > 0) {
                List<AuthenticationResponseChartData> newChartData = new ArrayList<>();

                for (int i = 0; i < rawChartData.size(); i++) {
                  newChartData.add(
                      new AuthenticationResponseChartData(rawChartData.get(i).getDate(),
                          rawChartData.get(i).getValue()));
                }

                view.createNewChartWithData(newChartData, chartObject);
              }
            }
          }

          @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
            view.showRequestErrorDialog(t.getMessage());
          }
        });
  }

  public void getSlugBreakdownForChart(DataChart chartObject) {
    view.callDisplayWaitingDialog(R.string.grabbing_breakdown_data_dialog_title);

    dataAccessHandler.getSlugBreakdownForChart(chartObject.getType(),
        new Callback<SlugBreakdownResponse>() {
          @Override public void onResponse(Call<SlugBreakdownResponse> call,
              Response<SlugBreakdownResponse> response) {
            switch (response.code()) {
              case 200:
                view.openSlugBreakdownActivity(response.body().getData().getBody().getData(),
                    chartObject);
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
            view.showRequestErrorDialog(t.getMessage());
          }
        });
  }

  public interface NutritionFragmentView extends BaseFragmentPresenter.BaseFragmentView {
    void displayUserGoalMetrics(String maxValue, String currentValue);

    void displayUserAddedMeals(List<UserMealsResponseInner> breakfastMeals,
        List<UserMealsResponseInner> lunchMeals, List<UserMealsResponseInner> dinnerMeals,
        List<UserMealsResponseInner> snackMeals);

    void displayUiForSection(List<AuthenticationResponseWidget> widgetsMapFromAPI,
        List<AuthenticationResponseChart> chartsMapFromAPI);

    void displayBarcodeSearchResults(List<SearchMealItemResponseDatum> mealsResponse,
        String mealType);

    void setupChartsAfterDeletion(DataChart chartObject);

    void openSlugBreakdownActivity(SlugBreakdownResponseInnerData breakdownData,
        DataChart chartObject);

    void createNewChartWithData(List<AuthenticationResponseChartData> chartData,
        DataChart chartObject);
  }
}
