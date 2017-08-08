package com.mcsaatchi.gmfit.insurance.activities.chronic;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class SubmitChronicActivityPresenter extends BaseActivityPresenter {
  private SubmitChronicActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SubmitChronicActivityPresenter(SubmitChronicActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void submitChronicTreatment(String contractNo, HashMap<String, RequestBody> attachements) {
    dataAccessHandler.createNewRequest(Helpers.toRequestBody(contractNo), null,
        Helpers.toRequestBody("2"), Helpers.toRequestBody("3"), null, null, null, null, null,
        attachements, new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                view.openChronicTrackActivity(response.body().getData().getBody().getData().getRequestId());
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.dismissWaitingDialog();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  interface SubmitChronicActivityView extends BaseActivityView {
    void openChronicTrackActivity(Integer requestId);

    void dismissWaitingDialog();
  }
}
