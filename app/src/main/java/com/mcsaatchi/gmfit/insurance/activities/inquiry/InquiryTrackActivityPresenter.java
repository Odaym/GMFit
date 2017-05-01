package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class InquiryTrackActivityPresenter extends BaseActivityPresenter {
  private InquiryTrackActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  InquiryTrackActivityPresenter(InquiryTrackActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getInquiriesList(String CountryCRMCode) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getInquiriesList(null, CountryCRMCode, new Callback<InquiriesListResponse>() {
      @Override public void onResponse(Call<InquiriesListResponse> call,
          Response<InquiriesListResponse> response) {

        switch (response.code()) {
          case 200:
            view.displayInquiriesList(response.body().getData().getBody().getData());
            break;
          case 449:
            view.displayRequestErrorDialog(
                Helpers.provideErrorStringFromJSON(response.errorBody()));
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<InquiriesListResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface InquiryTrackActivityView extends BaseActivityPresenter.BaseActivityView {
    void displayInquiriesList(List<InquiriesListResponseInnerData> inquiriesList);
  }
}
