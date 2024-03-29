package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UploadInsuranceImageResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  void uploadInsuranceImage(Map<String, RequestBody> file) {
    dataAccessHandler.uploadInsuranceImage(file, new Callback<UploadInsuranceImageResponse>() {
      @Override public void onResponse(Call<UploadInsuranceImageResponse> call,
          Response<UploadInsuranceImageResponse> response) {
        switch (response.code()) {
          case 200:
            view.saveImagePath(response.body().getData().getBody().getPath());
            break;
        }

        view.dismissWaitingDialog();
      }

      @Override public void onFailure(Call<UploadInsuranceImageResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void submitApprovalRequest(String contractNo, String remarks, String categoryValue,
      String subcategoryId, HashMap<String, RequestBody> attachements) {

    dataAccessHandler.createNewRequest(Helpers.toRequestBody(contractNo),
        Helpers.toRequestBody(categoryValue), Helpers.toRequestBody(subcategoryId),
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
                view.finishActivity();
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

  void getSubCategories(String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getSubCategories(contractNo, new Callback<SubCategoriesResponse>() {
      @Override public void onResponse(Call<SubCategoriesResponse> call,
          Response<SubCategoriesResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateSubCategories(response.body().getData().getBody().getData());
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<SubCategoriesResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface SubmitApprovalRequestActivityView extends BaseActivityView {
    void openApprovalRequestDetailsActivity(Integer claimId);

    void populateSubCategories(List<SubCategoriesResponseDatum> subCategories);

    void saveImagePath(String imagePath);

    void dismissWaitingDialog();
  }
}
