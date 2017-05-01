package com.mcsaatchi.gmfit.insurance.fragments;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.GetNearbyClinicsResponseDatum;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class InsuranceDirectoryFragmentPresenter extends BaseFragmentPresenter {
  private InsuranceDirectoryFragmentView view;
  private DataAccessHandlerImpl dataAccessHandler;

  InsuranceDirectoryFragmentPresenter(InsuranceDirectoryFragmentView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getNearbyClinics(String contractNo, String providerTypesCode, int searchCtry,
      double longitude, double latitude, int fetchClosest) {
    dataAccessHandler.getNearbyClinics(contractNo, providerTypesCode, searchCtry, longitude,
        latitude, fetchClosest, new Callback<GetNearbyClinicsResponse>() {
          @Override public void onResponse(Call<GetNearbyClinicsResponse> call,
              Response<GetNearbyClinicsResponse> response) {
            switch (response.code()) {
              case 200:
                view.displayNearbyClinics(response.body().getData().getBody().getData());
            }
          }

          @Override public void onFailure(Call<GetNearbyClinicsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface InsuranceDirectoryFragmentView extends BaseFragmentView {
    void displayNearbyClinics(List<GetNearbyClinicsResponseDatum> clinicsList);
  }
}
