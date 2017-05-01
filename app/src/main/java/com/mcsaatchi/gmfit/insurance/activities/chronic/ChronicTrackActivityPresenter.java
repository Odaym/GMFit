package com.mcsaatchi.gmfit.insurance.activities.chronic;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListResponse;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ChronicTrackActivityPresenter extends BaseActivityPresenter {
  private ChronicTrackActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ChronicTrackActivityPresenter(ChronicTrackActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getChronicTreatmentsList(String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getChronicTreatmentsList(contractNo, "3",
        new Callback<ChronicTreatmentListResponse>() {
          @Override public void onResponse(Call<ChronicTreatmentListResponse> call,
              Response<ChronicTreatmentListResponse> response) {
            switch (response.code()) {
              case 200:
                view.setupActiveTreatmentsList(response.body().getData().getBody().getData());
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }
            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<ChronicTreatmentListResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ChronicTrackActivityView extends BaseActivityView {
    void setupActiveTreatmentsList(List<ChronicTreatmentListInnerData> activeTreatments);
  }
}
