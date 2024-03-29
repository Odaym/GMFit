package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ApprovalRequestDetailsActivityPresenter extends BaseActivityPresenter {
  private ApprovalRequestDetailsActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ApprovalRequestDetailsActivityPresenter(ApprovalRequestDetailsActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getClaimDetailsList(String contractNo, String requestType, int claimId) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getClaimslistDetails(contractNo, requestType, String.valueOf(claimId),
        new Callback<ClaimListDetailsResponse>() {
          @Override public void onResponse(Call<ClaimListDetailsResponse> call,
              Response<ClaimListDetailsResponse> response) {
            switch (response.code()) {
              case 200:
                ClaimListDetailsResponseDatum responseDatum =
                    response.body().getData().getBody().getData().get(0);
                view.displayClaimDetails(responseDatum);
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<ClaimListDetailsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ApprovalRequestDetailsActivityView extends BaseActivityView {
    void displayClaimDetails(ClaimListDetailsResponseDatum responseDatum);
  }
}
