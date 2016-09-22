package com.mcsaatchi.gmfit.data_access;


import android.content.SharedPreferences;

import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.MealMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.rest.UiResponse;
import com.mcsaatchi.gmfit.rest.UserMealsResponse;
import com.mcsaatchi.gmfit.rest.UserPolicyResponse;

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

    public static void findMeals(String userAccessToken, String mealName, Callback<SearchMealItemResponse> callback){
        ApiCallsHandler.getInstance().findMeals(userAccessToken, mealName, callback);
    }

    public void getSlugBreakdownForChart(final String chartType, SharedPreferences prefs, final Callback<SlugBreakdownResponse> callback) {
        ApiCallsHandler.getInstance().getSlugBreakdownForChart(chartType, prefs, callback);
    }

    public void refreshAccessToken(SharedPreferences prefs, Callback<AuthenticationResponse> callback) {
        ApiCallsHandler.getInstance().refreshAccessToken(prefs, callback);
    }

    public void synchronizeMetricsWithServer(SharedPreferences prefs, String[] slugsArray, int[] valuesArray) {
        ApiCallsHandler.getInstance().synchronizeMetricsWithServer(prefs, slugsArray, valuesArray);
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

    public void getMealMetrics(String userAccessToken, String fullUrl, Callback<MealMetricsResponse> callback){
        ApiCallsHandler.getInstance().getMealMetrics(userAccessToken, fullUrl, callback);
    }

    public void verifyUser(String userAccessToken, String verificationCode, Callback<DefaultGetResponse> callback){
        ApiCallsHandler.getInstance().verifyUser(userAccessToken, verificationCode, callback);
    }

    public void updateUserWidgets(String userAccessToken, int[] widgetIds, int[] widgetPositions, Callback<DefaultGetResponse> callback){
        ApiCallsHandler.getInstance().updateUserWidgets(userAccessToken, widgetIds, widgetPositions, callback);
    }

    public void getUserAddedMeals(String userAccessToken, Callback<UserMealsResponse> callback){
        ApiCallsHandler.getInstance().getUserAddedMeals(userAccessToken, callback);
    }

    public void getUserPolicy(String userAccessToken, Callback<UserPolicyResponse> callback){
        ApiCallsHandler.getInstance().getUserPolicy(userAccessToken, callback);
    }

    public void registerUserFacebook(String facebookAccessToken, Callback<AuthenticationResponse> callback){
        ApiCallsHandler.getInstance().registerUserFacebook(facebookAccessToken, callback);
    }

    public void storeNewMeal(String userAccessToken, int meal_id, int servingsAmount, String when, Callback<DefaultGetResponse> callback){
        ApiCallsHandler.getInstance().storeNewMeal(userAccessToken, meal_id, servingsAmount, when, callback);
    }
}
