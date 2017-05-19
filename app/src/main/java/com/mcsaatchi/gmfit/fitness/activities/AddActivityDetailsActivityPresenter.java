package com.mcsaatchi.gmfit.fitness.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DeleteActivityResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class AddActivityDetailsActivityPresenter extends BaseActivityPresenter {
  private AddActivityDetailsActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  AddActivityDetailsActivityPresenter(AddActivityDetailsActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void addFitnessActivity(String fitness_activity_level_id, String duration, String date) {
    view.callDisplayWaitingDialog(R.string.adding_new_activity_dialog_title);

    dataAccessHandler.addFitnessActivity(fitness_activity_level_id, duration, date,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.finishAndLoadActivities();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
          }
        });
  }

  void getAllActivities(int activity_id) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getAllActivities(new Callback<ActivitiesListResponse>() {
      @Override public void onResponse(Call<ActivitiesListResponse> call,
          Response<ActivitiesListResponse> response) {
        switch (response.code()) {
          case 200:
            List<ActivitiesListResponseBody> body = response.body().getData().getBody();

            for (int i = 0; i < body.size(); i++) {
              if (body.get(i).getId() == activity_id) {
                view.displayActivityLevels(body.get(i));
                view.displayActivityIcon(body.get(i).getIcon());
              }
            }
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ActivitiesListResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void deleteUserActivity(int instance_id) {
    view.callDisplayWaitingDialog(R.string.deleting_user_activity_dialog_title);

    dataAccessHandler.deleteUserActivity(instance_id, new Callback<DeleteActivityResponse>() {
      @Override public void onResponse(Call<DeleteActivityResponse> call,
          Response<DeleteActivityResponse> response) {
        switch (response.code()) {
          case 200:

            view.finishAndLoadActivities();

            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DeleteActivityResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface AddActivityDetailsActivityView extends BaseActivityView {
    void finishAndLoadActivities();

    void displayActivityLevels(ActivitiesListResponseBody activitiesListResponseBody);

    void displayActivityIcon(String icon);
  }
}
