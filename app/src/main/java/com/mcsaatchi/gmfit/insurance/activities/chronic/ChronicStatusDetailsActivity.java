package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentDetailsResponse;
import com.mcsaatchi.gmfit.architecture.rest.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChronicStatusDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicalRemindersRecyclerView) RecyclerView medicalRemindersRecyclerView;
  @Bind(R.id.startDateTV) TextView startDateTV;
  @Bind(R.id.endDateTV) TextView endDateTV;
  @Bind(R.id.statusValueTV) TextView statusValueTV;

  private MedicalInformationAdapter adapter;
  private ChronicTreatmentListInnerData chronicTreatment;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_chronic_status);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      chronicTreatment =
          (ChronicTreatmentListInnerData) getIntent().getExtras().get("CHRONIC_OBJECT");
      setupToolbar(getClass().getSimpleName(), toolbar, chronicTreatment.getName(), true);

      statusValueTV.setTextColor(
          getResources().getColor(Helpers.determineStatusColor(chronicTreatment.getStatus())));
      statusValueTV.setText(chronicTreatment.getStatus());

      if (chronicTreatment.getStartDate() != null && chronicTreatment.getEndDate() != null) {
        startDateTV.setText(chronicTreatment.getStartDate().split("T")[0]);
        endDateTV.setText(chronicTreatment.getEndDate().split("T")[0]);
      }

      getChronicTreatmentDetails();
    } else {
      setupToolbar(getClass().getSimpleName(), toolbar,
          getResources().getString(R.string.treatment_status_section_title), true);
    }
  }

  private void getChronicTreatmentDetails() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.setCancelable(false);
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getChronicTreatmentDetails(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "3",
        chronicTreatment.getRequestNbr(), new Callback<ChronicTreatmentDetailsResponse>() {
          @Override public void onResponse(Call<ChronicTreatmentDetailsResponse> call,
              Response<ChronicTreatmentDetailsResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                if (response.body().getData().getBody().getData().getItemsList() != null) {
                  adapter = new MedicalInformationAdapter(
                      response.body().getData().getBody().getData().getItemsList(),
                      (medicalInformationModel, index) -> Toast.makeText(
                          ChronicStatusDetailsActivity.this, "Medical Information list item!",
                          Toast.LENGTH_SHORT).show());
                  medicalRemindersRecyclerView.setLayoutManager(
                      new LinearLayoutManager(ChronicStatusDetailsActivity.this));
                  medicalRemindersRecyclerView.setNestedScrollingEnabled(false);
                  medicalRemindersRecyclerView.addItemDecoration(
                      new SimpleDividerItemDecoration(ChronicStatusDetailsActivity.this));
                  medicalRemindersRecyclerView.setAdapter(adapter);
                }

                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<ChronicTreatmentDetailsResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(t.getMessage());
            alertDialog.show();
          }
        });
  }
}
