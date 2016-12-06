package com.mcsaatchi.gmfit.data_access;

import android.content.Context;
import android.content.res.Resources;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.GMFit_Application;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.MetaTextsResponse;
import com.mcsaatchi.gmfit.rest.RecentMealsResponse;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.UiResponse;
import com.mcsaatchi.gmfit.rest.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.rest.UserGoalsResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.rest.WidgetsResponse;
import java.util.Map;
import javax.inject.Inject;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCallsHandler {

  @Inject Resources activityResources;

  public ApiCallsHandler(Context app) {
    ((GMFit_Application) app).getAppComponent().inject(this);
  }

  void getSlugBreakdownForChart(final String chartType, String userAccessToken,
      final Callback<SlugBreakdownResponse> callback) {

    Call<SlugBreakdownResponse> apiCall =
        new RestClient().getGMFitService().getBreakdownForSlug(Constants.BASE_URL_ADDRESS +
            "user/metrics/breakdown?slug=" + chartType, userAccessToken);

    apiCall.enqueue(new Callback<SlugBreakdownResponse>() {
      @Override public void onResponse(Call<SlugBreakdownResponse> call,
          Response<SlugBreakdownResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SlugBreakdownResponse> call, Throwable t) {
      }
    });
  }

  void refreshAccessToken(String userAccessToken, final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall =
        new RestClient().getGMFitService().refreshAccessToken(userAccessToken);

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void synchronizeMetricsWithServer(String userAccessToken, String[] slugsArray,
      Number[] valuesArray) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .updateMetrics(userAccessToken,
            new UpdateMetricsRequest(slugsArray, valuesArray, Helpers.getCalendarDate()));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void signInUser(final String email, final String password,
      final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall =
        new RestClient().getGMFitService().signInUser(new SignInRequest(email, password));

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
      final Callback<AuthenticationResponse> callback) {
    Call<AuthenticationResponse> apiCall = new RestClient().getGMFitService()
        .registerUser(new RegisterRequest(full_name, email, password));

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
        new RestClient().getGMFitService().signInUserSilently(new SignInRequest(email, password));

    apiCall.enqueue(new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
      }
    });
  }

  void signOutUser(String userAccessToken, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        new RestClient().getGMFitService().signOutUser(userAccessToken);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserProfile(String userAccessToken, String finalDateOfBirth, String bloodType,
      String nationality, int medical_condition, String measurementSystem, int goalId,
      int activityLevelId, int finalGender, double height, double weight, double BMI,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .updateUserProfile(userAccessToken,
            new UpdateProfileRequest(finalDateOfBirth, bloodType, nationality, medical_condition,
                measurementSystem, goalId, activityLevelId, finalGender, height, weight, BMI));

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
  void getUiForSection(String userAccessToken, String section,
      final Callback<UiResponse> callback) {
    Call<UiResponse> apiCall =
        new RestClient().getGMFitService().getUiForSection(userAccessToken, section);

    apiCall.enqueue(new Callback<UiResponse>() {
      @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UiResponse> call, Throwable t) {
      }
    });
  }

  void getMedicalConditions(String userAccessToken,
      final Callback<MedicalConditionsResponse> callback) {
    Call<MedicalConditionsResponse> apiCall =
        new RestClient().getGMFitService().getMedicalConditions(userAccessToken);

    apiCall.enqueue(new Callback<MedicalConditionsResponse>() {
      @Override public void onResponse(Call<MedicalConditionsResponse> call,
          Response<MedicalConditionsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {
      }
    });
  }

  void sendResetPasswordLink(String userAccessToken, String email,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .sendResetPasswordLink(userAccessToken, new ForgotPasswordRequest(email));

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
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
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

  void findMeals(String userAccessToken, String mealName,
      final Callback<SearchMealItemResponse> callback) {
    Call<SearchMealItemResponse> apiCall =
        new RestClient().getGMFitService().searchForMeals(userAccessToken, mealName);

    apiCall.enqueue(new Callback<SearchMealItemResponse>() {
      @Override public void onResponse(Call<SearchMealItemResponse> call,
          Response<SearchMealItemResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {
      }
    });
  }

  void getMealMetrics(String userAccessToken, String fullUrl,
      final Callback<MealMetricsResponse> callback) {
    Call<MealMetricsResponse> apiCall =
        new RestClient().getGMFitService().getMealMetrics(userAccessToken, fullUrl);

    apiCall.enqueue(new Callback<MealMetricsResponse>() {
      @Override public void onResponse(Call<MealMetricsResponse> call,
          Response<MealMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MealMetricsResponse> call, Throwable t) {
      }
    });
  }

  void verifyUser(String userAccessToken, String verificationCode,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .verifyRegistrationCode(userAccessToken, new VerificationRequest(verificationCode));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserWidgets(String userAccessToken, int[] widgetIds, int[] widgetPositions,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .updateUserWidgets(userAccessToken, new UpdateWidgetsRequest(widgetIds, widgetPositions));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserCharts(String userAccessToken, int[] chartIds, int[] chartPositions,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .updateUserCharts(userAccessToken, new UpdateChartsRequest(chartIds, chartPositions));

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
  void getUserAddedMeals(String userAccessToken, final Callback<UserMealsResponse> callback) {
    Call<UserMealsResponse> apiCall =
        new RestClient().getGMFitService().getUserAddedMeals(userAccessToken);

    apiCall.enqueue(new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
      }
    });
  }

  void getUserAddedMealsOnDate(String userAccessToken, String chosenDate,
      final Callback<UserMealsResponse> callback) {
    Call<UserMealsResponse> apiCall = new RestClient().getGMFitService()
        .getUserAddedMealsOnDate(userAccessToken, chosenDate, chosenDate);

    apiCall.enqueue(new Callback<UserMealsResponse>() {
      @Override
      public void onResponse(Call<UserMealsResponse> call, Response<UserMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserMealsResponse> call, Throwable t) {
      }
    });
  }

  void getMetaTexts(String userAccessToken, String section,
      final Callback<MetaTextsResponse> callback) {
    Call<MetaTextsResponse> apiCall =
        new RestClient().getGMFitService().getMetaTexts(userAccessToken, section);

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
    Call<AuthenticationResponse> apiCall = new RestClient().getGMFitService()
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

  void storeNewMeal(String userAccessToken, int meal_id, int servingsAmount, String when,
      String date, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .storeNewMeal(userAccessToken,
            new StoreNewMealRequest(meal_id, servingsAmount, when.toLowerCase(), date));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getRecentMeals(String userAccessToken, String fullUrl,
      final Callback<RecentMealsResponse> callback) {
    Call<RecentMealsResponse> apiCall =
        new RestClient().getGMFitService().getRecentMeals(userAccessToken, fullUrl);

    apiCall.enqueue(new Callback<RecentMealsResponse>() {
      @Override public void onResponse(Call<RecentMealsResponse> call,
          Response<RecentMealsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<RecentMealsResponse> call, Throwable t) {
      }
    });
  }

  void updateUserMeals(String userAccessToken, int instance_id, int amount,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .updateUserMeals(userAccessToken, new UpdateMealsRequest(instance_id, amount));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getChartsBySection(String userAccessToken, String chartSection,
      final Callback<ChartsBySectionResponse> callback) {
    Call<ChartsBySectionResponse> apiCall = new RestClient().getGMFitService()
        .getChartsBySection(userAccessToken, Constants.BASE_URL_ADDRESS +
            "charts?section=" + chartSection);

    apiCall.enqueue(new Callback<ChartsBySectionResponse>() {
      @Override public void onResponse(Call<ChartsBySectionResponse> call,
          Response<ChartsBySectionResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
      }
    });
  }

  void addMetricChart(String userAccessToken, int chart_id,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .addMetricChart(userAccessToken, new AddMetricChartRequest(chart_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getPeriodicalChartData(String userAccessToken, String start_date, String end_date,
      String type, String monitored_metric, final Callback<ChartMetricBreakdownResponse> callback) {
    Call<ChartMetricBreakdownResponse> apiCall = new RestClient().getGMFitService()
        .getPeriodicalChartData(userAccessToken, start_date, end_date, type, monitored_metric);

    apiCall.enqueue(new Callback<ChartMetricBreakdownResponse>() {
      @Override public void onResponse(Call<ChartMetricBreakdownResponse> call,
          Response<ChartMetricBreakdownResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ChartMetricBreakdownResponse> call, Throwable t) {
      }
    });
  }

  void getUserGoalMetrics(String userAccessToken, String date, String type,
      final Callback<UserGoalMetricsResponse> callback) {
    Call<UserGoalMetricsResponse> apiCall =
        new RestClient().getGMFitService().getUserGoalMetrics(userAccessToken, date, type);

    apiCall.enqueue(new Callback<UserGoalMetricsResponse>() {
      @Override public void onResponse(Call<UserGoalMetricsResponse> call,
          Response<UserGoalMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserGoalMetricsResponse> call, Throwable t) {
      }
    });
  }

  void requestNewMeal(String userAccessToken, String mealName,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .requestNewMeal(userAccessToken, new RequestMealRequest(mealName));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getMedicalTests(String userAccessToken, final Callback<MedicalTestsResponse> callback) {
    Call<MedicalTestsResponse> apiCall =
        new RestClient().getGMFitService().getMedicalTests(userAccessToken);

    apiCall.enqueue(new Callback<MedicalTestsResponse>() {
      @Override public void onResponse(Call<MedicalTestsResponse> call,
          Response<MedicalTestsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalTestsResponse> call, Throwable t) {
      }
    });
  }

  void getTesticularMetrics(String userAccessToken,
      final Callback<MedicalTestMetricsResponse> callback) {
    Call<MedicalTestMetricsResponse> apiCall =
        new RestClient().getGMFitService().getTesticularMetrics(userAccessToken);

    apiCall.enqueue(new Callback<MedicalTestMetricsResponse>() {
      @Override public void onResponse(Call<MedicalTestMetricsResponse> call,
          Response<MedicalTestMetricsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<MedicalTestMetricsResponse> call, Throwable t) {
      }
    });
  }

  void getWidgets(String userAccessToken, String sectionName,
      final Callback<WidgetsResponse> callback) {
    Call<WidgetsResponse> apiCall =
        new RestClient().getGMFitService().getWidgets(userAccessToken, sectionName);

    apiCall.enqueue(new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
      }
    });
  }

  void getWidgetsWithDate(String userAccessToken, String sectionName, String date,
      final Callback<WidgetsResponse> callback) {
    Call<WidgetsResponse> apiCall =
        new RestClient().getGMFitService().getWidgetsWithDate(userAccessToken, sectionName, date);

    apiCall.enqueue(new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
      }
    });
  }

  void storeNewHealthTest(String userAccessToken, RequestBody test_name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .storeNewHealthTest(userAccessToken, test_name, date_taken, metrics, imageFiles);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void editExistingHealthTest(String userAccessToken, RequestBody instance_id, RequestBody name,
      RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      Map<String, RequestBody> deletedImages, final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .editExistingHealthTest(userAccessToken, instance_id, name, date_taken, metrics,
            deletedImages, imageFiles);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getTakenMedicalTests(String userAccessToken,
      final Callback<TakenMedicalTestsResponse> callback) {
    Call<TakenMedicalTestsResponse> apiCall =
        new RestClient().getGMFitService().getTakenMedicalTests(userAccessToken);

    apiCall.enqueue(new Callback<TakenMedicalTestsResponse>() {
      @Override public void onResponse(Call<TakenMedicalTestsResponse> call,
          Response<TakenMedicalTestsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {
      }
    });
  }

  void getUserProfile(String userAccessToken, final Callback<UserProfileResponse> callback) {
    Call<UserProfileResponse> apiCall =
        new RestClient().getGMFitService().getUserProfile(userAccessToken);

    apiCall.enqueue(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
      }
    });
  }

  void getEmergencyProfile(String userAccessToken,
      final Callback<EmergencyProfileResponse> callback) {
    Call<EmergencyProfileResponse> apiCall =
        new RestClient().getGMFitService().getEmergencyProfile(userAccessToken);

    apiCall.enqueue(new Callback<EmergencyProfileResponse>() {
      @Override public void onResponse(Call<EmergencyProfileResponse> call,
          Response<EmergencyProfileResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<EmergencyProfileResponse> call, Throwable t) {
      }
    });
  }

  void deleteUserChart(String userAccessToken, String chart_id,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .deleteUserChart(userAccessToken, new DeleteUserChartRequest(chart_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void deleteUserMeal(String userAccessToken, int instance_id,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall = new RestClient().getGMFitService()
        .deleteUserMeal(userAccessToken, new DeleteMealRequest(instance_id));

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void updateUserPicture(String userAccessToken, Map<String, RequestBody> profilePicture,
      final Callback<DefaultGetResponse> callback) {
    Call<DefaultGetResponse> apiCall =
        new RestClient().getGMFitService().updateUserPicture(userAccessToken, profilePicture);

    apiCall.enqueue(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
      }
    });
  }

  void getActivityLevels(String userAccessToken, final Callback<ActivityLevelsResponse> callback) {
    Call<ActivityLevelsResponse> apiCall =
        new RestClient().getGMFitService().getActivityLevels(userAccessToken);

    apiCall.enqueue(new Callback<ActivityLevelsResponse>() {
      @Override public void onResponse(Call<ActivityLevelsResponse> call,
          Response<ActivityLevelsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<ActivityLevelsResponse> call, Throwable t) {
      }
    });
  }

  void getUserGoals(String userAccessToken, final Callback<UserGoalsResponse> callback) {
    Call<UserGoalsResponse> apiCall =
        new RestClient().getGMFitService().getUserGoals(userAccessToken);

    apiCall.enqueue(new Callback<UserGoalsResponse>() {
      @Override
      public void onResponse(Call<UserGoalsResponse> call, Response<UserGoalsResponse> response) {
        callback.onResponse(call, response);
      }

      @Override public void onFailure(Call<UserGoalsResponse> call, Throwable t) {
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

    RegisterRequest(String name, String email, String password) {
      this.name = name;
      this.email = email;
      this.password = password;
    }
  }

  public class UpdateProfileRequest {
    final String date_of_birth;
    final String blood_type;
    final String country;
    final String metric_system;
    final int medical_conditions;
    final int user_goal;
    final int activity_level;
    final int gender;
    final double height;
    final double weight;
    final double BMI;

    UpdateProfileRequest(String date_of_birth, String blood_type, String country,
        int medical_conditions, String metric_system, int user_goal, int activity_level, int gender,
        double height, double weight, double BMI) {
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
      this.BMI = BMI;
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
    final int amount;
    final String when;
    final String date;

    StoreNewMealRequest(int meal_id, int amount, String when, String date) {
      this.meal_id = meal_id;
      this.amount = amount;
      this.when = when;
      this.date = date;
    }
  }

  public class UpdateMealsRequest {
    final int instance_id;
    final int amount;

    UpdateMealsRequest(int instance_id, int amount) {
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

  public class RequestMealRequest {
    final String name;

    RequestMealRequest(String name) {
      this.name = name;
    }
  }

  public class DeleteUserChartRequest {
    final String chart_id;

    public DeleteUserChartRequest(String chart_id) {
      this.chart_id = chart_id;
    }
  }

  public class DeleteMealRequest {
    final int instance_id;

    public DeleteMealRequest(int instance_id) {
      this.instance_id = instance_id;
    }
  }
}
