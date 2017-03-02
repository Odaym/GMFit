package com.mcsaatchi.gmfit.onboarding.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ResetPasswordActivity extends BaseActivity {

  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.verifyCodeET) FormEditText verifyCodeET;
  @Bind(R.id.submitResetPasswordBTN) Button submitResetPasswordBTN;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reset_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.reset_password_activity_title), true);

    allFields.add(passwordET);
    allFields.add(verifyCodeET);

    submitResetPasswordBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Helpers.isInternetAvailable(ResetPasswordActivity.this)) {
          if (Helpers.validateFields(allFields)) {
            finalizeResetPassword(verifyCodeET.getText().toString(),
                passwordET.getText().toString());
          }
        }
      }
    });
  }

  private void finalizeResetPassword(String token, String newPassword) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.resetting_password_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.resetting_password_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.finalizeResetPassword(token, newPassword, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            finish();

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getResources().getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }
}
