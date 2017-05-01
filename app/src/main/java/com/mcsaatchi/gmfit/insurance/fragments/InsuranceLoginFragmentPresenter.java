package com.mcsaatchi.gmfit.insurance.fragments;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class InsuranceLoginFragmentPresenter extends BaseFragmentPresenter {
  private InsuranceLoginFragmentView view;
  private DataAccessHandlerImpl dataAccessHandler;

  InsuranceLoginFragmentPresenter(InsuranceLoginFragmentView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void login(String indNbr, String country, String language, String password) {
    view.callDisplayWaitingDialog(R.string.signing_in_dialog_title);

    dataAccessHandler.insuranceUserLogin(indNbr, country, language, password,
        new Callback<InsuranceLoginResponse>() {
          @Override public void onResponse(Call<InsuranceLoginResponse> call,
              Response<InsuranceLoginResponse> response) {
            switch (response.code()) {
              case 200:
                view.saveInsuranceCredentials(indNbr, password);

                if (response.body().getData().getBody().getData().getIsFirstLogin()) {
                  view.openUpdatePasswordActivity(response.body().getData().getBody().getData());
                } else {
                  view.openHomeFragment(response.body().getData().getBody().getData(), indNbr);
                }
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

  void getCountriesList() {
    dataAccessHandler.getCountriesList(new Callback<CountriesListResponse>() {
      @Override public void onResponse(Call<CountriesListResponse> call,
          Response<CountriesListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateCountriesDropdown(response.body().getData().getBody().getData());
            break;
        }
      }

      @Override public void onFailure(Call<CountriesListResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  interface InsuranceLoginFragmentView extends BaseFragmentView {
    void saveInsuranceCredentials(String memberId, String password);

    void openUpdatePasswordActivity(InsuranceLoginResponseInnerData userObject);

    void openHomeFragment(InsuranceLoginResponseInnerData userObject, String memberId);

    void populateCountriesDropdown(List<CountriesListResponseDatum> countriesResponse);
  }
}
