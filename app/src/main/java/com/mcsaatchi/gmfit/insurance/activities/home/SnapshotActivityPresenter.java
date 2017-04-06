package com.mcsaatchi.gmfit.insurance.activities.home;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.CertainPDFResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SnapshotActivityPresenter {
  private SnapshotActivityView view;
  private DataAccessHandler dataAccessHandler;

  SnapshotActivityPresenter(SnapshotActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getSnapshot(String period, String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getSnapshot(contractNo, period, new Callback<CertainPDFResponse>() {
      @Override
      public void onResponse(Call<CertainPDFResponse> call, Response<CertainPDFResponse> response) {
        switch (response.code()) {
          case 200:
            view.displaySnapshotPDF(response.body().getData().getBody().getData());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<CertainPDFResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface SnapshotActivityView extends BaseActivityPresenter.BaseActivityView {
    void displaySnapshotPDF(String pdfData);
  }
}
