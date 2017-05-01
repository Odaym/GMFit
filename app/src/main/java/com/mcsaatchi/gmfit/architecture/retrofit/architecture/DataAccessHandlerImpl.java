package com.mcsaatchi.gmfit.architecture.retrofit.architecture;

import android.content.Context;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AddCRMNoteResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMNotesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CertainPDFResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartMetricBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CounsellingInformationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MealMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MetaTextsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MostPopularMedicationsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.RecentMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMealItemResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SearchMedicinesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UiResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WeightHistoryResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WidgetsResponse;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class DataAccessHandlerImpl implements DataAccessHandler {

  @Inject ApiCallsHandler apiCallsHandler;

  public DataAccessHandlerImpl(Context app) {
    ((GMFitApplication) app).getAppComponent().inject(this);
  }

  @Override public void findMeals(String mealName, Callback<SearchMealItemResponse> callback) {
    apiCallsHandler.findMeals(mealName, callback);
  }

  @Override
  public void searchForMealBarcode(String barcode, Callback<SearchMealItemResponse> callback) {
    apiCallsHandler.searchForMealBarcode(barcode, callback);
  }

  @Override public void getSlugBreakdownForChart(final String chartType,
      final Callback<SlugBreakdownResponse> callback) {
    apiCallsHandler.getSlugBreakdownForChart(chartType, callback);
  }

  @Override public void refreshAccessToken(Callback<AuthenticationResponse> callback) {
    apiCallsHandler.refreshAccessToken(callback);
  }

  @Override public void synchronizeMetricsWithServer(String[] slugsArray, Number[] valuesArray,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.synchronizeMetricsWithServer(slugsArray, valuesArray, callback);
  }

  @Override
  public void signInUser(String email, String password, Callback<AuthenticationResponse> callback) {
    apiCallsHandler.signInUser(email, password, callback);
  }

  @Override public void registerUser(String full_name, String email, String password,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.registerUser(full_name, email, password, callback);
  }

  @Override public void signInUserSilently(String email, String password,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.signInUserSilently(email, password, callback);
  }

  @Override public void signOutUser(Callback<DefaultGetResponse> callback) {
    apiCallsHandler.signOutUser(callback);
  }

  @Override public void updateUserProfile(RequestBody finalDateOfBirth, RequestBody bloodType,
      RequestBody nationality, HashMap<String, RequestBody> medicalConditions,
      RequestBody measurementSystem, RequestBody goalId, RequestBody activityLevelId,
      RequestBody finalGender, RequestBody height, RequestBody weight, RequestBody onboard,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserProfile(finalDateOfBirth, bloodType, nationality, medicalConditions,
        measurementSystem, goalId, activityLevelId, finalGender, height, weight, onboard, callback);
  }

  @Override public void updateUserProfileExplicitly(RequestBody name, RequestBody phone_number,
      RequestBody gender, RequestBody date_of_birth, RequestBody blood_type, RequestBody height,
      RequestBody weight, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserProfileExplicitly(name, phone_number, gender, date_of_birth,
        blood_type, height, weight, callback);
  }

  @Override public void updateUserWeight(double weight, String created_at,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserWeight(weight, created_at, callback);
  }

  @Override
  public void updateOneSignalToken(String onesignal_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateOneSignalToken(onesignal_id, callback);
  }

  @Override public void updateUserPicture(Map<String, RequestBody> profilePicture,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserPicture(profilePicture, callback);
  }

  @Override public void getUiForSection(String section, Callback<UiResponse> callback) {
    apiCallsHandler.getUiForSection(section, callback);
  }

  @Override public void getMedicalConditions(Callback<MedicalConditionsResponse> callback) {
    apiCallsHandler.getMedicalConditions(callback);
  }

  @Override public void sendResetPasswordLink(String email, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.sendResetPasswordLink(email, callback);
  }

  @Override public void changePassword(String old_password, String new_password,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.changePassword(old_password, new_password, callback);
  }

  @Override public void finalizeResetPassword(String resetPasswordToken, String newPassword,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.finalizeResetPassword(resetPasswordToken, newPassword, callback);
  }

  @Override public void getMealMetrics(String fullUrl, Callback<MealMetricsResponse> callback) {
    apiCallsHandler.getMealMetrics(fullUrl, callback);
  }

  @Override public void getUserGoalMetrics(String date, String type,
      Callback<UserGoalMetricsResponse> callback) {
    apiCallsHandler.getUserGoalMetrics(date, type, callback);
  }

  @Override public void verifyUser(String verificationCode, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.verifyUser(verificationCode, callback);
  }

  @Override public void updateUserWidgets(int[] widgetIds, int[] widgetPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserWidgets(widgetIds, widgetPositions, callback);
  }

  @Override public void updateUserCharts(int[] chartIds, int[] chartPositions,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserCharts(chartIds, chartPositions, callback);
  }

  @Override public void updateUserMeals(int instance_id, float amount,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.updateUserMeals(instance_id, amount, callback);
  }

  @Override public void deleteUserTest(int instance_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserTest(instance_id, callback);
  }

  @Override public void deleteUserMeal(int instance_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserMeal(instance_id, callback);
  }

  @Override public void getUserAddedMeals(Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMeals(callback);
  }

  @Override
  public void getUserAddedMealsOnDate(String chosenDate, Callback<UserMealsResponse> callback) {
    apiCallsHandler.getUserAddedMealsOnDate(chosenDate, callback);
  }

  @Override public void getMetaTexts(String section, Callback<MetaTextsResponse> callback) {
    apiCallsHandler.getMetaTexts(section, callback);
  }

  @Override public void handleFacebookProcess(String facebookAccessToken,
      Callback<AuthenticationResponse> callback) {
    apiCallsHandler.handleFacebookProcess(facebookAccessToken, callback);
  }

  @Override public void storeNewMeal(int meal_id, float servingsAmount, String when, String date,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewMeal(meal_id, servingsAmount, when, date, callback);
  }

  @Override public void getRecentMeals(String fullUrl, Callback<RecentMealsResponse> callback) {
    apiCallsHandler.getRecentMeals(fullUrl, callback);
  }

  @Override public void getPeriodicalChartData(String start_date, String end_date, String type,
      String monitored_metric, Callback<ChartMetricBreakdownResponse> callback) {
    apiCallsHandler.getPeriodicalChartData(start_date, end_date, type, monitored_metric, callback);
  }

  @Override public void addMetricChart(int chart_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.addMetricChart(chart_id, callback);
  }

  @Override
  public void getChartsBySection(String sectionName, Callback<ChartsBySectionResponse> callback) {
    apiCallsHandler.getChartsBySection(sectionName, callback);
  }

  @Override public void requestNewMeal(String mealName, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.requestNewMeal(mealName, callback);
  }

  @Override public void getTesticularMetrics(Callback<MedicalTestMetricsResponse> callback) {
    apiCallsHandler.getTesticularMetrics(callback);
  }

  @Override public void getMedicalTests(Callback<MedicalTestsResponse> callback) {
    apiCallsHandler.getMedicalTests(callback);
  }

  @Override public void getWidgets(String sectionName, Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgets(sectionName, callback);
  }

  @Override public void getWidgetsWithDate(String sectionName, String date,
      Callback<WidgetsResponse> callback) {
    apiCallsHandler.getWidgetsWithDate(sectionName, date, callback);
  }

  @Override public void storeNewHealthTest(RequestBody test_name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      Callback<DefaultGetResponse> callback) {
    apiCallsHandler.storeNewHealthTest(test_name, date_taken, metrics, imageFiles, callback);
  }

  @Override public void editExistingHealthTest(RequestBody instance_id, RequestBody name,
      RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      RequestBody deletedImages, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.editExistingHealthTest(instance_id, name, date_taken, metrics, imageFiles,
        deletedImages, callback);
  }

  @Override public void getTakenMedicalTests(Callback<TakenMedicalTestsResponse> callback) {
    apiCallsHandler.getTakenMedicalTests(callback);
  }

  @Override public void getOnboardingStatus(Callback<UserProfileResponse> callback) {
    apiCallsHandler.getOnboardingStatus(callback);
  }

  @Override public void getUserProfile(Callback<UserProfileResponse> callback) {
    apiCallsHandler.getUserProfile(callback);
  }

  @Override public void getEmergencyProfile(Callback<EmergencyProfileResponse> callback) {
    apiCallsHandler.getEmergencyProfile(callback);
  }

  @Override public void deleteUserChart(String chart_id, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.deleteUserChart(chart_id, callback);
  }

  @Override public void getActivityLevels(Callback<ActivityLevelsResponse> callback) {
    apiCallsHandler.getActivityLevels(callback);
  }

  @Override public void getUserWeightHistory(Callback<WeightHistoryResponse> callback) {
    apiCallsHandler.getUserWeightHistory(callback);
  }

  @Override public void getUserGoals(Callback<UserGoalsResponse> callback) {
    apiCallsHandler.getUserGoals(callback);
  }

  /**
   * INSURANCE API's
   */
  @Override public void getMostPopularMedications(String indNbr, String contractNo, String country,
      String language, String password, Callback<MostPopularMedicationsResponse> callback) {
    apiCallsHandler.getMostPopularMedications(indNbr, contractNo, country, language, password,
        callback);
  }

  @Override
  public void searchMedicines(String indNbr, String contractNo, String country, String language,
      String password, String key, Callback<SearchMedicinesResponse> callback) {
    apiCallsHandler.searchMedicines(indNbr, contractNo, country, language, password, key, callback);
  }

  @Override
  public void insuranceUserLogin(String indNbr, String country, String language, String password,
      Callback<InsuranceLoginResponse> callback) {
    apiCallsHandler.insuranceUserLogin(indNbr, country, language, password, callback);
  }

  @Override public void getCoverageDescription(String contractNo, String indNbr,
      Callback<CertainPDFResponse> callback) {
    apiCallsHandler.getCoverageDescription(contractNo, indNbr, callback);
  }

  @Override public void getMembersGuide(String contractNo, String indNbr,
      Callback<CertainPDFResponse> callback) {
    apiCallsHandler.getMembersGuide(contractNo, indNbr, callback);
  }

  @Override
  public void updateInsurancePassword(String contractNo, String oldPassword, String newPassword,
      String email, String mobileNumber, Callback<UpdateInsurancePasswordResponse> callback) {
    apiCallsHandler.updateInsurancePassword(contractNo, oldPassword, newPassword, email,
        mobileNumber, callback);
  }

  @Override public void getCardDetails(String contractNo, Callback<CertainPDFResponse> callback) {
    apiCallsHandler.getCardDetails(contractNo, callback);
  }

  @Override
  public void getSubCategories(String contractNo, Callback<SubCategoriesResponse> callback) {
    apiCallsHandler.getSubCategories(contractNo, callback);
  }

  @Override
  public void getNearbyClinics(String contractNo, String providerTypesCode, int searchCtry,
      double longitude, double latitude, int fetchClosest,
      Callback<GetNearbyClinicsResponse> callback) {
    apiCallsHandler.getNearbyClinics(contractNo, providerTypesCode, searchCtry, longitude, latitude,
        fetchClosest, callback);
  }

  @Override
  public void createNewRequest(RequestBody contractNo, RequestBody categ, RequestBody subCategId,
      RequestBody requestTypeId, RequestBody claimedAmount, RequestBody currencyCode,
      RequestBody serviceDate, RequestBody providerCode, RequestBody remarks,
      Map<String, RequestBody> attachements, final Callback<CreateNewRequestResponse> callback) {
    apiCallsHandler.createNewRequest(contractNo, categ, subCategId, requestTypeId, claimedAmount,
        currencyCode, serviceDate, providerCode, remarks, attachements, callback);
  }

  @Override public void createNewInquiryComplaint(RequestBody contractNo, RequestBody category,
      RequestBody subcategory, RequestBody title, RequestBody area, RequestBody crm_country,
      Map<String, RequestBody> attachements, final Callback<CreateNewRequestResponse> callback) {
    apiCallsHandler.createNewInquiryComplaint(contractNo, category, subcategory, title, area,
        crm_country, attachements, callback);
  }

  @Override public void getCountriesList(final Callback<CountriesListResponse> callback) {
    apiCallsHandler.getCountriesList(callback);
  }

  @Override public void getCRMCategories(RequestBody contractNo,
      final Callback<CRMCategoriesResponse> callback) {
    apiCallsHandler.getCRMCategories(contractNo, callback);
  }

  @Override public void getClaimsList(String contractNo, String requestType,
      final Callback<ClaimsListResponse> callback) {
    apiCallsHandler.getClaimslist(contractNo, requestType, callback);
  }

  @Override public void getClaimslistDetails(String contractNo, String requestType, String claimId,
      final Callback<ClaimListDetailsResponse> callback) {
    apiCallsHandler.getClaimslistDetails(contractNo, requestType, claimId, callback);
  }

  @Override
  public void getChronicTreatmentDetails(String contractNo, String requestType, String claimId,
      final Callback<ChronicTreatmentDetailsResponse> callback) {
    apiCallsHandler.getChronicTreatmentDetails(contractNo, requestType, claimId, callback);
  }

  @Override public void getChronicTreatmentsList(String contractNo, String requestType,
      final Callback<ChronicTreatmentListResponse> callback) {
    apiCallsHandler.getChronicTreatmentsList(contractNo, requestType, callback);
  }

  @Override public void getSnapshot(String contractNo, String period,
      final Callback<CertainPDFResponse> callback) {
    apiCallsHandler.getSnapshot(contractNo, period, callback);
  }

  @Override public void getInquiriesList(String incidentId, String crm_country,
      final Callback<InquiriesListResponse> callback) {
    apiCallsHandler.getInquiriesList(incidentId, crm_country, callback);
  }

  @Override
  public void getCounsellingInformation(final Callback<CounsellingInformationResponse> callback) {
    apiCallsHandler.getCounsellingInformation(callback);
  }

  @Override
  public void getCRMIncidentNotes(String incidentId, final Callback<CRMNotesResponse> callback) {
    apiCallsHandler.getCRMIncidentNotes(incidentId, callback);
  }

  @Override
  public void sendInsurancePasswordResetLink(String email, Callback<DefaultGetResponse> callback) {
    apiCallsHandler.sendInsurancePasswordResetLink(email, callback);
  }

  @Override
  public void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody, final Callback<AddCRMNoteResponse> callback) {
    apiCallsHandler.addCRMNote(incidentId, subject, noteText, mimeType, fileName, documentBody,
        callback);
  }
}
