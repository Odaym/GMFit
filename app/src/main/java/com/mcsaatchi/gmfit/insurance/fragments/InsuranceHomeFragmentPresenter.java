package com.mcsaatchi.gmfit.insurance.fragments;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.CertainPDFResponse;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class InsuranceHomeFragmentPresenter extends BaseFragmentPresenter {
  private InsuranceHomeFragmentView view;
  private DataAccessHandlerImpl dataAccessHandler;

  InsuranceHomeFragmentPresenter(InsuranceHomeFragmentView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getCardDetails(String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getCardDetails(contractNo, new Callback<CertainPDFResponse>() {
      @Override
      public void onResponse(Call<CertainPDFResponse> call, Response<CertainPDFResponse> response) {
        switch (response.code()) {
          case 200:
            view.openPDFViewerActivity(response.body().getData().getBody().getData());
            break;
          case 449:
            view.displayRequestErrorDialog(
                Helpers.provideErrorStringFromJSON(response.errorBody()));
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<CertainPDFResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface InsuranceHomeFragmentView extends BaseFragmentView {
    void openPDFViewerActivity(String PDF_file);
  }
}