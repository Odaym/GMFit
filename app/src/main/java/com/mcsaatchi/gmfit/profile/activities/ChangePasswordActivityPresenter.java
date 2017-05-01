package com.mcsaatchi.gmfit.profile.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ChangePasswordActivityPresenter {
  private ChangePasswordActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ChangePasswordActivityPresenter(ChangePasswordActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void changePassword(String old_password, String new_password) {
    view.callDisplayWaitingDialog(R.string.change_password_dialog_title);

    dataAccessHandler.changePassword(old_password, new_password,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.displaySuccessToast();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ChangePasswordActivityView extends BaseActivityPresenter.BaseActivityView {
    void displaySuccessToast();
  }
}
