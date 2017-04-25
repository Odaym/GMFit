package com.mcsaatchi.gmfit.health.activities;

import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.MostPopularMedicationsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MostPopularMedicationsResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.SearchMedicinesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SearchMedicinesResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SearchMedicationsActivityPresenter extends BaseActivityPresenter {
  private SearchMedicationsActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SearchMedicationsActivityPresenter(SearchMedicationsActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void searchMedicines(String indNbr, String contractNo, String country, String language,
      String password, String key) {
    dataAccessHandler.searchMedicines(indNbr, contractNo, country, language, password, key,
        new Callback<SearchMedicinesResponse>() {
          @Override public void onResponse(Call<SearchMedicinesResponse> call,
              Response<SearchMedicinesResponse> response) {
            switch (response.code()) {
              case 200:
                view.displayMedicineResults(
                    response.body().getData().getBody().getData().getItemLst());
                break;
            }
          }

          @Override public void onFailure(Call<SearchMedicinesResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  void getPopularMedicines(String indNbr, String contractNo, String country, String language,
      String password) {
    dataAccessHandler.getMostPopularMedications(indNbr, contractNo, country, language, password,
        new Callback<MostPopularMedicationsResponse>() {
          @Override public void onResponse(Call<MostPopularMedicationsResponse> call,
              Response<MostPopularMedicationsResponse> response) {
            switch (response.code()) {
              case 200:
                view.displayPopularMedicines(
                    response.body().getData().getBody().getData().getItemLst());
                break;
            }
          }

          @Override public void onFailure(Call<MostPopularMedicationsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface SearchMedicationsActivityView extends BaseActivityView {
    void displayMedicineResults(List<SearchMedicinesResponseDatum> medicationsFromResponse);

    void displayPopularMedicines(List<MostPopularMedicationsResponseDatum> medicationsFromResponse);
  }
}
