package com.mcsaatchi.gmfit.insurance.fragments;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import okhttp3.ResponseBody;
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

    dataAccessHandler.getCardDetails(contractNo, new Callback<ResponseBody>() {
      @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        switch (response.code()) {
          case 200:
            view.saveAndOpenPDF(response.body());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface InsuranceHomeFragmentView extends BaseFragmentView {
    void saveAndOpenPDF(ResponseBody responseBody);
  }
}