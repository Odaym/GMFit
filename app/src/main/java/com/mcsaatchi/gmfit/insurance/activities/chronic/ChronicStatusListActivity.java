package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.ChronicStatusAdapter;
import com.mcsaatchi.gmfit.insurance.models.TreatmentsModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChronicStatusListActivity extends BaseActivity {
  @Bind(R.id.activeTreatmentsRecyclerView) RecyclerView activeTreatmentsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ChronicStatusAdapter chronicStatusAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.chronic_status_list_activity_title), true);

    getChronicTreatmentsList();
  }

  private void setupActiveTreatmentsList(ArrayList<TreatmentsModel> activeTreatments) {
    chronicStatusAdapter = new ChronicStatusAdapter(this, activeTreatments);
    activeTreatmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    activeTreatmentsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    activeTreatmentsRecyclerView.setHasFixedSize(true);
    activeTreatmentsRecyclerView.setAdapter(chronicStatusAdapter);
  }

  private void getChronicTreatmentsList() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getChronicTreatmentsList(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "3",
        new Callback<ChronicTreatmentListResponse>() {
          @Override public void onResponse(Call<ChronicTreatmentListResponse> call,
              Response<ChronicTreatmentListResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                ArrayList<TreatmentsModel> activeTreatments = new ArrayList<>();

                List<ChronicTreatmentListInnerData> chronicTreatmentListInnerData =
                    response.body().getData().getBody().getData();

                for (int i = 0; i < chronicTreatmentListInnerData.size(); i++) {
                  activeTreatments.add(
                      new TreatmentsModel(chronicTreatmentListInnerData.get(i).getName(),
                          chronicTreatmentListInnerData.get(i).getStartDate(),
                          chronicTreatmentListInnerData.get(i).getEndDate(),
                          chronicTreatmentListInnerData.get(i).getStatus()));
                }

                setupActiveTreatmentsList(activeTreatments);

                break;
              case 449:
                alertDialog.setMessage(
                    "error: An error has occurred. Please contact your system administrator.");
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ChronicTreatmentListResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(t.getMessage());
            alertDialog.show();
          }
        });
  }
}
