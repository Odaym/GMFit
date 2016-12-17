package com.mcsaatchi.gmfit.architecture.data_access;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
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
    ((GMFitApplication) app).getAppComponent().inject(this);
  }

  public void findMeals(String mealName, Callback<SearchMealItemResponse> callback) {
    apiCallsHandler.findMeals(mealName, callback);
  }

  public void searchForMealBarcode(String barcode, Callback<SearchMealItemResponse> callback) {
    apiCallsHandler.searchForMealBarcode(barcode, callback);
  }

  public void getSlugBreakdownForChart(final String chartType,
      final Callback<SlugBreakdownResponse> callback) {
    apiCallsHandler.getSlugBreakdownForChart(chartType, callback);
  }

  public void refreshAccessToken(Callback<AuthenticationResponse> callback) {
    apiCallsHandler.refreshAccessToken(callback);
  }

  public void synchronizeMetricsWithServer(String[] slugsArray, Number[] valuesArray) {
    apiCallsHandler.synchronizeMetricsWithServer(slugsArray, valuesArray);
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

  public void signOutUser(Callback<DefaultGetResponse> callback) {
    apiCallsHandler.signOutUser(callback);
  }

  public void updateUserProfile(String finalDateOfBirth, String bloodType, String nationality,
      int medical_condition, String measurementSystem, int goalId, int activityLevelId,
      int finalGender, double height, double weight, String onboard,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserProfile(finalDateOfBirth, bloodType, nationality, medical_condition,
        measurementSystem, goalId, activityLevelId, finalGender, height, weight, onboard, callback);
  }

  public void updateUserPicture(Map<String, RequestBody> profilePicture,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserPicture(profilePicture, callback);
  }

  public void getUiForSection(String section, Callback<UiResponse> callback) {
    apiCallsHandler.getUiForSection(section, callback);
  }

  public void getMedicalConditions(Callback<MedicalConditionsResponse> callback) {
    apiCallsHandler.getMedicalConditions(callback);
  }

  public void sendResetPasswordLink(String email, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.sendResetPasswordLink(email, callback);
  }

  public void changePassword(String old_password, String new_password,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.changePassword(old_password, new_password, callback);
  }

  public void finalizeResetPassword(String resetPasswordToken, String newPassword,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.finalizeResetPassword(resetPasswordToken, newPassword, callback);
  }

  public void getMealMetrics(String fullUrl, Callback<MealMetricsResponse> callback) {
    apiCallsHandler.getMealMetrics(fullUrl, callback);
  }

  public void getUserGoalMetrics(String date, String type,
      Callback<UserGoalMetricsResponse> callback) {
    apiCallsHandler.getUserGoalMetrics(date, type, callback);
  }

  public void verifyUser(String verificationCode, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.verifyUser(verificationCode, callback);
  }

  public void updateUserWidgets(int[] widgetIds, int[] widgetPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserWidgets(widgetIds, widgetPositions, callback);
  }

  public void updateUserCharts(int[] chartIds, int[] chartPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserCharts(chartIds, chartPositions, callback);
  }

  public void updateUserMeals(int instance_id, float amount,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserMeals(instance_id, amount, callback);
  }

  public void deleteUserMeal(int instance_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserMeal(instance_id, callback);
  }

  public void getUserAddedMeals(Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMeals(callback);
  }

  public void getUserAddedMealsOnDate(String chosenDate, Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMealsOnDate(chosenDate, callback);
  }

  public void getMetaTexts(String section, Callback<MetaTextsResponse> callback) {
    apiCallsHandler.getMetaTexts(section, callback);
  }

  public void handleFacebookProcess(String facebookAccessToken,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.handleFacebookProcess(facebookAccessToken, callback);
  }

  public void storeNewMeal(int meal_id, float servingsAmount, String when, String date,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewMeal(meal_id, servingsAmount, when, date, callback);
  }

  public void getRecentMeals(String fullUrl, Callback<RecentMealsResponse> callback) {
    apiCallsHandler.getRecentMeals(fullUrl, callback);
  }

  public void getPeriodicalChartData(String start_date, String end_date, String type,
      String monitored_metric, Callback<ChartMetricBreakdownResponse> callback) {
    apiCallsHandler.getPeriodicalChartData(start_date, end_date, type, monitored_metric, callback);
  }

  public void addMetricChart(int chart_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.addMetricChart(chart_id, callback);
  }

  public void getChartsBySection(String sectionName, Callback<ChartsBySectionResponse> callback) {
    apiCallsHandler.getChartsBySection(sectionName, callback);
  }

  public void requestNewMeal(String mealName, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.requestNewMeal(mealName, callback);
  }

  public void getTesticularMetrics(Callback<MedicalTestMetricsResponse> callback) {
    apiCallsHandler.getTesticularMetrics(callback);
  }

  public void getMedicalTests(Callback<MedicalTestsResponse> callback) {
    apiCallsHandler.getMedicalTests(callback);
  }

  public void getWidgets(String sectionName, Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgets(sectionName, callback);
  }

  public void getWidgetsWithDate(String sectionName, String date,
      Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgetsWithDate(sectionName, date, callback);
  }

  public void storeNewHealthTest(RequestBody test_name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewHealthTest(test_name, date_taken, metrics, imageFiles, callback);
  }

  public void editExistingHealthTest(RequestBody instance_id, RequestBody name,
      RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      RequestBody deletedImages, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.editExistingHealthTest(instance_id, name, date_taken, metrics, imageFiles,
        deletedImages, callback);
  }

  public void getTakenMedicalTests(Callback<TakenMedicalTestsResponse> callback) {
    apiCallsHandler.getTakenMedicalTests(callback);
  }

  public void getOnboardingStatus(Callback<UserProfileResponse> callback) {
    apiCallsHandler.getOnboardingStatus(callback);
  }

  public void getUserProfile(Callback<UserProfileResponse> callback) {
    apiCallsHandler.getUserProfile(callback);
  }

  public void getEmergencyProfile(Callback<EmergencyProfileResponse> callback) {
    apiCallsHandler.getEmergencyProfile(callback);
  }

  public void deleteUserChart(String chart_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserChart(chart_id, callback);
  }

  public void getActivityLevels(Callback<ActivityLevelsResponse> callback) {
    apiCallsHandler.getActivityLevels(callback);
  }

  public void getUserGoals(Callback<UserGoalsResponse> callback) {
    apiCallsHandler.getUserGoals(callback);
  }
}
