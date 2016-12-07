package com.mcsaatchi.gmfit.onboarding.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.profile.activities.TOS_Activity;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Poster;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Singleton;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignUp_Activity extends Base_Activity {

  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.firstNameET) FormEditText firstNameET;
  @Bind(R.id.lastNameET) FormEditText lastNameET;
  @Bind(R.id.showPasswordTV) TextView showPasswordTV;
  @Bind(R.id.createAccountBTN) Button createAccountBTN;
  @Bind(R.id.creatingAccountTOSTV) TextView creatingAccountTOSTV;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private boolean passwordShowing = false;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_up);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.sign_up_activity_title), true);

    allFields.add(firstNameET);
    allFields.add(lastNameET);
    allFields.add(emailET);
    allFields.add(passwordET);

    passwordET.setTypeface(Typeface.DEFAULT);

    createAccountBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Helpers.validateFields(allFields)) {
          registerUser(firstNameET.getText().toString() + " " + lastNameET.getText().toString(),
              emailET.getText().toString(), passwordET.getText().toString());
        } else {
          showPasswordTV.setVisibility(View.GONE);
        }
      }
    });

    creatingAccountTOSTV.setText(Html.fromHtml(getString(R.string.creating_account_TOS)));
    creatingAccountTOSTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(SignUp_Activity.this, TOS_Activity.class));
      }
    });

    showPasswordTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (passwordShowing) {
          passwordET.setTransformationMethod(new PasswordTransformationMethod());
          showPasswordTV.setText(R.string.show_password);
        } else {
          passwordET.setTransformationMethod(null);
          showPasswordTV.setText(R.string.hide_password);
        }

        passwordET.setSelection(passwordET.getText().length());

        passwordShowing = !passwordShowing;
      }
    });

    passwordET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        showPasswordTV.setVisibility(View.VISIBLE);
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  private void registerUser(final String full_name, final String email, final String password) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.signing_up_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_up_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.registerUser(full_name, email, password,
        new Callback<AuthenticationResponse>() {
          @Override public void onResponse(Call<AuthenticationResponse> call,
              Response<AuthenticationResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

                //Refreshes access token
                prefs.edit()
                    .putString(Constants.PREF_USER_ACCESS_TOKEN,
                        "Bearer " + responseBody.getToken())
                    .apply();
                prefs.edit().putString(Constants.EXTRAS_USER_FULL_NAME, full_name).apply();
                prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, email).apply();
                prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, password).apply();

                EventBus_Singleton.getInstance()
                    .post(new EventBus_Poster(
                        Constants.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                Intent intent =
                    new Intent(SignUp_Activity.this, AccountVerification_Activity.class);
                startActivity(intent);

                finish();

                break;
              case 449:
                alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
                alertDialog.show();
                break;
            }
          }

          @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }
}
