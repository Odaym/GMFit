package com.mcsaatchi.gmfit.architecture.retrofit.architecture;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AddCRMNoteResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticleDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMNotesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicDeletionResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CounsellingInformationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CurrenciesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DeleteActivityResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MetaTextsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MostPopularMedicationsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.RecentMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMedicinesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ServicesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UiResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UploadInsuranceImageResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserActivitiesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WeightHistoryResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WidgetsResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallsHandler {

  @Inject RestClient restClient;

  public ApiCallsHandler(Context app) {
    ((GMFitApplication) app).getAppComponent().inject(this);
  }

  void getSlugBreakdownForChart(final String chartType,
      final Callback<SlugBreakdownResponse> callback) {

    Call<SlugBreakdownResponse> apiCall = restClient.getGMFitService()
        .getBreakdownForSlug(
            Constants.BASE_URL_ADDRESS + "user/metrics/breakdown?slug=" + chartType);

    apiCall.enqueue(new Callback<SlugBreakdownResponse>() {
      @Override public void onResponse(Call<SlugBreakdownResponse> call,
          Response<SlugBreakdownResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
      }
    });
  }

  void refreshAccessToken(final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall = restClient.getGMFitService().refreshAccessToken();

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void synchronizeMetricsWithServer(String[] slugsArray, Number[] valuesArray,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .updateMetrics(
            new UpdateMetricsRequest(slugsArray, valuesArray, Helpers.getCalendarDate()));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void signInUser(final String email, final String password,
      final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall =
        restClient.getGMFitService().signInUser(new SignInRequest(email, password));

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void registerUser(final String full_name, final String email, final String password,
      final String phone_number, final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall = restClient.getGMFitService()
        .registerUser(new RegisterRequest(full_name, email, password, phone_number));

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void signInUserSilently(String email, String password,
      final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall =
        restClient.getGMFitService().signInUserSilently(new SignInRequest(email, password));

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void signOutUser(final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService().signOutUser();

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserProfile(RequestBody finalDateOfBirth, RequestBody bloodType,
      RequestBody nationality, HashMap<String, RequestBody> medicalConditions,
      RequestBody measurementSystem, RequestBody goalId, RequestBody activityLevelId,
      RequestBody finalGender, RequestBody height, RequestBody weight, RequestBody onboard,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .updateUserProfile(finalDateOfBirth, bloodType, nationality, measurementSystem,
            medicalConditions, goalId, activityLevelId, finalGender, height, weight, onboard);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserProfileExplicitly(RequestBody name, RequestBody phone_number, RequestBody gender,
      RequestBody date_of_birth, RequestBody blood_type, RequestBody height, RequestBody weight,
      Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .updateUserProfileExplicitly(name, phone_number, gender, date_of_birth, blood_type, height,
            weight);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserWeight(double weight, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().updateUserWeight(new UpdateUserWeightRequest(weight));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateOneSignalToken(String onesignal_id, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().updateOneSignalToken(new UpdateOneSignalRequest(onesignal_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  /**
   * Non-Caching
   */
  void getUiForSection(String section, final Callback<UiResponse> callback) {
    Call<UiResponse> apiCall = restClient.getGMFitService().getUiForSection(section);

    apiCall.enqueue(new Callback<UiResponse>() {
      @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UiResponse> call, Throwable t) {
      }
    });
  }

  void getMedicalConditions(final Callback<MedicalConditionsResponse> callback) {
    Call<MedicalConditionsResponse> apiCall = restClient.getGMFitService().getMedicalConditions();

    apiCall.enqueue(new Callback<MedicalConditionsResponse>() {
      @Override public void onResponse(Call<MedicalConditionsResponse> call,
          Response<MedicalConditionsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {
      }
    });
  }

  void sendResetPasswordLink(String email, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().sendResetPasswordLink(new ForgotPasswordRequest(email));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void changePassword(String old_password, String new_password,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .changePassword(new ChangePasswordRequest(old_password, new_password));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void finalizeResetPassword(String resetPasswordToken, String newPassword,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .finalizeResetPassword(new ResetPasswordRequest(resetPasswordToken, newPassword));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void findMeals(String mealName, final Callback<SearchMealItemResponse> callback) {
    Call<SearchMealItemResponse> apiCall = restClient.getGMFitService().searchForMeals(mealName);

    apiCall.enqueue(new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
      }
    });
  }

  void searchForMealBarcode(String barcode, final Callback<SearchMealItemResponse> callback) {
    Call<SearchMealItemResponse> apiCall =
        restClient.getGMFitService().searchForMealBarcode(barcode);

    apiCall.enqueue(new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
      }
    });
  }

  void getMealMetrics(String fullUrl, final Callback<MealMetricsResponse> callback) {
    Call<MealMetricsResponse> apiCall = restClient.getGMFitService().getMealMetrics(fullUrl);

    apiCall.enqueue(new Callback<MealMetricsResponse>() {
      @Override public void onResponse(Call<MealMetricsResponse> call,
          Response<MealMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
      }
    });
  }

  void verifyUser(String verificationCode, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .verifyRegistrationCode(new VerificationRequest(verificationCode));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserWidgets(int[] widgetIds, int[] widgetPositions,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .updateUserWidgets(new UpdateWidgetsRequest(widgetIds, widgetPositions));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserCharts(int[] chartIds, int[] chartPositions,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .updateUserCharts(new UpdateChartsRequest(chartIds, chartPositions));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getUserAddedMeals(final Callback<UserMealsResponse> callback) {
    Call<UserMealsResponse> apiCall = restClient.getGMFitService().getUserAddedMeals();

    apiCall.enqueue(new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
      }
    });
  }

  void getUserAddedMealsOnDate(String chosenDate, final Callback<UserMealsResponse> callback) {
    Call<UserMealsResponse> apiCall =
        restClient.getGMFitService().getUserAddedMealsOnDate(chosenDate, chosenDate);

    apiCall.enqueue(new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
      }
    });
  }

  void getMetaTexts(String section, final Callback<MetaTextsResponse> callback) {
    Call<MetaTextsResponse> apiCall = restClient.getGMFitService().getMetaTexts(section);

    apiCall.enqueue(new Callback<MetaTextsResponse>() {
      @Override
      public void onResponse(Call<MetaTextsResponse> call, Response<MetaTextsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MetaTextsResponse> call, Throwable t) {
      }
    });
  }

  void handleFacebookProcess(String facebookAccessToken,
      final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall = restClient.getGMFitService()
        .handleFacebookProcess(new HandleFacebookRequest(facebookAccessToken));

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void storeNewMeal(int meal_id, float servingsAmount, String when, String date,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .storeNewMeal(new StoreNewMealRequest(meal_id, servingsAmount, when.toLowerCase(), date));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getRecentMeals(String fullUrl, final Callback<RecentMealsResponse> callback) {
    Call<RecentMealsResponse> apiCall = restClient.getGMFitService().getRecentMeals(fullUrl);

    apiCall.enqueue(new Callback<RecentMealsResponse>() {
      @Override public void onResponse(Call<RecentMealsResponse> call,
          Response<RecentMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<RecentMealsResponse> call, Throwable t) {
      }
    });
  }

  void updateUserMeals(int instance_id, float amount, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().updateUserMeals(new UpdateMealsRequest(instance_id, amount));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getChartsBySection(String chartSection, final Callback<ChartsBySectionResponse> callback) {
    Call<ChartsBySectionResponse> apiCall = restClient.getGMFitService()
        .getChartsBySection(Constants.BASE_URL_ADDRESS + "charts?section=" + chartSection);

    apiCall.enqueue(new Callback<ChartsBySectionResponse>() {
      @Override public void onResponse(Call<ChartsBySectionResponse> call,
          Response<ChartsBySectionResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
      }
    });
  }

  void addMetricChart(int chart_id, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().addMetricChart(new AddMetricChartRequest(chart_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getPeriodicalChartData(String start_date, String end_date, String type,
      String monitored_metric, final Callback<ChartMetricBreakdownResponse> callback) {
    Call<ChartMetricBreakdownResponse> apiCall = restClient.getGMFitService()
        .getPeriodicalChartData(start_date, end_date, type, monitored_metric);

    apiCall.enqueue(new Callback<ChartMetricBreakdownResponse>() {
      @Override public void onResponse(Call<ChartMetricBreakdownResponse> call,
          Response<ChartMetricBreakdownResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
      }
    });
  }

  void getUserGoalMetrics(String date, String type,
      final Callback<UserGoalMetricsResponse> callback) {
    Call<UserGoalMetricsResponse> apiCall =
        restClient.getGMFitService().getUserGoalMetrics(date, type);

    apiCall.enqueue(new Callback<UserGoalMetricsResponse>() {
      @Override public void onResponse(Call<UserGoalMetricsResponse> call,
          Response<UserGoalMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
      }
    });
  }

  void requestNewMeal(String mealName, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().requestNewMeal(new RequestMealRequest(mealName));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getMedicalTests(final Callback<MedicalTestsResponse> callback) {
    Call<MedicalTestsResponse> apiCall = restClient.getGMFitService().getMedicalTests();

    apiCall.enqueue(new Callback<MedicalTestsResponse>() {
      @Override public void onResponse(Call<MedicalTestsResponse> call,
          Response<MedicalTestsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalTestsResponse> call, Throwable t) {
      }
    });
  }

  void getTesticularMetrics(final Callback<MedicalTestMetricsResponse> callback) {
    Call<MedicalTestMetricsResponse> apiCall = restClient.getGMFitService().getTesticularMetrics();

    apiCall.enqueue(new Callback<MedicalTestMetricsResponse>() {
      @Override public void onResponse(Call<MedicalTestMetricsResponse> call,
          Response<MedicalTestMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalTestMetricsResponse> call, Throwable t) {
      }
    });
  }

  void getWidgets(String sectionName, final Callback<WidgetsResponse> callback) {
    Call<WidgetsResponse> apiCall = restClient.getGMFitService().getWidgets(sectionName);

    apiCall.enqueue(new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
      }
    });
  }

  void getWidgetsWithDate(String sectionName, String date,
      final Callback<WidgetsResponse> callback) {
    Call<WidgetsResponse> apiCall =
        restClient.getGMFitService().getWidgetsWithDate(sectionName, date);

    apiCall.enqueue(new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
      }
    });
  }

  void storeNewHealthTest(RequestBody test_name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().storeNewHealthTest(test_name, date_taken, metrics, imageFiles);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void editExistingHealthTest(RequestBody instance_id, RequestBody name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      RequestBody deletedImages, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .editExistingHealthTest(instance_id, name, date_taken, metrics, imageFiles, deletedImages);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getTakenMedicalTests(final Callback<TakenMedicalTestsResponse> callback) {
    Call<TakenMedicalTestsResponse> apiCall = restClient.getGMFitService().getTakenMedicalTests();

    apiCall.enqueue(new Callback<TakenMedicalTestsResponse>() {
      @Override public void onResponse(Call<TakenMedicalTestsResponse> call,
          Response<TakenMedicalTestsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {
      }
    });
  }

  void getOnboardingStatus(final Callback<UserProfileResponse> callback) {
    Call<UserProfileResponse> apiCall = restClient.getGMFitService().getOnboardingStatus();

    apiCall.enqueue(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
      }
    });
  }

  void getUserProfile(final Callback<UserProfileResponse> callback) {
    Call<UserProfileResponse> apiCall = restClient.getGMFitService().getUserProfile();

    apiCall.enqueue(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
      }
    });
  }

  void getOperationContacts(final Callback<OperationContactsResponse> callback) {
    Call<OperationContactsResponse> apiCall = restClient.getGMFitService().getOperationContacts();

    apiCall.enqueue(new Callback<OperationContactsResponse>() {
      @Override public void onResponse(Call<OperationContactsResponse> call,
          Response<OperationContactsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<OperationContactsResponse> call, Throwable t) {
      }
    });
  }

  void addFitnessActivity(String fitness_activity_level_id, String duration, String date,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .addFitnessActivity(
            new AddFitnessActivityRequest(fitness_activity_level_id, duration, date));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void editFitnessActivity(String id, String duration, String date, String level_id,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .editFitnessActivity(new EditFitnessActivityRequest(id, duration, date, level_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void deleteFitnessActivity(int instance_id, final Callback<DeleteActivityResponse> callback) {
    Call<DeleteActivityResponse> apiCall = restClient.getGMFitService()
        .deleteFitnessActivity(new DeleteFitnessActivityRequest(instance_id));

    apiCall.enqueue(new Callback<DeleteActivityResponse>() {
      @Override public void onResponse(Call<DeleteActivityResponse> call,
          Response<DeleteActivityResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DeleteActivityResponse> call, Throwable t) {
      }
    });
  }

  void getArticles(String sectionName, final Callback<ArticlesResponse> callback) {
    Call<ArticlesResponse> apiCall = restClient.getGMFitService().getArticles(sectionName);

    apiCall.enqueue(new Callback<ArticlesResponse>() {
      @Override
      public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ArticlesResponse> call, Throwable t) {
      }
    });
  }

  void getArticleDetails(String fullUrl, final Callback<ArticleDetailsResponse> callback) {
    Call<ArticleDetailsResponse> apiCall = restClient.getGMFitService().getArticleDetails(fullUrl);

    apiCall.enqueue(new Callback<ArticleDetailsResponse>() {
      @Override public void onResponse(Call<ArticleDetailsResponse> call,
          Response<ArticleDetailsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ArticleDetailsResponse> call, Throwable t) {
      }
    });
  }

  void getUserAchievements(final Callback<AchievementsResponse> callback) {
    Call<AchievementsResponse> apiCall = restClient.getGMFitService().getUserAchievements();

    apiCall.enqueue(new Callback<AchievementsResponse>() {
      @Override public void onResponse(Call<AchievementsResponse> call,
          Response<AchievementsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AchievementsResponse> call, Throwable t) {
      }
    });
  }

  void getAllActivities(final Callback<ActivitiesListResponse> callback) {
    Call<ActivitiesListResponse> apiCall = restClient.getGMFitService().getAllActivities();

    apiCall.enqueue(new Callback<ActivitiesListResponse>() {
      @Override public void onResponse(Call<ActivitiesListResponse> call,
          Response<ActivitiesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ActivitiesListResponse> call, Throwable t) {
      }
    });
  }

  void getUserActivities(String date, final Callback<UserActivitiesResponse> callback) {
    Call<UserActivitiesResponse> apiCall = restClient.getGMFitService().getUserActivities(date);

    apiCall.enqueue(new Callback<UserActivitiesResponse>() {
      @Override public void onResponse(Call<UserActivitiesResponse> call,
          Response<UserActivitiesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserActivitiesResponse> call, Throwable t) {
      }
    });
  }

  void deleteUserChart(String chart_id, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().deleteUserChart(new DeleteUserChartRequest(chart_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void deleteUserMeal(int instance_id, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().deleteUserMeal(new DeleteMealRequest(instance_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void deleteUserTest(int instance_id, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().deleteUserTest(new DeleteTestRequest(instance_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserPicture(Map<String, RequestBody> profilePicture,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        restClient.getGMFitService().updateUserPicture(profilePicture);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getActivityLevels(final Callback<ActivityLevelsResponse> callback) {
    Call<ActivityLevelsResponse> apiCall = restClient.getGMFitService().getActivityLevels();

    apiCall.enqueue(new Callback<ActivityLevelsResponse>() {
      @Override public void onResponse(Call<ActivityLevelsResponse> call,
          Response<ActivityLevelsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ActivityLevelsResponse> call, Throwable t) {
      }
    });
  }

  void getUserWeightHistory(final Callback<WeightHistoryResponse> callback) {
    Call<WeightHistoryResponse> apiCall = restClient.getGMFitService().getUserWeightHistory();

    apiCall.enqueue(new Callback<WeightHistoryResponse>() {
      @Override public void onResponse(Call<WeightHistoryResponse> call,
          Response<WeightHistoryResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<WeightHistoryResponse> call, Throwable t) {
      }
    });
  }

  void getUserGoals(final Callback<UserGoalsResponse> callback) {
    Call<UserGoalsResponse> apiCall = restClient.getGMFitService().getUserGoals();

    apiCall.enqueue(new Callback<UserGoalsResponse>() {
      @Override
      public void onResponse(Call<UserGoalsResponse> call, Response<UserGoalsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserGoalsResponse> call, Throwable t) {
      }
    });
  }

  /**
   * INSURANCE API's
   */

  void searchMedicines(String key, final Callback<SearchMedicinesResponse> callback) {
    Call<SearchMedicinesResponse> apiCall =
        restClient.getGMFitService().searchMedicines(new SearchMedicinesRequest(key));

    apiCall.enqueue(new Callback<SearchMedicinesResponse>() {
      @Override public void onResponse(Call<SearchMedicinesResponse> call,
          Response<SearchMedicinesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SearchMedicinesResponse> call, Throwable t) {
      }
    });
  }

  void getMostPopularMedications(String indNbr, String contractNo, String country, String language,
      String password, final Callback<MostPopularMedicationsResponse> callback) {
    Call<MostPopularMedicationsResponse> apiCall = restClient.getGMFitService()
        .getMostPopularMedications(
            new DefaultBodyForInsuranceRequests(indNbr, contractNo, country, language, password));

    apiCall.enqueue(new Callback<MostPopularMedicationsResponse>() {
      @Override public void onResponse(Call<MostPopularMedicationsResponse> call,
          Response<MostPopularMedicationsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MostPopularMedicationsResponse> call, Throwable t) {
      }
    });
  }

  void insuranceUserLogin(String indNbr, String country, String language, String password,
      final Callback<InsuranceLoginResponse> callback) {
    Call<InsuranceLoginResponse> apiCall = restClient.getGMFitService()
        .insuranceUserLogin(new InsuranceLoginRequest(indNbr, country, language, password));

    apiCall.enqueue(new Callback<InsuranceLoginResponse>() {
      @Override public void onResponse(Call<InsuranceLoginResponse> call,
          Response<InsuranceLoginResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
      }
    });
  }

  void getCoverageDescription(String contractNo, String indNbr,
      final Callback<ResponseBody> callback) {
    Call<ResponseBody> apiCall = restClient.getGMFitService()
        .getCoverageDescription(new CoverageDescriptionRequest(contractNo, indNbr));

    apiCall.enqueue(new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
      }
    });
  }

  void getMembersGuide(String contractNo, String indNbr, final Callback<ResponseBody> callback) {
    Call<ResponseBody> apiCall = restClient.getGMFitService()
        .getMembersGuide(new CoverageDescriptionRequest(contractNo, indNbr));

    apiCall.enqueue(new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
      }
    });
  }

  void updateInsurancePassword(String contractNo, String oldPassword, String newPassword,
      String email, String mobileNumber, final Callback<UpdateInsurancePasswordResponse> callback) {
    Call<UpdateInsurancePasswordResponse> apiCall = restClient.getGMFitService()
        .updateInsurancePassword(
            new UpdateInsurancePasswordRequest(contractNo, oldPassword, newPassword, email,
                mobileNumber));

    apiCall.enqueue(new Callback<UpdateInsurancePasswordResponse>() {
      @Override public void onResponse(Call<UpdateInsurancePasswordResponse> call,
          Response<UpdateInsurancePasswordResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UpdateInsurancePasswordResponse> call, Throwable t) {
      }
    });
  }

  void getCardDetails(String contractNo, final Callback<ResponseBody> callback) {
    Call<ResponseBody> apiCall =
        restClient.getGMFitService().getCardDetails(new SimpleInsuranceRequest(contractNo));

    apiCall.enqueue(new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
      }
    });
  }

  void getSubCategories(String contractNo, final Callback<SubCategoriesResponse> callback) {
    Call<SubCategoriesResponse> apiCall =
        restClient.getGMFitService().getSubCategories(new SimpleInsuranceRequest(contractNo));

    apiCall.enqueue(new Callback<SubCategoriesResponse>() {
      @Override public void onResponse(Call<SubCategoriesResponse> call,
          Response<SubCategoriesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SubCategoriesResponse> call, Throwable t) {
      }
    });
  }

  void uploadInsuranceImage(Map<String, RequestBody> file,
      final Callback<UploadInsuranceImageResponse> callback) {
    Call<UploadInsuranceImageResponse> apiCall =
        restClient.getGMFitService().uploadInsuranceImage(file);

    apiCall.enqueue(new Callback<UploadInsuranceImageResponse>() {
      @Override public void onResponse(Call<UploadInsuranceImageResponse> call,
          Response<UploadInsuranceImageResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UploadInsuranceImageResponse> call, Throwable t) {
      }
    });
  }

  void getNearbyClinics(String contractNo, String providerTypesCode, int searchCtry,
      double longitude, double latitude, int fetchClosest,
      final Callback<GetNearbyClinicsResponse> callback) {
    Call<GetNearbyClinicsResponse> apiCall = restClient.getGMFitService()
        .getNearbyClinics(
            new NearbyClinicsRequest(contractNo, providerTypesCode, searchCtry, longitude, latitude,
                fetchClosest));

    apiCall.enqueue(new Callback<GetNearbyClinicsResponse>() {
      @Override public void onResponse(Call<GetNearbyClinicsResponse> call,
          Response<GetNearbyClinicsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<GetNearbyClinicsResponse> call, Throwable t) {
      }
    });
  }

  void applySearchFilters(String contractNo, int searchCtry, int searchCity,
      String providerTypesCode, int fetchClosest,
      final Callback<GetNearbyClinicsResponse> callback) {

    Call<GetNearbyClinicsResponse> apiCall;

    if (searchCity == 0) {
      apiCall = restClient.getGMFitService()
          .applySearchFilters(
              new ApplySearchFiltersRequestWithoutCity(contractNo, searchCtry, providerTypesCode,
                  fetchClosest));
    } else {
      apiCall = restClient.getGMFitService()
          .applySearchFilters(
              new ApplySearchFiltersRequest(contractNo, searchCtry, searchCity, providerTypesCode,
                  fetchClosest));
    }

    if (apiCall != null) {
      apiCall.enqueue(new Callback<GetNearbyClinicsResponse>() {
        @Override public void onResponse(Call<GetNearbyClinicsResponse> call,
            Response<GetNearbyClinicsResponse> response) {
          callback.onResponse(call, response);
        }

        @Override public void onFailure(Call<GetNearbyClinicsResponse> call, Throwable t) {
        }
      });
    }
  }

  void sendInsurancePasswordResetLink(String email, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = restClient.getGMFitService()
        .sendInsurancePasswordResetLink(new ForgotPasswordRequest(email));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void createNewRequest(RequestBody contractNo, RequestBody categ, RequestBody subCategId,
      RequestBody requestTypeId, RequestBody claimedAmount, RequestBody currencyCode,
      RequestBody serviceDate, RequestBody providerCode, RequestBody remarks,
      Map<String, RequestBody> attachements, final Callback<CreateNewRequestResponse> callback) {
    Call<CreateNewRequestResponse> apiCall = restClient.getGMFitService()
        .createNewRequest(contractNo, categ, subCategId, requestTypeId, claimedAmount, currencyCode,
            serviceDate, providerCode, remarks, attachements);

    apiCall.enqueue(new Callback<CreateNewRequestResponse>() {
      @Override public void onResponse(Call<CreateNewRequestResponse> call,
          Response<CreateNewRequestResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
      }
    });
  }

  void createNewInquiryComplaint(RequestBody contractNo, RequestBody crm_country,
      RequestBody category, RequestBody subcategory, RequestBody area, RequestBody title,
      RequestBody description, RequestBody path,
      final Callback<CreateNewRequestResponse> callback) {
    Call<CreateNewRequestResponse> apiCall = restClient.getGMFitService()
        .createNewInquiryComplaint(contractNo, crm_country, category, subcategory, area, title,
            description, path);

    apiCall.enqueue(new Callback<CreateNewRequestResponse>() {
      @Override public void onResponse(Call<CreateNewRequestResponse> call,
          Response<CreateNewRequestResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
      }
    });
  }

  void createNewInquiryComplaintWithoutImage(RequestBody contractNo, RequestBody crm_country,
      RequestBody category, RequestBody subcategory, RequestBody area, RequestBody title,
      RequestBody description, final Callback<CreateNewRequestResponse> callback) {
    Call<CreateNewRequestResponse> apiCall = restClient.getGMFitService()
        .createNewInquiryComplaintWithoutImage(contractNo, crm_country, category, subcategory, area,
            title, description);

    apiCall.enqueue(new Callback<CreateNewRequestResponse>() {
      @Override public void onResponse(Call<CreateNewRequestResponse> call,
          Response<CreateNewRequestResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
      }
    });
  }

  void getCountriesList(final Callback<CountriesListResponse> callback) {
    Call<CountriesListResponse> apiCall = restClient.getGMFitService().getCountriesList();

    apiCall.enqueue(new Callback<CountriesListResponse>() {
      @Override public void onResponse(Call<CountriesListResponse> call,
          Response<CountriesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CountriesListResponse> call, Throwable t) {
      }
    });
  }

  void getCitiesList(String selectedCtry, final Callback<CitiesListResponse> callback) {
    Call<CitiesListResponse> apiCall =
        restClient.getGMFitService().getCitiesList(new GetCitiesListRequest(selectedCtry));

    apiCall.enqueue(new Callback<CitiesListResponse>() {
      @Override
      public void onResponse(Call<CitiesListResponse> call, Response<CitiesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CitiesListResponse> call, Throwable t) {
      }
    });
  }

  void getServicesList(final Callback<ServicesListResponse> callback) {
    Call<ServicesListResponse> apiCall = restClient.getGMFitService().getServicesList();

    apiCall.enqueue(new Callback<ServicesListResponse>() {
      @Override public void onResponse(Call<ServicesListResponse> call,
          Response<ServicesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ServicesListResponse> call, Throwable t) {
      }
    });
  }

  void getCurrenciesList(final Callback<CurrenciesListResponse> callback) {
    Call<CurrenciesListResponse> apiCall = restClient.getGMFitService().getCurrenciesList();

    apiCall.enqueue(new Callback<CurrenciesListResponse>() {
      @Override public void onResponse(Call<CurrenciesListResponse> call,
          Response<CurrenciesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CurrenciesListResponse> call, Throwable t) {
      }
    });
  }

  void getClaimslist(String contractNo, String requestType,
      final Callback<ClaimsListResponse> callback) {
    Call<ClaimsListResponse> apiCall =
        restClient.getGMFitService().getClaimsList(new ClaimsListRequest(contractNo, requestType));

    apiCall.enqueue(new Callback<ClaimsListResponse>() {
      @Override
      public void onResponse(Call<ClaimsListResponse> call, Response<ClaimsListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ClaimsListResponse> call, Throwable t) {
      }
    });
  }

  void getClaimslistDetails(String contractNo, String requestType, String claimId,
      final Callback<ClaimListDetailsResponse> callback) {
    Call<ClaimListDetailsResponse> apiCall = restClient.getGMFitService()
        .getClaimsListDetails(new ClaimsListDetailsRequest(contractNo, requestType, claimId));

    apiCall.enqueue(new Callback<ClaimListDetailsResponse>() {
      @Override public void onResponse(Call<ClaimListDetailsResponse> call,
          Response<ClaimListDetailsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ClaimListDetailsResponse> call, Throwable t) {
      }
    });
  }

  void getChronicTreatmentsList(String contractNo, String requestType,
      final Callback<ChronicTreatmentListResponse> callback) {
    Call<ChronicTreatmentListResponse> apiCall = restClient.getGMFitService()
        .getChronicTreatmentsList(new ClaimsListRequest(contractNo, requestType));

    apiCall.enqueue(new Callback<ChronicTreatmentListResponse>() {
      @Override public void onResponse(Call<ChronicTreatmentListResponse> call,
          Response<ChronicTreatmentListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChronicTreatmentListResponse> call, Throwable t) {
      }
    });
  }

  void deleteChronicTreatment(String contractNo, String requestID, String requestType,
      final Callback<ChronicDeletionResponse> callback) {
    Call<ChronicDeletionResponse> apiCall = restClient.getGMFitService()
        .deleteChronicTreatment(new ChronicDeletionRequest(contractNo, requestID, requestType));

    apiCall.enqueue(new Callback<ChronicDeletionResponse>() {
      @Override public void onResponse(Call<ChronicDeletionResponse> call,
          Response<ChronicDeletionResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChronicDeletionResponse> call, Throwable t) {
      }
    });
  }

  void getChronicTreatmentDetails(String contractNo, String requestType, String claimId,
      final Callback<ChronicTreatmentDetailsResponse> callback) {
    Call<ChronicTreatmentDetailsResponse> apiCall = restClient.getGMFitService()
        .getChronicTreatmentDetails(new ClaimsListDetailsRequest(contractNo, requestType, claimId));

    apiCall.enqueue(new Callback<ChronicTreatmentDetailsResponse>() {
      @Override public void onResponse(Call<ChronicTreatmentDetailsResponse> call,
          Response<ChronicTreatmentDetailsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChronicTreatmentDetailsResponse> call, Throwable t) {
      }
    });
  }

  void getCRMCategories(RequestBody contractNo, RequestBody dbCountry,
      final Callback<CRMCategoriesResponse> callback) {
    Call<CRMCategoriesResponse> apiCall =
        restClient.getGMFitService().getCRMCategories(contractNo, dbCountry);

    apiCall.enqueue(new Callback<CRMCategoriesResponse>() {
      @Override public void onResponse(Call<CRMCategoriesResponse> call,
          Response<CRMCategoriesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CRMCategoriesResponse> call, Throwable t) {
      }
    });
  }

  void getSnapshot(String contractNo, String period, final Callback<ResponseBody> callback) {
    Call<ResponseBody> apiCall =
        restClient.getGMFitService().getSnapshot(new SnapShotRequest(contractNo, period));

    apiCall.enqueue(new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
      }
    });
  }

  void getInquiriesList(String incidentId, String crm_country,
      final Callback<InquiriesListResponse> callback) {
    Call<InquiriesListResponse> apiCall = restClient.getGMFitService()
        .getInquiriesList(new InquiriesListRequest(incidentId, crm_country));

    apiCall.enqueue(new Callback<InquiriesListResponse>() {
      @Override public void onResponse(Call<InquiriesListResponse> call,
          Response<InquiriesListResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<InquiriesListResponse> call, Throwable t) {
      }
    });
  }

  void getCounsellingInformation(String medCode,
      final Callback<CounsellingInformationResponse> callback) {
    Call<CounsellingInformationResponse> apiCall = restClient.getGMFitService()
        .getCounsellingInformation(new CounsellingInformationRequest(medCode));

    apiCall.enqueue(new Callback<CounsellingInformationResponse>() {
      @Override public void onResponse(Call<CounsellingInformationResponse> call,
          Response<CounsellingInformationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CounsellingInformationResponse> call, Throwable t) {
      }
    });
  }

  void getCRMIncidentNotes(String incidentId, String dbCountry,
      final Callback<CRMNotesResponse> callback) {
    Call<CRMNotesResponse> apiCall = restClient.getGMFitService()
        .getCRMIncidentNotes(new CRMNotesRequest(incidentId, dbCountry));

    apiCall.enqueue(new Callback<CRMNotesResponse>() {
      @Override
      public void onResponse(Call<CRMNotesResponse> call, Response<CRMNotesResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<CRMNotesResponse> call, Throwable t) {
      }
    });
  }

  void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody, String dbCountry,
      final Callback<AddCRMNoteResponse> callback) {
    Call<AddCRMNoteResponse> apiCall = restClient.getGMFitService()
        .addCRMNote(
            new AddCRMNoteRequest(incidentId, subject, noteText, mimeType, fileName, documentBody,
                dbCountry));

    apiCall.enqueue(new Callback<AddCRMNoteResponse>() {
      @Override
      public void onResponse(Call<AddCRMNoteResponse> call, Response<AddCRMNoteResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AddCRMNoteResponse> call, Throwable t) {
      }
    });
  }

  public class UpdateMetricsRequest {
    final String[] slug;
    final Number[] value;
    final String date;

    UpdateMetricsRequest(String[] slug, Number[] value, String date) {
      this.slug = slug;
      this.value = value;
      this.date = date;
    }
  }

  public class SignInRequest {
    final String email;
    final String password;

    SignInRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }
  }

  public class RegisterRequest {
    final String name;
    final String email;
    final String password;
    final String phone_number;

    RegisterRequest(String name, String email, String password, String phone_number) {
      this.name = name;
      this.email = email;
      this.password = password;
      this.phone_number = phone_number;
    }
  }

  public class UpdateProfileRequest {
    final String date_of_birth;
    final String blood_type;
    final String country;
    final String metric_system;
    final Map<String, RequestBody> medical_conditions;
    final int user_goal;
    final int activity_level;
    final int gender;
    final double height;
    final double weight;
    final String onboard;

    UpdateProfileRequest(String date_of_birth, String blood_type, String country,
        Map<String, RequestBody> medical_conditions, String metric_system, int user_goal,
        int activity_level, int gender, double height, double weight, String onboard) {
      this.date_of_birth = date_of_birth;
      this.blood_type = blood_type;
      this.country = country;
      this.metric_system = metric_system;
      this.medical_conditions = medical_conditions;
      this.user_goal = user_goal;
      this.activity_level = activity_level;
      this.gender = gender;
      this.height = height;
      this.weight = weight;
      this.onboard = onboard;
    }
  }

  public class ForgotPasswordRequest {
    final String email;

    ForgotPasswordRequest(String email) {
      this.email = email;
    }
  }

  public class ResetPasswordRequest {
    final String password;
    final String token;

    ResetPasswordRequest(String token, String password) {
      this.token = token;
      this.password = password;
    }
  }

  public class VerificationRequest {
    final String code;

    VerificationRequest(String code) {
      this.code = code;
    }
  }

  public class UpdateWidgetsRequest {
    final int[] widgets;
    final int[] positions;

    UpdateWidgetsRequest(int[] widgets, int[] positions) {
      this.widgets = widgets;
      this.positions = positions;
    }
  }

  public class UpdateChartsRequest {
    final int[] charts;
    final int[] positions;

    UpdateChartsRequest(int[] charts, int[] positions) {
      this.charts = charts;
      this.positions = positions;
    }
  }

  public class HandleFacebookRequest {
    final String access_token;

    HandleFacebookRequest(String access_token) {
      this.access_token = access_token;
    }
  }

  public class StoreNewMealRequest {
    final int meal_id;
    final float amount;
    final String when;
    final String date;

    StoreNewMealRequest(int meal_id, float amount, String when, String date) {
      this.meal_id = meal_id;
      this.amount = amount;
      this.when = when;
      this.date = date;
    }
  }

  public class UpdateMealsRequest {
    final int instance_id;
    final float amount;

    UpdateMealsRequest(int instance_id, float amount) {
      this.instance_id = instance_id;
      this.amount = amount;
    }
  }

  public class AddMetricChartRequest {
    final int chart_id;

    AddMetricChartRequest(int chart_id) {
      this.chart_id = chart_id;
    }
  }

  public class UpdateUserWeightRequest {
    final double weight;

    public UpdateUserWeightRequest(double weight) {
      this.weight = weight;
    }
  }

  public class RequestMealRequest {
    final String name;

    RequestMealRequest(String name) {
      this.name = name;
    }
  }

  public class DeleteUserChartRequest {
    final String chart_id;

    DeleteUserChartRequest(String chart_id) {
      this.chart_id = chart_id;
    }
  }

  public class DeleteTestRequest {
    final int test_id;

    DeleteTestRequest(int test_id) {
      this.test_id = test_id;
    }
  }

  public class DeleteMealRequest {
    final int instance_id;

    DeleteMealRequest(int instance_id) {
      this.instance_id = instance_id;
    }
  }

  public class ChangePasswordRequest {
    final String old_password;
    final String new_password;
    final String confirm_new_password;

    ChangePasswordRequest(String old_password, String new_password) {
      this.old_password = old_password;
      this.new_password = new_password;
      this.confirm_new_password = new_password;
    }
  }

  public class InsuranceLoginRequest {
    String indNbr;
    String country;
    String language;
    String password;

    InsuranceLoginRequest(String indNbr, String country, String language, String password) {
      this.indNbr = indNbr;
      this.country = country;
      this.language = language;
      this.password = password;
    }
  }

  public class DefaultBodyForInsuranceRequests {
    String indNbr;
    String contractNo;
    String country;
    String language;
    String password;

    DefaultBodyForInsuranceRequests(String indNbr, String contractNo, String country,
        String language, String password) {
      this.indNbr = indNbr;
      this.contractNo = contractNo;
      this.country = country;
      this.language = language;
      this.password = password;
    }
  }

  public class SimpleInsuranceRequest {
    String contractNo;

    SimpleInsuranceRequest(String contractNo) {
      this.contractNo = contractNo;
    }
  }

  public class SearchMedicinesRequest {
    String key;

    SearchMedicinesRequest(String key) {
      this.key = key;
    }
  }

  public class UpdateOneSignalRequest {
    private String onesignal_id;

    UpdateOneSignalRequest(String onesignal_id) {
      this.onesignal_id = onesignal_id;
    }
  }

  public class UpdateInsurancePasswordRequest {
    String contractNo;
    String oldPswrd;
    String newPswrd;
    String email;
    String mobileNo;

    UpdateInsurancePasswordRequest(String contractNo, String oldPswrd, String newPswrd,
        String email, String mobileNo) {
      this.contractNo = contractNo;
      this.oldPswrd = oldPswrd;
      this.newPswrd = newPswrd;
      this.email = email;
      this.mobileNo = mobileNo;
    }
  }

  public class NearbyClinicsRequest {
    String contractNo;
    String providerTypesCode;
    int searchCtry;
    double longitude;
    double latitude;
    int fetchClosest;

    NearbyClinicsRequest(String contractNo, String providerTypesCode, int searchCtry,
        double longitude, double latitude, int fetchClosest) {
      this.contractNo = contractNo;
      this.providerTypesCode = providerTypesCode;
      this.searchCtry = searchCtry;
      this.longitude = longitude;
      this.latitude = latitude;
      this.fetchClosest = fetchClosest;
    }
  }

  public class ApplySearchFiltersRequest {
    String contractNo;
    int searchCtry;
    int searchCity;
    String providerTypesCode;
    int fetchClosest;

    public ApplySearchFiltersRequest(String contractNo, int searchCtry, int searchCity,
        String providerTypesCode, int fetchClosest) {
      this.contractNo = contractNo;
      this.searchCtry = searchCtry;
      this.searchCity = searchCity;
      this.providerTypesCode = providerTypesCode;
      this.fetchClosest = fetchClosest;
    }
  }

  public class ApplySearchFiltersRequestWithoutCity {
    String contractNo;
    int searchCtry;
    String providerTypesCode;
    int fetchClosest;

    public ApplySearchFiltersRequestWithoutCity(String contractNo, int searchCtry,
        String providerTypesCode, int fetchClosest) {
      this.contractNo = contractNo;
      this.searchCtry = searchCtry;
      this.providerTypesCode = providerTypesCode;
      this.fetchClosest = fetchClosest;
    }
  }

  public class CoverageDescriptionRequest {
    String contractNo;
    String identifyReport;

    CoverageDescriptionRequest(String contractNo, String identifyReport) {
      this.contractNo = contractNo;
      this.identifyReport = identifyReport;
    }
  }

  public class ClaimsListRequest {
    String contractNo;
    String requestType;

    ClaimsListRequest(String contractNo, String requestType) {
      this.contractNo = contractNo;
      this.requestType = requestType;
    }
  }

  public class ClaimsListDetailsRequest {
    String contractNo;
    String requestType;
    String claimId;

    ClaimsListDetailsRequest(String contractNo, String requestType, String claimId) {
      this.contractNo = contractNo;
      this.requestType = requestType;
      this.claimId = claimId;
    }
  }

  public class SnapShotRequest {
    String contractNo;
    String period;

    SnapShotRequest(String contractNo, String period) {
      this.contractNo = contractNo;
      this.period = period;
    }
  }

  public class InquiriesListRequest {
    private String incidentId;
    private String crm_country;

    InquiriesListRequest(String incidentId, String crm_country) {
      this.incidentId = incidentId;
      this.crm_country = crm_country;
    }
  }

  public class CRMNotesRequest {
    private String incidentId;
    private String dbCountry;

    CRMNotesRequest(String incidentId, String dbCountry) {
      this.incidentId = incidentId;
      this.dbCountry = dbCountry;
    }
  }

  public class AddCRMNoteRequest {
    String incidentId;
    String subject;
    String noteText;
    String mimeType;
    String fileName;
    String documentBody;
    String dbCountry;

    AddCRMNoteRequest(String incidentId, String subject, String noteText, String mimeType,
        String fileName, String documentBody, String dbCountry) {
      this.incidentId = incidentId;
      this.subject = subject;
      this.noteText = noteText;
      this.mimeType = mimeType;
      this.fileName = fileName;
      this.documentBody = documentBody;
      this.dbCountry = dbCountry;
    }
  }

  public class AddFitnessActivityRequest {
    String fitness_activity_level_id;
    String duration;
    String date;

    public AddFitnessActivityRequest(String fitness_activity_level_id, String duration,
        String date) {
      this.fitness_activity_level_id = fitness_activity_level_id;
      this.duration = duration;
      this.date = date;
    }
  }

  public class EditFitnessActivityRequest {
    String id;
    String duration;
    String date;
    String level_id;

    public EditFitnessActivityRequest(String id, String duration, String date, String level_id) {
      this.id = id;
      this.duration = duration;
      this.date = date;
      this.level_id = level_id;
    }
  }

  public class ChronicDeletionRequest {
    String contractNo;
    String requestID;
    String requestType;

    public ChronicDeletionRequest(String contractNo, String requestID, String requestType) {
      this.contractNo = contractNo;
      this.requestID = requestID;
      this.requestType = requestType;
    }
  }

  public class GetCitiesListRequest {
    String selectedCtry;

    public GetCitiesListRequest(String selectedCtry) {
      this.selectedCtry = selectedCtry;
    }
  }

  public class DeleteFitnessActivityRequest {
    int instance_id;

    public DeleteFitnessActivityRequest(int instance_id) {
      this.instance_id = instance_id;
    }
  }

  public class CounsellingInformationRequest {
    String medCode;

    public CounsellingInformationRequest(String medCode) {
      this.medCode = medCode;
    }
  }
}
