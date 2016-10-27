package com.mcsaatchi.gmfit.data_access;


import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.rest.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.rest.HealthWidgetsResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.RecentMealsResponse;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.UiResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.rest.UserPolicyResponse;
import com.mcsaatchi.gmfit.rest.UserProfileResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Callback;

public class DataAccessHandler {

    private static DataAccessHandler dataAccessHandler;

    private DataAccessHandler() {

    }

    public static DataAccessHandler getInstance() {
        if (dataAccessHandler == null) {
            dataAccessHandler = new DataAccessHandler();
        }

        return dataAccessHandler;
    }

    public void findMeals(String userAccessToken, String mealName, Callback<SearchMealItemResponse> callback) {
        ApiCallsHandler.getInstance().findMeals(userAccessToken, mealName, callback);
    }

    public void getSlugBreakdownForChart(final String chartType, String userAccessToken, final Callback<SlugBreakdownResponse> callback) {
        ApiCallsHandler.getInstance().getSlugBreakdownForChart(chartType, userAccessToken, callback);
    }

    public void refreshAccessToken(String userAccessToken, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().refreshAccessToken(userAccessToken, callback);
    }

    public void synchronizeMetricsWithServer(String userAccessToken, String[] slugsArray, int[] valuesArray) {
        ApiCallsHandler.getInstance().synchronizeMetricsWithServer(userAccessToken, slugsArray, valuesArray);
    }

    public void signInUser(String email, String password, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().signInUser(email, password, callback);
    }

    public void registerUser(String full_name, String email, String password, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().registerUser(full_name, email, password, callback);
    }

    public void signInUserSilently(String email, String password, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().signInUserSilently(email, password, callback);
    }

    public void signOutUser(String userAccessToken, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().signOutUser(userAccessToken, callback);
    }

    public void updateUserProfile(String userAccessToken, String finalDateOfBirth, String bloodType, String nationality, int medical_condition, String measurementSystem, String goal,
                                  int finalGender, double height, double weight, double BMI, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().updateUserProfile(userAccessToken, finalDateOfBirth, bloodType, nationality, medical_condition, measurementSystem, goal,
                finalGender, height, weight, BMI, callback);
    }

    public void getUiForSection(String userAccessToken, String section, Callback<UiResponse> callback) {
        ApiCallsHandler.getInstance().getUiForSection(userAccessToken, section, callback);
    }

    public void getMedicalConditions(String userAccessToken, Callback<MedicalConditionsResponse> callback) {
        ApiCallsHandler.getInstance().getMedicalConditions(userAccessToken, callback);
    }

    public void sendResetPasswordLink(String userAccessToken, String email, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().sendResetPasswordLink(userAccessToken, email, callback);
    }

    public void finalizeResetPassword(String resetPasswordToken, String newPassword, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().finalizeResetPassword(resetPasswordToken, newPassword, callback);
    }

    public void getMealMetrics(String userAccessToken, String fullUrl, Callback<MealMetricsResponse> callback) {
        ApiCallsHandler.getInstance().getMealMetrics(userAccessToken, fullUrl, callback);
    }

    public void verifyUser(String userAccessToken, String verificationCode, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().verifyUser(userAccessToken, verificationCode, callback);
    }

    public void updateUserWidgets(String userAccessToken, int[] widgetIds, int[] widgetPositions, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().updateUserWidgets(userAccessToken, widgetIds, widgetPositions, callback);
    }

    public void updateUserCharts(String userAccessToken, int[] chartIds, int[] chartPositions, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().updateUserCharts(userAccessToken, chartIds, chartPositions, callback);
    }

    public void updateUserMeals(String userAccessToken, int instance_id, int amount, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().updateUserMeals(userAccessToken, instance_id, amount, callback);
    }

    public void deleteUserMeal(String userAccessToken, int instance_id, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().deleteUserMeal(userAccessToken, instance_id, callback);
    }

    public void getUserAddedMeals(String userAccessToken, Callback<UserMealsResponse> callback) {
        ApiCallsHandler.getInstance().getUserAddedMeals(userAccessToken, callback);
    }

    public void getUserPolicy(String userAccessToken, Callback<UserPolicyResponse> callback) {
        ApiCallsHandler.getInstance().getUserPolicy(userAccessToken, callback);
    }

    public void registerUserFacebook(String facebookAccessToken, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().registerUserFacebook(facebookAccessToken, callback);
    }

    public void storeNewMeal(String userAccessToken, int meal_id, int servingsAmount, String when, String date, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().storeNewMeal(userAccessToken, meal_id, servingsAmount, when, date, callback);
    }

    public void getRecentMeals(String userAccessToken, String fullUrl, Callback<RecentMealsResponse> callback) {
        ApiCallsHandler.getInstance().getRecentMeals(userAccessToken, fullUrl, callback);
    }

    public void getPeriodicalChartData(String userAccessToken, String start_date, String end_date, String type, String monitored_metric, Callback<ChartMetricBreakdownResponse> callback) {
        ApiCallsHandler.getInstance().getPeriodicalChartData(userAccessToken, start_date, end_date, type, monitored_metric, callback);
    }

    public void addMetricChart(String userAccessToken, int chart_id, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().addMetricChart(userAccessToken, chart_id, callback);
    }

    public void getChartsBySection(String userAccessToken, String sectionName, Callback<ChartsBySectionResponse> callback) {
        ApiCallsHandler.getInstance().getChartsBySection(userAccessToken, sectionName, callback);
    }

    public void requestNewMeal(String userAccessToken, String mealName, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().requestNewMeal(userAccessToken, mealName, callback);
    }

    public void getMedicalTests(String userAccessToken, Callback<MedicalTestsResponse> callback) {
        ApiCallsHandler.getInstance().getMedicalTests(userAccessToken, callback);
    }

    public void getHealthWidgets(String userAccessToken, String sectionName, Callback<HealthWidgetsResponse> callback) {
        ApiCallsHandler.getInstance().getHealthWidgets(userAccessToken, sectionName, callback);
    }

    public void storeNewHealthTest(String userAccessToken, RequestBody test_slug, RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().storeNewHealthTest(userAccessToken, test_slug, date_taken, metrics, imageFiles, callback);
    }

    public void editExistingHealthTest(String userAccessToken, RequestBody instance_id, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles, Map<String, RequestBody> deletedImages, Callback<DefaultGetResponse> callback) {
        ApiCallsHandler.getInstance().editExistingHealthTest(userAccessToken, instance_id, metrics, imageFiles, deletedImages, callback);
    }

    public void getTakenMedicalTests(String userAccessToken, Callback<TakenMedicalTestsResponse> callback) {
        ApiCallsHandler.getInstance().getTakenMedicalTests(userAccessToken, callback);
    }

    public void getUserProfile(String userAccessToken, Callback<UserProfileResponse> callback){
        ApiCallsHandler.getInstance().getUserProfile(userAccessToken, callback);
    }

    public void getEmergencyProfile(String userAccessToken, Callback<EmergencyProfileResponse> callback){
        ApiCallsHandler.getInstance().getEmergencyProfile(userAccessToken, callback);
    }

    public void deleteUserChart(String userAccessToken, String chart_id, Callback<DefaultGetResponse> callback){
        ApiCallsHandler.getInstance().deleteUserChart(userAccessToken, chart_id, callback);
    }
}
