package com.mcsaatchi.gmfit.health.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CounsellingInformationResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AddExistingMedicationActivityPresenter extends BaseActivityPresenter {
  private AddExistingMedicationActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  AddExistingMedicationActivityPresenter(AddExistingMedicationActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getCounsellingInformation(String medCode) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getCounsellingInformation(medCode, new Callback<CounsellingInformationResponse>() {
      @Override public void onResponse(Call<CounsellingInformationResponse> call,
          Response<CounsellingInformationResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayCounsellingInformation(
                response.body().getData().getBody().getData().getCompatibilityCheckDesc());
            break;
          case 449:
            view.displayRequestErrorDialog(
                Helpers.provideErrorStringFromJSON(response.errorBody()));
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<CounsellingInformationResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface AddExistingMedicationActivityView extends BaseActivityView {
    void displayCounsellingInformation(String compatibilityDescription);
  }
}
