package com.mcsaatchi.gmfit.insurance.activities.chronic;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicDeletionResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ChronicDeletionActivityPresenter extends BaseActivityPresenter {
  private ChronicDeletionActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ChronicDeletionActivityPresenter(ChronicDeletionActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void deleteChronicTreatment(String contractNo, String requestID, String requestType) {
    view.callDisplayWaitingDialog(R.string.deleting_chronic_treatment_dialog_title);

    dataAccessHandler.deleteChronicTreatment(contractNo, requestID, "4",
        new Callback<ChronicDeletionResponse>() {
          @Override public void onResponse(Call<ChronicDeletionResponse> call,
              Response<ChronicDeletionResponse> response) {
            switch (response.code()) {
              case 200:
                view.successfullyDeletedChronicTreatment();
                break;
            }
            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<ChronicDeletionResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ChronicDeletionActivityView extends BaseActivityView {
    void successfullyDeletedChronicTreatment();
  }
}
