package com.mcsaatchi.gmfit.insurance.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import java.util.HashMap;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitReimbursementActivityPresenter extends BaseActivityPresenter {
  private SubmitReimbursementActivityView view;
  private DataAccessHandler dataAccessHandler;

  public SubmitReimbursementActivityPresenter(SubmitReimbursementActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void submitReimbursement(String contractNo, String category, String subCategoryId,
      String requestTypeId, String amountClaimed, String remarks,
      HashMap<String, RequestBody> attachements) {
    view.callDisplayWaitingDialog(R.string.submit_new_reimbursement);

    dataAccessHandler.createNewRequest(Helpers.toRequestBody(contractNo),
        Helpers.toRequestBody(category), Helpers.toRequestBody(subCategoryId),
        Helpers.toRequestBody(requestTypeId), Helpers.toRequestBody(amountClaimed),
        Helpers.toRequestBody("2"),
        Helpers.toRequestBody(Helpers.formatRequestTime() + "T16:27:32+02:00"),
        Helpers.toRequestBody("D"), Helpers.toRequestBody(remarks), attachements,
        new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                view.openReimbursementDetailsActivity(
                    response.body().getData().getBody().getData().getRequestId());
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  public void getSubCategories(String contractNo) {
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

  public interface SubmitReimbursementActivityView extends BaseActivityView {
    void populateSubCategories(List<SubCategoriesResponseDatum> subCategories);

    void openReimbursementDetailsActivity(Integer claimId);
  }
}
