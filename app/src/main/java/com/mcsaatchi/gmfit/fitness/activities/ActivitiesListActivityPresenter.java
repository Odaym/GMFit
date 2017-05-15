package com.mcsaatchi.gmfit.fitness.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ActivitiesListActivityPresenter extends BaseActivityPresenter {
  private ActivitiesListActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ActivitiesListActivityPresenter(ActivitiesListActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getAllActivities() {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getAllActivities(new Callback<ActivitiesListResponse>() {
      @Override public void onResponse(Call<ActivitiesListResponse> call,
          Response<ActivitiesListResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayAllActivities(response.body().getData().getBody());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ActivitiesListResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface ActivitiesListActivityView extends BaseActivityView {
    void displayAllActivities(List<ActivitiesListResponseBody> activitiesListResponseBodies);
  }
}
