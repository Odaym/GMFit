package com.mcsaatchi.gmfit.health.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AddNewHealthTestPart2ActivityPresenter extends BaseActivityPresenter {
  private AddNewHealthTestPart2ActivityView view;
  private DataAccessHandler dataAccessHandler;

  AddNewHealthTestPart2ActivityPresenter(AddNewHealthTestPart2ActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getTesticularMetrics() {
    view.callDisplayWaitingDialog(R.string.fetching_test_data_dialog_title);

    dataAccessHandler.getTesticularMetrics(new Callback<MedicalTestMetricsResponse>() {
      @Override public void onResponse(Call<MedicalTestMetricsResponse> call,
          Response<MedicalTestMetricsResponse> response) {
        switch (response.code()) {
          case 200:
            view.handleAvailableTestMetrics(
                (ArrayList<MedicalTestMetricsResponseBody>) response.body().getData().getBody());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<MedicalTestMetricsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void storeNewHealthTest(String test_name, String date_taken, Map<String, RequestBody> metrics,
      Map<String, RequestBody> imageFiles) {

    view.callDisplayWaitingDialog(R.string.creating_new_test_dialog_title);

    dataAccessHandler.storeNewHealthTest(Helpers.toRequestBody(test_name),
        Helpers.toRequestBody(date_taken), metrics, imageFiles, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.handleHealthTestCreated();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  void storeEditedHealthTest(int instance_id, String name, String date_taken,
      Map<String, RequestBody> metrics, Map<String, RequestBody> imageFiles,
      RequestBody deletedImages) {

    view.callDisplayWaitingDialog(R.string.editing_existing_test_dialog_title);

    dataAccessHandler.editExistingHealthTest(Helpers.toRequestBody(String.valueOf(instance_id)),
        Helpers.toRequestBody(name), Helpers.toRequestBody(date_taken), metrics, imageFiles,
        deletedImages, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:

                view.handleEditedHealthTestCreated();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface AddNewHealthTestPart2ActivityView extends BaseActivityView {
    void handleAvailableTestMetrics(ArrayList<MedicalTestMetricsResponseBody> metricsFromResponse);

    void handleHealthTestCreated();

    void handleEditedHealthTestCreated();
  }
}
