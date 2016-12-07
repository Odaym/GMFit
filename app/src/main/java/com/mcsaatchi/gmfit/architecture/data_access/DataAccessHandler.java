package com.mcsaatchi.gmfit.architecture.data_access;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.GMFit_Application;
import com.mcsaatchi.gmfit.architecture.rest.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MetaTextsResponse;
import com.mcsaatchi.gmfit.architecture.rest.RecentMealsResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponse;
import java.util.Map;
import javax.inject.Inject;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class DataAccessHandler {

  @Inject ApiCallsHandler apiCallsHandler;

  public DataAccessHandler(Context app) {
    ((GMFit_Application) app).getAppComponent().inject(this);
  }

  public void findMeals(String userAccessToken, String mealName,
      Callback<SearchMealItemResponse> callback) {
    apiCallsHandler.findMeals(userAccessToken, mealName, callback);
  }

  public void getSlugBreakdownForChart(final String chartType, String userAccessToken,
      final Callback<SlugBreakdownResponse> callback) {
    apiCallsHandler.getSlugBreakdownForChart(chartType, userAccessToken, callback);
  }

  public void refreshAccessToken(String userAccessToken,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.refreshAccessToken(userAccessToken, callback);
  }

  public void synchronizeMetricsWithServer(String userAccessToken, String[] slugsArray,
      Number[] valuesArray) {
    apiCallsHandler.synchronizeMetricsWithServer(userAccessToken, slugsArray, valuesArray);
  }

  public void signInUser(String email, String password, Callback<AuthenticationResponse> callback) {
    apiCallsHandler.signInUser(email, password, callback);
  }

  public void registerUser(String full_name, String email, String password,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.registerUser(full_name, email, password, callback);
  }

  public void signInUserSilently(String email, String password,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.signInUserSilently(email, password, callback);
  }

  public void signOutUser(String userAccessToken, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.signOutUser(userAccessToken, callback);
  }

  public void updateUserProfile(String userAccessToken, String finalDateOfBirth, String bloodType,
      String nationality, int medical_condition, String measurementSystem, int goalId,
      int activityLevelId, int finalGender, double height, double weight,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserProfile(userAccessToken, finalDateOfBirth, bloodType, nationality,
        medical_condition, measurementSystem, goalId, activityLevelId, finalGender, height, weight,
        callback);
  }

  public void updateUserPicture(String userAccessToken, Map<String, RequestBody> profilePicture,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserPicture(userAccessToken, profilePicture, callback);
  }

  public void getUiForSection(String userAccessToken, String section,
      Callback<UiResponse> callback) {
    apiCallsHandler.getUiForSection(userAccessToken, section, callback);
  }

  public void getMedicalConditions(String userAccessToken,
      Callback<MedicalConditionsResponse> callback) {
    apiCallsHandler.getMedicalConditions(userAccessToken, callback);
  }

  public void sendResetPasswordLink(String userAccessToken, String email,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.sendResetPasswordLink(userAccessToken, email, callback);
  }

  public void finalizeResetPassword(String resetPasswordToken, String newPassword,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.finalizeResetPassword(resetPasswordToken, newPassword, callback);
  }

  public void getMealMetrics(String userAccessToken, String fullUrl,
      Callback<MealMetricsResponse> callback) {
    apiCallsHandler.getMealMetrics(userAccessToken, fullUrl, callback);
  }

  public void getUserGoalMetrics(String userAccessToken, String date, String type,
      Callback<UserGoalMetricsResponse> callback) {
    apiCallsHandler.getUserGoalMetrics(userAccessToken, date, type, callback);
  }

  public void verifyUser(String userAccessToken, String verificationCode,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.verifyUser(userAccessToken, verificationCode, callback);
  }

  public void updateUserWidgets(String userAccessToken, int[] widgetIds, int[] widgetPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserWidgets(userAccessToken, widgetIds, widgetPositions, callback);
  }

  public void updateUserCharts(String userAccessToken, int[] chartIds, int[] chartPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserCharts(userAccessToken, chartIds, chartPositions, callback);
  }

  public void updateUserMeals(String userAccessToken, int instance_id, int amount,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserMeals(userAccessToken, instance_id, amount, callback);
  }

  public void deleteUserMeal(String userAccessToken, int instance_id,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserMeal(userAccessToken, instance_id, callback);
  }

  public void getUserAddedMeals(String userAccessToken, Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMeals(userAccessToken, callback);
  }

  public void getUserAddedMealsOnDate(String userAccessToken, String chosenDate,
      Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMealsOnDate(userAccessToken, chosenDate, callback);
  }

  public void getMetaTexts(String userAccessToken, String section,
      Callback<MetaTextsResponse> callback) {
    apiCallsHandler.getMetaTexts(userAccessToken, section, callback);
  }

  public void handleFacebookProcess(String facebookAccessToken,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.handleFacebookProcess(facebookAccessToken, callback);
  }

  public void storeNewMeal(String userAccessToken, int meal_id, int servingsAmount, String when,
      String date, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewMeal(userAccessToken, meal_id, servingsAmount, when, date, callback);
  }

  public void getRecentMeals(String userAccessToken, String fullUrl,
      Callback<RecentMealsResponse> callback) {
    apiCallsHandler.getRecentMeals(userAccessToken, fullUrl, callback);
  }

  public void getPeriodicalChartData(String userAccessToken, String start_date, String end_date,
      String type, String monitored_metric, Callback<ChartMetricBreakdownResponse> callback) {
    apiCallsHandler.getPeriodicalChartData(userAccessToken, start_date, end_date, type,
        monitored_metric, callback);
  }

  public void addMetricChart(String userAccessToken, int chart_id,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.addMetricChart(userAccessToken, chart_id, callback);
  }

  public void getChartsBySection(String userAccessToken, String sectionName,
      Callback<ChartsBySectionResponse> callback) {
    apiCallsHandler.getChartsBySection(userAccessToken, sectionName, callback);
  }

  public void requestNewMeal(String userAccessToken, String mealName,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.requestNewMeal(userAccessToken, mealName, callback);
  }

  public void getTesticularMetrics(String userAccessToken,
      Callback<MedicalTestMetricsResponse> callback) {
    apiCallsHandler.getTesticularMetrics(userAccessToken, callback);
  }

  public void getMedicalTests(String userAccessToken, Callback<MedicalTestsResponse> callback) {
    apiCallsHandler.getMedicalTests(userAccessToken, callback);
  }

  public void getWidgets(String userAccessToken, String sectionName,
      Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgets(userAccessToken, sectionName, callback);
  }

  public void getWidgetsWithDate(String userAccessToken, String sectionName, String date,
      Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgetsWithDate(userAccessToken, sectionName, date, callback);
  }

  public void storeNewHealthTest(String userAccessToken, RequestBody test_name,
      RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewHealthTest(userAccessToken, test_name, date_taken, metrics, imageFiles,
        callback);
  }

  public void editExistingHealthTest(String userAccessToken, RequestBody instance_id,
      RequestBody name, RequestBody date_taken, Map<String, RequestBody> metrics,
      Map<String, RequestBody> imageFiles, RequestBody deletedImages,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.editExistingHealthTest(userAccessToken, instance_id, name, date_taken, metrics,
        imageFiles, deletedImages, callback);
  }

  public void getTakenMedicalTests(String userAccessToken,
      Callback<TakenMedicalTestsResponse> callback) {
    apiCallsHandler.getTakenMedicalTests(userAccessToken, callback);
  }

  public void getUserProfile(String userAccessToken, Callback<UserProfileResponse> callback) {
    apiCallsHandler.getUserProfile(userAccessToken, callback);
  }

  public void getEmergencyProfile(String userAccessToken,
      Callback<EmergencyProfileResponse> callback) {
    apiCallsHandler.getEmergencyProfile(userAccessToken, callback);
  }

  public void deleteUserChart(String userAccessToken, String chart_id,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserChart(userAccessToken, chart_id, callback);
  }

  public void getActivityLevels(String userAccessToken, Callback<ActivityLevelsResponse> callback) {
    apiCallsHandler.getActivityLevels(userAccessToken, callback);
  }

  public void getUserGoals(String userAccessToken, Callback<UserGoalsResponse> callback) {
    apiCallsHandler.getUserGoals(userAccessToken, callback);
  }
}
