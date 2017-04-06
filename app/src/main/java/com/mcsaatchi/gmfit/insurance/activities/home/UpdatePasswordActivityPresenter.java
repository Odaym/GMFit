package com.mcsaatchi.gmfit.insurance.activities.home;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class UpdatePasswordActivityPresenter extends BaseActivityPresenter {
  private UpdatePasswordActivityView view;
  private DataAccessHandler dataAccessHandler;

  UpdatePasswordActivityPresenter(UpdatePasswordActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void updateInsurancePassword(String contractNumber, String oldPassword, final String newPassword,
      String email, String mobileNumber) {

    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.updateInsurancePassword(contractNumber, oldPassword, newPassword, email,
        mobileNumber, new Callback<UpdateInsurancePasswordResponse>() {
          @Override public void onResponse(Call<UpdateInsurancePasswordResponse> call,
              Response<UpdateInsurancePasswordResponse> response) {
            switch (response.code()) {
              case 200:
                view.sendResultBackAndCloseActivity(newPassword);
                break;
            }
          }

          @Override public void onFailure(Call<UpdateInsurancePasswordResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface UpdatePasswordActivityView extends BaseActivityView {
    void sendResultBackAndCloseActivity(String newPassword);
  }
}
