package com.mcsaatchi.gmfit.insurance.activities.directory;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class DirectorySearchFilterActivityPresenter extends BaseActivityPresenter {
  private DirectorySearchFilterActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  DirectorySearchFilterActivityPresenter(DirectorySearchFilterActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
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

  void getCitiesList() {
    //dataAccessHandler.getCountriesList(new Callback<CountriesListResponse>() {
    //  @Override public void onResponse(Call<CountriesListResponse> call,
    //      Response<CountriesListResponse> response) {
    //    switch (response.code()) {
    //      case 200:
    //        view.populateCountriesDropdown(response.body().getData().getBody().getData());
    //        break;
    //    }
    //  }
    //
    //  @Override public void onFailure(Call<CountriesListResponse> call, Throwable t) {
    //    Timber.d("Call failed with error : %s", t.getMessage());
    //  }
    //});
  }

  interface DirectorySearchFilterActivityView extends BaseActivityView {
    void populateCountriesDropdown(List<CountriesListResponseDatum> countriesResponse);

    //void populateCitiesDropdown(List<CountriesListResponseDatum> countriesResponse);
  }
}
