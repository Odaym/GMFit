package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ReimbursementTrackActivityPresenter extends BaseActivityPresenter {
  private ReimbursementTrackActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ReimbursementTrackActivityPresenter(ReimbursementTrackActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getReimbursementClaims(String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getClaimsList(contractNo, "1", new Callback<ClaimsListResponse>() {
      @Override
      public void onResponse(Call<ClaimsListResponse> call, Response<ClaimsListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateClaimsList(response.body().getData().getBody().getData());

            view.hookupSearchBar(response.body().getData().getBody().getData());

            break;
          case 449:
            view.displayRequestErrorDialog(
                Helpers.provideErrorStringFromJSON(response.errorBody()));
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<ClaimsListResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface ReimbursementTrackActivityView extends BaseActivityPresenter.BaseActivityView {
    void populateClaimsList(List<ClaimsListResponseDatum> claimsList);

    void hookupSearchBar(List<ClaimsListResponseDatum> claimsList);
  }
}
