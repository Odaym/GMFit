package com.mcsaatchi.gmfit.insurance.presenters;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.presenters.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReimbursementTrackActivityPresenter extends BaseActivityPresenter {
  private ReimbursementTrackActivityView view;
  private DataAccessHandler dataAccessHandler;

  public ReimbursementTrackActivityPresenter(ReimbursementTrackActivityView view,
      DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  public void getReimbursementClaims(String contractNo) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getClaimsList(contractNo, "1", new Callback<ClaimsListResponse>() {
      @Override
      public void onResponse(Call<ClaimsListResponse> call, Response<ClaimsListResponse> response) {
        switch (response.code()) {
          case 200:
            view.populateClaimsList(response.body().getData().getBody().getData());
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

  public interface ReimbursementTrackActivityView extends BaseActivityPresenter.BaseActivityView {
    void populateClaimsList(List<ClaimsListResponseDatum> claimsList);
  }
}
