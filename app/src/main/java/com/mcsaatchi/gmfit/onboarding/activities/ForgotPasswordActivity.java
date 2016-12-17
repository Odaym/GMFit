package com.mcsaatchi.gmfit.onboarding.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
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

public class ForgotPasswordActivity extends BaseActivity {

  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.submitForgotPasswordEmailBTN) Button submitForgotPasswordEmailBTN;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_forgot_password);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.forgot_password_activity_title), true);

    allFields.add(emailET);

    submitForgotPasswordEmailBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Helpers.validateFields(allFields)) {
          forgotPasswordSendToken(emailET.getText().toString());
        }
      }
    });
  }

  private void forgotPasswordSendToken(String email) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.sending_reset_password_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.sending_reset_password_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.sendResetPasswordLink(email, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            startActivity(intent);

            Toast.makeText(ForgotPasswordActivity.this, R.string.password_change_successful,
                Toast.LENGTH_SHORT).show();

            finish();

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }
}
