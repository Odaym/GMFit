package com.mcsaatchi.gmfit.insurance.fragments;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class InsuranceFragmentPresenter extends BaseFragmentPresenter {
  private InsuranceFragmentView view;
  private DataAccessHandlerImpl dataAccessHandler;

  InsuranceFragmentPresenter(InsuranceFragmentView view, DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void login(String username, String countryCode, String language, String password) {
    view.callDisplayWaitingDialog(R.string.signing_in_dialog_title);

    dataAccessHandler.insuranceUserLogin(username, countryCode, language, password,
        new Callback<InsuranceLoginResponse>() {
          @Override public void onResponse(Call<InsuranceLoginResponse> call,
              Response<InsuranceLoginResponse> response) {
            switch (response.code()) {
              case 200:
                view.openHomeFragment(response.body().getData().getBody().getData(), username);
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface InsuranceFragmentView extends BaseFragmentView {
    void openHomeFragment(InsuranceLoginResponseInnerData userObject, String username);
  }
}
