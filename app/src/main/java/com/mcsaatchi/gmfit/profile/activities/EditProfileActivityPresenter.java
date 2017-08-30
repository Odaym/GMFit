package com.mcsaatchi.gmfit.profile.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class EditProfileActivityPresenter extends BaseActivityPresenter {
  private EditProfileActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  EditProfileActivityPresenter(EditProfileActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void updateUserProfileExplicitly(RequestBody name, RequestBody phone_number, RequestBody gender,
      RequestBody date_of_birth, RequestBody blood_type, RequestBody height, RequestBody weight) {

    view.callDisplayWaitingDialog(R.string.updating_user_profile_dialog_title);

    dataAccessHandler.updateUserProfileExplicitly(name, phone_number, gender, date_of_birth,
        blood_type, height, weight, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                view.handleSuccessfulProfileUpdate();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface EditProfileActivityView extends BaseActivityView {
    void handleSuccessfulProfileUpdate();
  }
}
