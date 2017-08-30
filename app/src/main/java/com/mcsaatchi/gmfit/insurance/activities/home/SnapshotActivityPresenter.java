package com.mcsaatchi.gmfit.insurance.activities.home;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SnapshotActivityPresenter {
  private SnapshotActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SnapshotActivityPresenter(SnapshotActivityView view, DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getSnapshot(String period, String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getSnapshot(contractNo, period, new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        switch (response.code()) {
          case 200:
            view.saveAndOpenPDF(response.body(), "Snapshot.pdf");
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface SnapshotActivityView extends BaseActivityPresenter.BaseActivityView {
    void saveAndOpenPDF(ResponseBody responseBody, String pdfData);
  }
}
