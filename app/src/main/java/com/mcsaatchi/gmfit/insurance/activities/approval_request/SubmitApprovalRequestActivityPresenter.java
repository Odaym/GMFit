package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.rest.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.HashMap;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class SubmitApprovalRequestActivityPresenter extends BaseActivityPresenter {
  private SubmitApprovalRequestActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SubmitApprovalRequestActivityPresenter(SubmitApprovalRequestActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void submitApprovalRequest(String contractNo, String remarks, String categoryValue,
      HashMap<String, RequestBody> attachements) {

    dataAccessHandler.createNewRequest(Helpers.toRequestBody(contractNo),
        Helpers.toRequestBody(categoryValue), Helpers.toRequestBody("2"),
        Helpers.toRequestBody("2"), Helpers.toRequestBody("10"), Helpers.toRequestBody("2"),
        Helpers.toRequestBody(Helpers.formatRequestTime() + "T16:27:32+02:00"),
        Helpers.toRequestBody("D"), Helpers.toRequestBody(remarks), attachements,
        new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                view.openApprovalRequestDetailsActivity(
                    response.body().getData().getBody().getData().getRequestId());
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.dismissWaitingDialog();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface SubmitApprovalRequestActivityView extends BaseActivityView {
    void openApprovalRequestDetailsActivity(Integer claimId);

    void dismissWaitingDialog();
  }
}
