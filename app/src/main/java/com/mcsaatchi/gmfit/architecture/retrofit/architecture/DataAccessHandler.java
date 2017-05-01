package com.mcsaatchi.gmfit.architecture.retrofit.architecture;

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
import okhttp3.RequestBody;
import retrofit2.Callback;

public interface DataAccessHandler {
  void findMeals(String mealName, Callback<SearchMealItemResponse> callback);

  void searchForMealBarcode(String barcode, Callback<SearchMealItemResponse> callback);

  void getSlugBreakdownForChart(final String chartType,
      final Callback<SlugBreakdownResponse> callback);

  void refreshAccessToken(Callback<AuthenticationResponse> callback);

  void synchronizeMetricsWithServer(String[] slugsArray, Number[] valuesArray,
      Callback<DefaultGetResponse> callback);

  void signInUser(String email, String password, Callback<AuthenticationResponse> callback);

  void registerUser(String full_name, String email, String password,
      Callback<AuthenticationResponse> callback);

  void signInUserSilently(String email, String password,
      Callback<AuthenticationResponse> callback);

  void signOutUser(Callback<DefaultGetResponse> callback);

  void updateUserProfile(RequestBody finalDateOfBirth, RequestBody bloodType,
      RequestBody nationality, HashMap<String, RequestBody> medicalConditions,
      RequestBody measurementSystem, RequestBody goalId, RequestBody activityLevelId,
      RequestBody finalGender, RequestBody height, RequestBody weight, RequestBody onboard,
      Callback<DefaultGetResponse> callback);

  void updateUserProfileExplicitly(RequestBody name, RequestBody phone_number,
      RequestBody gender, RequestBody date_of_birth, RequestBody blood_type, RequestBody height,
      RequestBody weight, Callback<DefaultGetResponse> callback);

  void updateUserWeight(double weight, String created_at,
      Callback<DefaultGetResponse> callback);

  void updateOneSignalToken(String onesignal_id, Callback<DefaultGetResponse> callback);

  void updateUserPicture(Map<String, RequestBody> profilePicture,
      Callback<DefaultGetResponse> callback);

  void getUiForSection(String section, Callback<UiResponse> callback);

  void getMedicalConditions(Callback<MedicalConditionsResponse> callback);

  void sendResetPasswordLink(String email, Callback<DefaultGetResponse> callback);

  void changePassword(String old_password, String new_password,
      Callback<DefaultGetResponse> callback);

  void finalizeResetPassword(String resetPasswordToken, String newPassword,
      Callback<DefaultGetResponse> callback);

  void getMealMetrics(String fullUrl, Callback<MealMetricsResponse> callback);

  void getUserGoalMetrics(String date, String type,
      Callback<UserGoalMetricsResponse> callback);

  void verifyUser(String verificationCode, Callback<DefaultGetResponse> callback);

  void updateUserWidgets(int[] widgetIds, int[] widgetPositions,
      Callback<DefaultGetResponse> callback);

  void updateUserCharts(int[] chartIds, int[] chartPositions,
      Callback<DefaultGetResponse> callback);

  void updateUserMeals(int instance_id, float amount,
      Callback<DefaultGetResponse> callback);

  void deleteUserTest(int instance_id, Callback<DefaultGetResponse> callback);

  void deleteUserMeal(int instance_id, Callback<DefaultGetResponse> callback);

  void getUserAddedMeals(Callback<UserMealsResponse> callback);

  void getUserAddedMealsOnDate(String chosenDate, Callback<UserMealsResponse> callback);

  void getMetaTexts(String section, Callback<MetaTextsResponse> callback);

  void handleFacebookProcess(String facebookAccessToken,
      Callback<AuthenticationResponse> callback);

  void storeNewMeal(int meal_id, float servingsAmount, String when, String date,
      Callback<DefaultGetResponse> callback);

  void getRecentMeals(String fullUrl, Callback<RecentMealsResponse> callback);

  void getPeriodicalChartData(String start_date, String end_date, String type,
      String monitored_metric, Callback<ChartMetricBreakdownResponse> callback);

  void addMetricChart(int chart_id, Callback<DefaultGetResponse> callback);

  void getChartsBySection(String sectionName, Callback<ChartsBySectionResponse> callback);

  void requestNewMeal(String mealName, Callback<DefaultGetResponse> callback);

  void getTesticularMetrics(Callback<MedicalTestMetricsResponse> callback);

  void getMedicalTests(Callback<MedicalTestsResponse> callback);

  void getWidgets(String sectionName, Callback<WidgetsResponse> callback);

  void getWidgetsWithDate(String sectionName, String date,
      Callback<WidgetsResponse> callback);

  void storeNewHealthTest(RequestBody test_name, RequestBody date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      Callback<DefaultGetResponse> callback);

  void editExistingHealthTest(RequestBody instance_id, RequestBody name,
      RequestBody date_taken, Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      RequestBody deletedImages, Callback<DefaultGetResponse> callback);

  void getTakenMedicalTests(Callback<TakenMedicalTestsResponse> callback);

  void getOnboardingStatus(Callback<UserProfileResponse> callback);

  void getUserProfile(Callback<UserProfileResponse> callback);

  void getEmergencyProfile(Callback<EmergencyProfileResponse> callback);

  void deleteUserChart(String chart_id, Callback<DefaultGetResponse> callback);

  void getActivityLevels(Callback<ActivityLevelsResponse> callback);

  void getUserWeightHistory(Callback<WeightHistoryResponse> callback);

  void getUserGoals(Callback<UserGoalsResponse> callback);

  /**
   * INSURANCE API's
   */
  void getMostPopularMedications(String indNbr, String contractNo, String country,
      String language, String password, Callback<MostPopularMedicationsResponse> callback);

  void searchMedicines(String indNbr, String contractNo, String country, String language,
      String password, String key, Callback<SearchMedicinesResponse> callback);

  void insuranceUserLogin(String indNbr, String country, String language, String password,
      Callback<InsuranceLoginResponse> callback);

  void getCoverageDescription(String contractNo, String indNbr,
      Callback<CertainPDFResponse> callback);

  void getMembersGuide(String contractNo, String indNbr,
      Callback<CertainPDFResponse> callback);

  void updateInsurancePassword(String contractNo, String oldPassword, String newPassword,
      String email, String mobileNumber, Callback<UpdateInsurancePasswordResponse> callback);

  void getCardDetails(String contractNo, Callback<CertainPDFResponse> callback);

  void getSubCategories(String contractNo, Callback<SubCategoriesResponse> callback);

  void getNearbyClinics(String contractNo, String providerTypesCode, int searchCtry,
      double longitude, double latitude, int fetchClosest,
      Callback<GetNearbyClinicsResponse> callback);

  void createNewRequest(RequestBody contractNo, RequestBody categ, RequestBody subCategId,
      RequestBody requestTypeId, RequestBody claimedAmount, RequestBody currencyCode,
      RequestBody serviceDate, RequestBody providerCode, RequestBody remarks,
      Map<String, RequestBody> attachements, final Callback<CreateNewRequestResponse> callback);

  void createNewInquiryComplaint(RequestBody contractNo, RequestBody category,
      RequestBody subcategory, RequestBody title, RequestBody area, RequestBody crm_country,
      Map<String, RequestBody> attachements, final Callback<CreateNewRequestResponse> callback);

  void getCountriesList(final Callback<CountriesListResponse> callback);

  void getCRMCategories(RequestBody contractNo,
      final Callback<CRMCategoriesResponse> callback);

  void getClaimsList(String contractNo, String requestType,
      final Callback<ClaimsListResponse> callback);

  void getClaimslistDetails(String contractNo, String requestType, String claimId,
      final Callback<ClaimListDetailsResponse> callback);

  void getChronicTreatmentDetails(String contractNo, String requestType, String claimId,
      final Callback<ChronicTreatmentDetailsResponse> callback);

  void getChronicTreatmentsList(String contractNo, String requestType,
      final Callback<ChronicTreatmentListResponse> callback);

  void getSnapshot(String contractNo, String period,
      final Callback<CertainPDFResponse> callback);

  void getInquiriesList(String incidentId, String crm_country,
      final Callback<InquiriesListResponse> callback);

  void getCounsellingInformation(final Callback<CounsellingInformationResponse> callback);

  void getCRMIncidentNotes(String incidentId, final Callback<CRMNotesResponse> callback);

  void sendInsurancePasswordResetLink(String email, Callback<DefaultGetResponse> callback);

  void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody, final Callback<AddCRMNoteResponse> callback);
}
