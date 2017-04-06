package com.mcsaatchi.gmfit.health.fragments;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.WeightHistoryResponse;
import com.mcsaatchi.gmfit.architecture.rest.WeightHistoryResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponseDatum;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import com.mcsaatchi.gmfit.health.models.Medication;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class HealthFragmentPresenter extends BaseFragmentPresenter {
  private HealthFragmentView view;
  private RuntimeExceptionDao<Medication, Integer> medicationDAO;
  private DataAccessHandler dataAccessHandler;

  HealthFragmentPresenter(HealthFragmentView view,
      RuntimeExceptionDao<Medication, Integer> medicationDAO, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.medicationDAO = medicationDAO;
    this.dataAccessHandler = dataAccessHandler;
  }

  List<Medication> getMedicationsFromDB() {
    return medicationDAO.queryForAll();
  }

  void getWidgets() {
    dataAccessHandler.getWidgets("medical", new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        switch (response.code()) {
          case 200:
            List<WidgetsResponseDatum> widgetsFromResponse =
                response.body().getData().getBody().getData();

            ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();

            for (int i = 0; i < widgetsFromResponse.size(); i++) {
              HealthWidget widget = new HealthWidget();

              widget.setId(widgetsFromResponse.get(i).getWidgetId());
              widget.setMeasurementUnit(widgetsFromResponse.get(i).getUnit());
              widget.setPosition(i);
              widget.setValue(Float.parseFloat(widgetsFromResponse.get(i).getTotal()));
              widget.setTitle(widgetsFromResponse.get(i).getName());
              widget.setSlug(widgetsFromResponse.get(i).getSlug());

              healthWidgetsMap.add(widget);
            }

            view.setupWidgetViews(healthWidgetsMap);

            break;
        }
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getTakenMedicalTests() {
    dataAccessHandler.getTakenMedicalTests(new Callback<TakenMedicalTestsResponse>() {
      @Override public void onResponse(Call<TakenMedicalTestsResponse> call,
          Response<TakenMedicalTestsResponse> response) {
        switch (response.code()) {
          case 200:
            List<TakenMedicalTestsResponseBody> takenMedicalTests =
                response.body().getData().getBody();

            view.displayTakenMedicalTests(takenMedicalTests);

            break;
        }
      }

      @Override public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getUserWeight() {
    dataAccessHandler.getUserProfile(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        switch (response.code()) {
          case 200:
            UserProfileResponseDatum userProfileData =
                response.body().getData().getBody().getData();

            view.saveAndDisplayUserWeight(userProfileData);

            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void setupUserWeightChart() {
    dataAccessHandler.getUserWeightHistory(new Callback<WeightHistoryResponse>() {
      @Override public void onResponse(Call<WeightHistoryResponse> call,
          Response<WeightHistoryResponse> response) {
        switch (response.code()) {
          case 200:
            List<WeightHistoryResponseDatum> weightHistoryList =
                response.body().getData().getBody().getData();

            view.displayUserWeightChart(weightHistoryList);

            break;
        }
      }

      @Override public void onFailure(Call<WeightHistoryResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface HealthFragmentView extends BaseFragmentView {
    void setupWidgetViews(ArrayList<HealthWidget> healthWidgetsMap);

    void displayTakenMedicalTests(List<TakenMedicalTestsResponseBody> takenMedicalTests);

    void saveAndDisplayUserWeight(UserProfileResponseDatum userProfileData);

    void displayUserWeightChart(List<WeightHistoryResponseDatum> weightHistoryList);
  }
}
