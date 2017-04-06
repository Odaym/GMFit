package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.rest.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ReimbursementDetailsActivityPresenter extends BaseActivityPresenter {
  private ReimbursementDetailsActivityView view;
  private DataAccessHandler dataAccessHandler;

  ReimbursementDetailsActivityPresenter(ReimbursementDetailsActivityView view,
      DataAccessHandler dataAccessHandler) {
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
                view.populateClaimDetails(response.body().getData().getBody().getData().get(0));
                break;
            }
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
