package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ApprovalRequestsTrackActivityPresenter extends BaseActivityPresenter {
  private ApprovalRequestsTrackActivityView view;
  private DataAccessHandler dataAccessHandler;

  ApprovalRequestsTrackActivityPresenter(ApprovalRequestsTrackActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getClaimsList(String contractNo, String requestType) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getClaimsList(contractNo, requestType, new Callback<ClaimsListResponse>() {
      @Override
      public void onResponse(Call<ClaimsListResponse> call, Response<ClaimsListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateClaimsList(response.body().getData().getBody().getData());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ClaimsListResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface ApprovalRequestsTrackActivityView extends BaseActivityView {
    void populateClaimsList(List<ClaimsListResponseDatum> claimsList);
  }
}
