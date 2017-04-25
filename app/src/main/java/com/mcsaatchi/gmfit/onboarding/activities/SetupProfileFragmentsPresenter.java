package com.mcsaatchi.gmfit.onboarding.activities;

import android.os.Build;
import android.support.annotation.RequiresApi;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.architecture.rest.ActivityLevelsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetupProfileFragmentsPresenter extends BaseFragmentPresenter {
  private SetupProfileFragmentsView_2 view_2;
  private SetupProfileFragmentsView_3 view_3;
  private SetupProfileFragmentsView_4 view_4;
  private DataAccessHandlerImpl dataAccessHandler;

  public SetupProfileFragmentsPresenter(SetupProfileFragmentsView_2 view_2,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view_2 = view_2;
    this.dataAccessHandler = dataAccessHandler;
  }

  public SetupProfileFragmentsPresenter(SetupProfileFragmentsView_3 view_3,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view_3 = view_3;
    this.dataAccessHandler = dataAccessHandler;
  }

  public SetupProfileFragmentsPresenter(SetupProfileFragmentsView_4 view_4,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view_4 = view_4;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void getUserGoals() {
    dataAccessHandler.getUserGoals(new Callback<UserGoalsResponse>() {
      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
      public void onResponse(Call<UserGoalsResponse> call, Response<UserGoalsResponse> response) {
        switch (response.code()) {
          case 200:

            List<UserGoalsResponseBody> userGoals = response.body().getData().getBody();

            view_2.populateUserGoals(userGoals);
            break;
        }
      }

      @Override public void onFailure(Call<UserGoalsResponse> call, Throwable t) {
        view_2.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void getActivityLevels() {
    dataAccessHandler.getActivityLevels(new Callback<ActivityLevelsResponse>() {
      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
      public void onResponse(Call<ActivityLevelsResponse> call,
          Response<ActivityLevelsResponse> response) {
        switch (response.code()) {
          case 200:

            List<ActivityLevelsResponseBody> activityLevels = response.body().getData().getBody();

            view_3.populateActivityLevels(activityLevels);
            break;
        }
      }

      @Override public void onFailure(Call<ActivityLevelsResponse> call, Throwable t) {
        view_3.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  public void getMedicalConditions() {
    dataAccessHandler.getMedicalConditions(new Callback<MedicalConditionsResponse>() {
      @Override public void onResponse(Call<MedicalConditionsResponse> call,
          Response<MedicalConditionsResponse> response) {
        switch (response.code()) {
          case 200:
            ArrayList<MedicalConditionsResponseDatum> allMedicalData =
                (ArrayList<MedicalConditionsResponseDatum>) response.body()
                    .getData()
                    .getBody()
                    .getData();

            view_4.populateMedicalConditionsSpinner(allMedicalData);

            break;
          case 401:

            break;
        }
      }

      @Override public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {

      }
    });
  }

  public void setupUserProfile(RequestBody finalDateOfBirth, RequestBody bloodType,
      RequestBody nationality, HashMap<String, RequestBody> medicalConditions,
      RequestBody measurementSystem, RequestBody goalId, RequestBody activityLevelId,
      RequestBody finalGender, RequestBody height, RequestBody weight) {

    view_4.callDisplayWaitingDialog(R.string.signing_up_dialog_title);

    dataAccessHandler.updateUserProfile(finalDateOfBirth, bloodType, nationality, medicalConditions,
        measurementSystem, goalId, activityLevelId, finalGender, height, weight,
        Helpers.toRequestBody("1"), new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                getUiForSection("fitness");
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view_4.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  private void getUiForSection(String section) {
    dataAccessHandler.getUiForSection(Constants.BASE_URL_ADDRESS + "user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                view_4.callDismissWaitingDialog();

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                view_4.openMainActivity(chartsMap);

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            view_4.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  public interface SetupProfileFragmentsView_2 extends BaseFragmentView {
    void populateUserGoals(List<UserGoalsResponseBody> userGoals);
  }

  public interface SetupProfileFragmentsView_3 extends BaseFragmentView {
    void populateActivityLevels(List<ActivityLevelsResponseBody> activityLevels);
  }

  public interface SetupProfileFragmentsView_4 extends BaseFragmentView {
    void populateMedicalConditionsSpinner(ArrayList<MedicalConditionsResponseDatum> allMedicalData);

    void openMainActivity(List<AuthenticationResponseChart> chartsMap);
  }
}
