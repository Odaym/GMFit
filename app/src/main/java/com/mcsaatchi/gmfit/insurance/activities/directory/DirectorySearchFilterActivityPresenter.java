package com.mcsaatchi.gmfit.insurance.activities.directory;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CitiesListResponseCityVOArr;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ServicesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ServicesListResponseServiceVOArr;
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

  void getCitiesList(String selectedCtry) {
    dataAccessHandler.getCitiesList(selectedCtry, new Callback<CitiesListResponse>() {
      @Override
      public void onResponse(Call<CitiesListResponse> call, Response<CitiesListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateCitiesDropdown(
                response.body().getData().getBody().getData().getCityVOArr());
            break;
        }
      }

      @Override public void onFailure(Call<CitiesListResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  void getServicesList() {
    dataAccessHandler.getServicesList(new Callback<ServicesListResponse>() {
      @Override public void onResponse(Call<ServicesListResponse> call,
          Response<ServicesListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateServicesDropdown(
                response.body().getData().getBody().getData().getServiceVOArr());
            break;
        }
      }

      @Override public void onFailure(Call<ServicesListResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  interface DirectorySearchFilterActivityView extends BaseActivityView {
    void populateCountriesDropdown(List<CountriesListResponseDatum> countriesResponse);

    void populateCitiesDropdown(List<CitiesListResponseCityVOArr> citiesListResponses);

    void populateServicesDropdown(List<ServicesListResponseServiceVOArr> servicesListResponseServiceVOArrs);
  }
}
