package com.mcsaatchi.gmfit.insurance.activities.forgotpassword;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class UpdateInsuranceDetailsActivityPresenter extends BaseActivityPresenter {
  private UpdatePasswordActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  UpdateInsuranceDetailsActivityPresenter(UpdatePasswordActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void updateInsuranceDetails(String contractNumber, String oldPassword, final String newPassword,
      String email, String mobileNumber) {

    view.callDisplayWaitingDialog(R.string.updating_insurance_details_dialog_title);

    dataAccessHandler.updateInsurancePassword(contractNumber, oldPassword, newPassword, email,
        mobileNumber, new Callback<UpdateInsurancePasswordResponse>() {
          @Override public void onResponse(Call<UpdateInsurancePasswordResponse> call,
              Response<UpdateInsurancePasswordResponse> response) {
            switch (response.code()) {
              case 200:
                //view.sendResultBackAndCloseActivity(newPassword);
                break;
            }
          }

          @Override public void onFailure(Call<UpdateInsurancePasswordResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface UpdatePasswordActivityView extends BaseActivityView {
  }
}
