package com.mcsaatchi.gmfit.insurance.activities.chronic;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentDetailsResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentDetailsResponseMedicationItem;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ChronicDetailsActivityPresenter extends BaseActivityPresenter {
  private ChronicDetailsActivityView view;
  private DataAccessHandler dataAccessHandler;

  ChronicDetailsActivityPresenter(ChronicDetailsActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getChronicTreatmentDetails(String contractNo, String requestNbr) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getChronicTreatmentDetails(contractNo, "3", requestNbr,
        new Callback<ChronicTreatmentDetailsResponse>() {
          @Override public void onResponse(Call<ChronicTreatmentDetailsResponse> call,
              Response<ChronicTreatmentDetailsResponse> response) {
            switch (response.code()) {
              case 200:
                if (response.body().getData().getBody().getData().getItemsList() != null) {
                  view.displayChronicTreatmentDetails(
                      response.body().getData().getBody().getData().getItemsList());
                }
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<ChronicTreatmentDetailsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ChronicDetailsActivityView extends BaseActivityView {
    void displayChronicTreatmentDetails(
        List<ChronicTreatmentDetailsResponseMedicationItem> treatmentItems);
  }
}
