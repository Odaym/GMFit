package com.mcsaatchi.gmfit.profile.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivityPresenter {
  private ChangePasswordActivityView view;
  private DataAccessHandler dataAccessHandler;

  public ChangePasswordActivityPresenter(ChangePasswordActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void changePassword(String old_password, String new_password) {
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

  public interface ChangePasswordActivityView extends BaseActivityPresenter.BaseActivityView {
    void displaySuccessToast();
  }
}
