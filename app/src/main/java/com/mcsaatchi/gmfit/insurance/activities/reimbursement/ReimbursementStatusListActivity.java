package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponse;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.adapters.StatusAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ReimbursementStatusListActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  private StatusAdapter statusAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reimbursement_status_list);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Reimbursement Status", true);

    getReimbursementClaims();
  }

  private void getReimbursementClaims() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getClaimsList(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        "1", new Callback<ClaimsListResponse>() {
          @Override public void onResponse(Call<ClaimsListResponse> call,
              Response<ClaimsListResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                statusAdapter = new StatusAdapter(ReimbursementStatusListActivity.this,
                    response.body().getData().getBody().getData(),
                    new StatusAdapter.OnClickListener() {
                      @Override
                      public void onClick(ClaimsListResponseDatum reimbursementModel, int index) {
                        Intent intent = new Intent(ReimbursementStatusListActivity.this,
                            ReimbursementStatusDetailsActivity.class);
                        intent.putExtra(ReimbursementStatusDetailsActivity.REIMBURSEMENT_REQUEST_ID,
                            reimbursementModel.getId());
                        startActivity(intent);
                      }
                    });

                recyclerView.setLayoutManager(
                    new LinearLayoutManager(ReimbursementStatusListActivity.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(statusAdapter);
                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
                alertDialog.show();
                break;
            }
          }

          @Override public void onFailure(Call<ClaimsListResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(t.getMessage());
            alertDialog.show();
          }
        });
  }
}