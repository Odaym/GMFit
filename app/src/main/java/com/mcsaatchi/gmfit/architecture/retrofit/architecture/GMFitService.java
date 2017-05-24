package com.mcsaatchi.gmfit.architecture.retrofit.architecture;

import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AddCRMNoteResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMNotesResponse;
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
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SlugBreakdownResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UiResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserActivitiesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalMetricsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserMealsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WeightHistoryResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WidgetsResponse;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GMFitService {

  @POST("login") Call<AuthenticationResponse> signInUser(
      @Body ApiCallsHandler.SignInRequest userCredentails);

  @POST("login") Call<AuthenticationResponse> signInUserSilently(
      @Body ApiCallsHandler.SignInRequest userCredentails);

  @POST("register") Call<AuthenticationResponse> registerUser(
      @Body ApiCallsHandler.RegisterRequest userCredentials);

  @GET("logout") Call<DefaultGetResponse> signOutUser();

  @Multipart @POST("user/update-profile") Call<DefaultGetResponse> updateUserProfile(
      @Part("date_of_birth") RequestBody date_of_birth, @Part("blood_type") RequestBody blood_type,
      @Part("country") RequestBody country, @Part("metric_system") RequestBody metric_system,
      @PartMap() Map<String, RequestBody> medical_conditions,
      @Part("user_goal") RequestBody user_goal, @Part("activity_level") RequestBody activity_level,
      @Part("gender") RequestBody gender, @Part("height") RequestBody height,
      @Part("weight") RequestBody weight, @Part("onboard") RequestBody onboard);

  @Multipart @POST("user/update-profile") Call<DefaultGetResponse> updateUserProfileExplicitly(
      @Part("name") RequestBody name, @Part("phone_number") RequestBody phone_number,
      @Part("gender") RequestBody gender, @Part("date_of_birth") RequestBody date_of_birth,
      @Part("blood_type") RequestBody blood_type, @Part("height") RequestBody height,
      @Part("weight") RequestBody weight);

  @POST("user/update-profile") Call<DefaultGetResponse> updateUserWeight(
      @Body ApiCallsHandler.UpdateUserWeightRequest updateUserWeightRequest);

  @POST("user/update-profile") Call<DefaultGetResponse> updateOneSignalToken(
      @Body ApiCallsHandler.UpdateOneSignalRequest updateOneSignalRequest);

  @Multipart @POST("user/profile/picture") Call<DefaultGetResponse> updateUserPicture(
      @PartMap() Map<String, RequestBody> profilePicture);

  @GET Call<UiResponse> getUiForSection(@Url String fullUrl);

  @GET("refresh-token") Call<AuthenticationResponse> refreshAccessToken();

  @POST("user/add-metric") Call<DefaultGetResponse> updateMetrics(
      @Body ApiCallsHandler.UpdateMetricsRequest updateMetricsRequest);

  @POST("user/fitness_activities/add") Call<DefaultGetResponse> addFitnessActivity(
      @Body ApiCallsHandler.AddFitnessActivityRequest addFitnessActivityRequest);

  @POST("user/fitness_activities/edit") Call<DefaultGetResponse> editFitnessActivity(
      @Body ApiCallsHandler.EditFitnessActivityRequest editFitnessActivityRequest);

  @POST("user/fitness_activities/delete") Call<DeleteActivityResponse> deleteFitnessActivity(
      @Body ApiCallsHandler.DeleteFitnessActivityRequest deleteFitnessActivityRequest);

  @GET("articles") Call<ArticlesResponse> getArticles(@Query("section") String sectionName);

  @GET("user/metrics/range") Call<ChartMetricBreakdownResponse> getPeriodicalChartData(
      @Query("start_date") String start_date, @Query("end_date") String end_date,
      @Query("type") String type, @Query("monitored_metrics") String monitored_metrics);

  @GET("user/goals/metrics") Call<UserGoalMetricsResponse> getUserGoalMetrics(
      @Query("date") String start_date, @Query("type") String type);

  @GET("meta") Call<MetaTextsResponse> getMetaTexts(@Query("section") String section);

  @POST("verify") Call<DefaultGetResponse> verifyRegistrationCode(
      @Body ApiCallsHandler.VerificationRequest verificationRequest);

  @GET("medical-conditions") Call<MedicalConditionsResponse> getMedicalConditions();

  @POST("facebook") Call<AuthenticationResponse> handleFacebookProcess(
      @Body ApiCallsHandler.HandleFacebookRequest handleFacebookRequest);

  @POST("forgot-password") Call<DefaultGetResponse> sendResetPasswordLink(
      @Body ApiCallsHandler.ForgotPasswordRequest sendResetPasswordLinkRequest);

  @POST("user/change-password") Call<DefaultGetResponse> changePassword(
      @Body ApiCallsHandler.ChangePasswordRequest changePasswordRequests);

  @POST("forgot-password-change") Call<DefaultGetResponse> finalizeResetPassword(
      @Body ApiCallsHandler.ResetPasswordRequest resetPasswordRequest);

  @GET Call<SlugBreakdownResponse> getBreakdownForSlug(@Url String finalURL);

  @POST("user/widgets/update") Call<DefaultGetResponse> updateUserWidgets(
      @Body ApiCallsHandler.UpdateWidgetsRequest updateWidgetsRequest);

  @POST("user/charts/update") Call<DefaultGetResponse> updateUserCharts(
      @Body ApiCallsHandler.UpdateChartsRequest updateChartsRequest);

  @POST("user/meals/update") Call<DefaultGetResponse> updateUserMeals(
      @Body ApiCallsHandler.UpdateMealsRequest updateMealsRequest);

  @POST("user/charts/add") Call<DefaultGetResponse> addMetricChart(
      @Body ApiCallsHandler.AddMetricChartRequest addMetricChartRequest);

  @GET("user/meals") Call<UserMealsResponse> getUserAddedMeals();

  @GET("user/meals") Call<UserMealsResponse> getUserAddedMealsOnDate(
      @Query("start_date") String start_date, @Query("end_date") String end_date);

  @GET Call<ChartsBySectionResponse> getChartsBySection(@Url String fullUrl);

  @GET("meals") Call<SearchMealItemResponse> searchForMeals(@Query("name") String mealName);

  @GET("meals") Call<SearchMealItemResponse> searchForMealBarcode(@Query("barcode") String barcode);

  @GET Call<MealMetricsResponse> getMealMetrics(@Url String fullUrl);

  @POST("user/meals/store") Call<DefaultGetResponse> storeNewMeal(
      @Body ApiCallsHandler.StoreNewMealRequest storeNewMealRequest);

  @POST("user/meals/delete") Call<DefaultGetResponse> deleteUserMeal(
      @Body ApiCallsHandler.DeleteMealRequest deleteMealRequest);

  @POST("user/medical/delete") Call<DefaultGetResponse> deleteUserTest(
      @Body ApiCallsHandler.DeleteTestRequest deleteTestRequest);

  @GET Call<RecentMealsResponse> getRecentMeals(@Url String fullUrl);

  @POST("user/meals/request") Call<DefaultGetResponse> requestNewMeal(
      @Body ApiCallsHandler.RequestMealRequest requestMealRequest);

  @GET("medical-tests") Call<MedicalTestsResponse> getMedicalTests();

  @GET("user/medical/metrics") Call<MedicalTestMetricsResponse> getTesticularMetrics();

  @GET("user/widgets") Call<WidgetsResponse> getWidgets(@Query("section") String sectionName);

  @GET("user/widgets") Call<WidgetsResponse> getWidgetsWithDate(
      @Query("section") String sectionName, @Query("date") String date);

  @Multipart @POST("user/medical/store") Call<DefaultGetResponse> storeNewHealthTest(
      @Part("name") RequestBody test_name, @Part("date_taken") RequestBody date_taken,
      @PartMap() Map<String, RequestBody> metrics, @PartMap() Map<String, RequestBody> imageFiles);

  @Multipart @POST("user/medical/edit") Call<DefaultGetResponse> editExistingHealthTest(
      @Part("instance_id") RequestBody instance_id, @Part("name") RequestBody test_name,
      @Part("date_taken") RequestBody date_taken, @PartMap() Map<String, RequestBody> metrics,
      @PartMap() Map<String, RequestBody> imageFiles,
      @Part("delete_image_ids") RequestBody deletedImages);

  @GET("user/medical") Call<TakenMedicalTestsResponse> getTakenMedicalTests();

  @GET("user/profile") Call<UserProfileResponse> getOnboardingStatus();

  @GET("user/profile") Call<UserProfileResponse> getUserProfile();

  @GET("operations") Call<OperationContactsResponse> getOperationContacts();

  @POST("user/charts/delete") Call<DefaultGetResponse> deleteUserChart(
      @Body ApiCallsHandler.DeleteUserChartRequest chart_id);

  @GET("user/fitness_activities") Call<UserActivitiesResponse> getUserActivities();

  @GET("fitness_activities") Call<ActivitiesListResponse> getAllActivities();

  @GET("activity_levels") Call<ActivityLevelsResponse> getActivityLevels();

  @GET("user_goals") Call<UserGoalsResponse> getUserGoals();

  @GET("user/weight/history") Call<WeightHistoryResponse> getUserWeightHistory();

  /**
   * INSURANCE API's
   */
  //HOME SCREEN
  @POST("insurance/insurance-card-details") Call<ResponseBody> getCardDetails(
      @Body ApiCallsHandler.SimpleInsuranceRequest simpleInsuranceRequest);

  @POST("insurance/login") Call<InsuranceLoginResponse> insuranceUserLogin(
      @Body ApiCallsHandler.InsuranceLoginRequest insuranceUserLoginRequest);

  @POST("insurance/coverage") Call<ResponseBody> getCoverageDescription(
      @Body ApiCallsHandler.CoverageDescriptionRequest coverageDescriptionRequest);

  @POST("insurance/guide/pdf") Call<ResponseBody> getMembersGuide(
      @Body ApiCallsHandler.CoverageDescriptionRequest coverageDescriptionRequest);

  @POST("insurance/update-info") Call<UpdateInsurancePasswordResponse> updateInsurancePassword(
      @Body ApiCallsHandler.UpdateInsurancePasswordRequest updateInsurancePasswordRequest);

  @POST("insurance/network/advanced-search") Call<GetNearbyClinicsResponse> getNearbyClinics(
      @Body ApiCallsHandler.NearbyClinicsRequest simpleInsuranceRequest);

  @POST("insurance/snapshot/pdf") Call<ResponseBody> getSnapshot(
      @Body ApiCallsHandler.SnapShotRequest snapShotRequest);

  @POST("insurance/countries") Call<CountriesListResponse> getCountriesList();
  //HOME SCREEN

  // MEDICINES
  @POST("insurance/medecines/most-popular")
  Call<MostPopularMedicationsResponse> getMostPopularMedications(
      @Body ApiCallsHandler.DefaultBodyForInsuranceRequests mostPopularMedicationsRequest);

  @POST("insurance/medecines/search") Call<SearchMedicinesResponse> searchMedicines(
      @Body ApiCallsHandler.SearchMedicinesRequest searchMedicinesRequest);
  // MEDICINES

  // SUBMISSIONS AND LISTINGS
  @POST("insurance/sub-categories") Call<SubCategoriesResponse> getSubCategories(
      @Body ApiCallsHandler.SimpleInsuranceRequest simpleInsuranceRequest);

  @Multipart @POST("insurance/request/create") Call<CreateNewRequestResponse> createNewRequest(
      @Part("contractNo") RequestBody contractNo, @Part("categ") RequestBody categ,
      @Part("subCategId") RequestBody subCategId, @Part("requestTypeId") RequestBody requestTypeId,
      @Part("claimedAmount") RequestBody claimedAmount,
      @Part("currencyCode") RequestBody currencyCode, @Part("serviceDate") RequestBody serviceDate,
      @Part("providerCode") RequestBody providerCode, @Part("remarks") RequestBody remarks,
      @PartMap() Map<String, RequestBody> attachements);

  @Multipart @POST("insurance/crm/request/create")
  Call<CreateNewRequestResponse> createNewInquiryComplaint(
      @Part("contractNo") RequestBody contractNo, @Part("category") RequestBody category,
      @Part("subcategory") RequestBody subcategory, @Part("title") RequestBody title,
      @Part("area") RequestBody area, @Part("crm_country") RequestBody crm_country,
      @PartMap() Map<String, RequestBody> attachements);

  @Multipart @POST("insurance/crm/categories") Call<CRMCategoriesResponse> getCRMCategories(
      @Part("contractNo") RequestBody contractNo);

  @POST("insurance/claims/list") Call<ClaimsListResponse> getClaimsList(
      @Body ApiCallsHandler.ClaimsListRequest claimsListRequest);

  @POST("insurance/claims/list") Call<ChronicTreatmentDetailsResponse> getChronicTreatmentDetails(
      @Body ApiCallsHandler.ClaimsListDetailsRequest claimsListRequest);

  @POST("insurance/claims/list") Call<ClaimListDetailsResponse> getClaimsListDetails(
      @Body ApiCallsHandler.ClaimsListDetailsRequest claimsListDetailsRequest);

  @POST("insurance/claims/list") Call<ChronicTreatmentListResponse> getChronicTreatmentsList(
      @Body ApiCallsHandler.ClaimsListRequest chronicListDetailsRequest);

  @POST("insurance/crm/request/get") Call<InquiriesListResponse> getInquiriesList(
      @Body ApiCallsHandler.InquiriesListRequest inquiriesListRequest);

  @POST("insurance/expert-system") Call<CounsellingInformationResponse> getCounsellingInformation();

  @POST("insurance/crm/request/notes") Call<CRMNotesResponse> getCRMIncidentNotes(
      @Body ApiCallsHandler.CRMNotesRequest crmNotesRequest);

  @POST("insurance/crm/request/notes/add") Call<AddCRMNoteResponse> addCRMNote(
      @Body ApiCallsHandler.AddCRMNoteRequest crmNotesRequest);

  @POST("insurance/forgot_password") Call<DefaultGetResponse> sendInsurancePasswordResetLink(
      @Body ApiCallsHandler.ForgotPasswordRequest sendResetPasswordLinkRequest);
  // SUBMISSIONS AND LISTINGS
}
