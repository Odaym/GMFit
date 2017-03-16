package com.mcsaatchi.gmfit.health.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.CounsellingInformationResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CounsellingInformationActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.counsellingInformationTV) TextView counsellingInformationTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_counselling_information);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.counselling_information_activity_title), true);

    getCounsellingInformation();
  }

  private void getCounsellingInformation() {
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

    dataAccessHandler.getCounsellingInformation(new Callback<CounsellingInformationResponse>() {

      @Override public void onResponse(Call<CounsellingInformationResponse> call,
          Response<CounsellingInformationResponse> response) {

        switch (response.code()) {
          case 200:
            counsellingInformationTV.setText(
                response.body().getData().getBody().getData().getCompatibilityCheckDesc());
            break;
          case 449:
            alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
            alertDialog.show();
            break;
        }

        waitingDialog.dismiss();
      }

      @Override public void onFailure(Call<CounsellingInformationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }
}
