package com.mcsaatchi.gmfit.insurance.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListResponse;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChronicTrackActivityPresenter extends BaseActivityPresenter {
  private ChronicTrackActivityView view;
  private DataAccessHandler dataAccessHandler;

  public ChronicTrackActivityPresenter(ChronicTrackActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void getChronicTreatmentsList(String contractNo) {
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

  public interface ChronicTrackActivityView extends BaseActivityView {
    void setupActiveTreatmentsList(List<ChronicTreatmentListInnerData> activeTreatments);
  }
}
