package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UploadInsuranceImageResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

class SubmitInquiryActivityPresenter extends BaseActivityPresenter {
  private SubmitInquiryActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  SubmitInquiryActivityPresenter(SubmitInquiryActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void uploadInsuranceImage(Map<String, RequestBody> file) {
    view.callDisplayWaitingDialog(R.string.submit_new_inquiry);

    dataAccessHandler.uploadInsuranceImage(file, new Callback<UploadInsuranceImageResponse>() {
      @Override public void onResponse(Call<UploadInsuranceImageResponse> call,
          Response<UploadInsuranceImageResponse> response) {
        switch (response.code()) {
          case 200:
            view.startSubmitInquiryComplaint(response.body().getData().getBody().getPath());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<UploadInsuranceImageResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void submitInquiryComplaint(RequestBody contractNo, RequestBody crm_country, RequestBody category,
      RequestBody subcategory, RequestBody area, RequestBody title, RequestBody path) {

    dataAccessHandler.createNewInquiryComplaint(contractNo, crm_country, category, subcategory,
        area, title, path, new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                view.finishActivity();
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  void submitInquiryComplaintWithoutImage(RequestBody contractNo, RequestBody crm_country,
      RequestBody category, RequestBody subcategory, RequestBody area, RequestBody title) {

    view.callDisplayWaitingDialog(R.string.submit_new_inquiry);

    dataAccessHandler.createNewInquiryComplaintWithoutImage(contractNo, crm_country, category,
        subcategory, area, title, new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                view.finishActivity();
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  void getCRMCategories(RequestBody contractNo, RequestBody crm_country) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getCRMCategories(contractNo, crm_country,
        new Callback<CRMCategoriesResponse>() {
          @Override public void onResponse(Call<CRMCategoriesResponse> call,
              Response<CRMCategoriesResponse> response) {
            switch (response.code()) {
              case 200:
                view.setupCategoryAndSubCategoryPickerDropdown(
                    response.body().getData().getBody().getData());
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<CRMCategoriesResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  interface SubmitInquiryActivityView extends BaseActivityView {
    void startSubmitInquiryComplaint(String imagePath);

    void setupCategoryAndSubCategoryPickerDropdown(
        List<CRMCategoriesResponseDatum> crmCategoriesResponseDatumList);
  }
}
