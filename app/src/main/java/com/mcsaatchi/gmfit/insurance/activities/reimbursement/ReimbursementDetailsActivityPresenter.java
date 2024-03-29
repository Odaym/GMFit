package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ReimbursementDetailsActivityPresenter extends BaseActivityPresenter {
  private ReimbursementDetailsActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ReimbursementDetailsActivityPresenter(ReimbursementDetailsActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getReimbursementClaimDetails(String contractNo, int claimId) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getClaimslistDetails(contractNo, "1", String.valueOf(claimId),
        new Callback<ClaimListDetailsResponse>() {
          @Override public void onResponse(Call<ClaimListDetailsResponse> call,
              Response<ClaimListDetailsResponse> response) {
            switch (response.code()) {
              case 200:
                if (response.body().getData().getBody().getData() != null) {
                  view.populateClaimDetails(response.body().getData().getBody().getData().get(0));
                }
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<ClaimListDetailsResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  interface ReimbursementDetailsActivityView extends BaseActivityView {
    void populateClaimDetails(ClaimListDetailsResponseDatum claimDetails);
  }
}
