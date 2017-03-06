package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicInactiveStatusAdapter;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicStatusAdapter;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChronicStatusListActivity extends BaseActivity {
  @Bind(R.id.activeTreatmentsRecyclerView) RecyclerView activeTreatmentsRecyclerView;
  @Bind(R.id.inactiveTreatmentsRecyclerView) RecyclerView inactiveTreatmentsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ChronicStatusAdapter chronicStatusAdapter;
  private ChronicInactiveStatusAdapter chronicInactiveStatusAdapter;

  private Calendar fromDate = Calendar.getInstance();
  private Calendar toDate = Calendar.getInstance();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.chronic_status_list_activity_title), true);

    setupActiveTreatmentsList();

    setupInactiveTreatmentsList();

    getApprovalRequestClaims();
  }

  private void setupActiveTreatmentsList() {
    //
    //chronicStatusAdapter = new ChronicStatusAdapter(this, activeTreatments);
    //activeTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    //activeTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    //activeTreatmentsRecyclerView.setHasFixedSize(true);
    //activeTreatmentsRecyclerView.setAdapter(chronicStatusAdapter);
  }

  private void setupInactiveTreatmentsList() {
    //
    //chronicInactiveStatusAdapter = new ChronicInactiveStatusAdapter(this, inactiveTreatmentsList);
    //inactiveTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    //inactiveTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    //inactiveTreatmentsRecyclerView.setHasFixedSize(true);
    //inactiveTreatmentsRecyclerView.setAdapter(chronicInactiveStatusAdapter);
  }


  private void getApprovalRequestClaims() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
        getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getClaimsList(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        "3", new Callback<ClaimsListResponse>() {
          @Override public void onResponse(Call<ClaimsListResponse> call,
              Response<ClaimsListResponse> response) {
            switch (response.code()) {
              case 200:
                //statusAdapter = new StatusAdapter(response.body().getData().getBody().getData(),
                //    new StatusAdapter.OnClickListener() {
                //      @Override
                //      public void onClick(ClaimsListResponseDatum reimbursementModel, int index) {
                //        Intent intent = new Intent(ApprovalRequestsStatusListActivity.this,
                //            ReimbursementStatusDetailsActivity.class);
                //        intent.putExtra(ReimbursementStatusDetailsActivity.REIMBURSEMENT_MODEL_KEY,
                //            reimbursementModel);
                //        startActivity(intent);
                //      }
                //    });
                //
                //recyclerView.setLayoutManager(
                //    new LinearLayoutManager(ApprovalRequestsStatusListActivity.this));
                //recyclerView.setHasFixedSize(true);
                //recyclerView.setAdapter(statusAdapter);

                break;
              case 449:
                alertDialog.setMessage("error: An error has occurred. Please contact your system administrator.");
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ClaimsListResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(t.getMessage());
            alertDialog.show();
          }
        });
  }

}
