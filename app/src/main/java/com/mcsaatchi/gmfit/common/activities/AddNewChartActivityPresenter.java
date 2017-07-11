package com.mcsaatchi.gmfit.common.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartsBySectionResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChartsBySectionResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewChartActivityPresenter extends BaseActivityPresenter {

  private AddNewChartActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;
  AddNewChartActivityPresenter(AddNewChartActivityView view, DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getChartsBySection(String sectionName, final int requestCodeToSendBack) {
    dataAccessHandler.getChartsBySection(sectionName, new Callback<ChartsBySectionResponse>() {
      @Override public void onResponse(Call<ChartsBySectionResponse> call,
          Response<ChartsBySectionResponse> response) {
        switch (response.code()) {
          case 200:
            view.dismissWaitingDialog();

            List<ChartsBySectionResponseDatum> chartsFromResponse = response.body().getData().getBody().getData();

            for (int i = 0; i < chartsFromResponse.size(); i++) {
              if (chartsFromResponse.get(i).getSlug().equals("flights-climbed")) {
                chartsFromResponse.remove(i);
              }
            }

            view.displayChartsBySection(chartsFromResponse,
                requestCodeToSendBack);
            break;
        }
      }

      @Override public void onFailure(Call<ChartsBySectionResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void addMetricChart(int chart_id){
    dataAccessHandler.addMetricChart(chart_id, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            Timber.d("onResponse: Successfully add a new chart!");
            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface AddNewChartActivityView extends BaseActivityView {
    void displayChartsBySection(List<ChartsBySectionResponseDatum> chartsFromResponse,
        int requestCodeToSendBack);

    void dismissWaitingDialog();
  }
}
