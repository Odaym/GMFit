package com.mcsaatchi.gmfit.profile.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ChangePasswordActivity extends BaseActivity {
  @Bind(R.id.oldPasswordET) FormEditText oldPasswordET;
  @Bind(R.id.retypeNewPasswordET) FormEditText retypeNewPasswordET;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_change_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.change_password_activity_title), true);

    allFields.add(oldPasswordET);
    allFields.add(retypeNewPasswordET);
    allFields.add(newPasswordET);

    oldPasswordET.setTypeface(Typeface.DEFAULT);
    retypeNewPasswordET.setTypeface(Typeface.DEFAULT);
    newPasswordET.setTypeface(Typeface.DEFAULT);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.change_password, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (Helpers.validateFields(allFields)) {
      if (!oldPasswordET.getText()
          .toString()
          .equals(prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""))) {
        Toast.makeText(this, getString(R.string.old_password_conflict_existing_password),
            Toast.LENGTH_SHORT).show();
      } else if (!newPasswordET.getText()
          .toString()
          .equals(retypeNewPasswordET.getText().toString())) {
        Toast.makeText(this, getString(R.string.old_password_retype_no_match), Toast.LENGTH_LONG)
            .show();
        newPasswordET.setText("");
        retypeNewPasswordET.setText("");
      } else if (oldPasswordET.getText().toString().equals(newPasswordET.getText().toString())) {
        Toast.makeText(this, getString(R.string.conflicting_old_new_password), Toast.LENGTH_SHORT)
            .show();
      } else {
        changePassword(oldPasswordET.getText().toString(), newPasswordET.getText().toString());
      }
    }

    return super.onOptionsItemSelected(item);
  }

  private void changePassword(String old_password, String new_password) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.change_password_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.change_password_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.changePassword(old_password, new_password,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Toast.makeText(ChangePasswordActivity.this,
                    getString(R.string.password_change_successful), Toast.LENGTH_SHORT).show();

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
