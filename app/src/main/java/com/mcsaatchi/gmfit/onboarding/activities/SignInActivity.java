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
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SignInActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.signInBTN) Button signInBTN;
  @Bind(R.id.forgotPasswordTV) TextView forgotPasswordTV;
  @Bind(R.id.showPasswordTV) TextView showPasswordTV;

  private boolean passwordShowing = false;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_in);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.sign_in_activity_title), true);

    allFields.add(emailET);
    allFields.add(passwordET);

    passwordET.setTypeface(Typeface.DEFAULT);
    emailET.setSelection(emailET.getText().length());

    signInBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (Helpers.validateFields(allFields)) {
          if (Helpers.isInternetAvailable(SignInActivity.this)) {
            signInUser(emailET.getText().toString(), passwordET.getText().toString());
          } else {
            Helpers.showNoInternetDialog(SignInActivity.this);
          }
        } else {
          showPasswordTV.setVisibility(View.GONE);
        }
      }
    });

    forgotPasswordTV.setText(Html.fromHtml(getString(R.string.forgot_password_button)));
    forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
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

  public void signInUser(final String email, final String password) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.signInUser(email, password, new Callback<AuthenticationResponse>() {
      @Override public void onResponse(Call<AuthenticationResponse> call,
          Response<AuthenticationResponse> response) {

        switch (response.code()) {
          case 200:
            AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

            //Refreshes access token
            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken())
                .apply();
            prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, email).apply();
            prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, password).apply();
            prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();

            getOnboardingStatus(waitingDialog);

            break;
          case 401:
            alertDialog.setTitle(getString(R.string.error_occurred_dialog_title));
            alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
            alertDialog.show();
            break;
          case 403:
            EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

            Intent userNotVerifiedIntent =
                new Intent(SignInActivity.this, AccountVerificationActivity.class);
            startActivity(userNotVerifiedIntent);

            finish();
            break;
          case 449:
            alertDialog.setMessage(
                getResources().getString(R.string.server_error_got_returned));
            alertDialog.show();
            break;
          case 500:
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
            break;
        }

        waitingDialog.dismiss();
      }

      @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getResources().getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  public void getOnboardingStatus(final ProgressDialog waitingDialog) {
    dataAccessHandler.getOnboardingStatus(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {

        Intent intent;

        switch (response.code()) {
          case 200:
            String userOnBoard = response.body().getData().getBody().getData().getOnboard();

            if (userOnBoard.equals("1")) {
              getUiForSection(waitingDialog, "fitness");
            } else {
              EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

              intent = new Intent(SignInActivity.this, SetupProfileActivity.class);
              startActivity(intent);
              finish();
            }

            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  private void getUiForSection(final ProgressDialog waitingDialog, String section) {
    dataAccessHandler.getUiForSection(Constants.BASE_URL_ADDRESS + "user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                    (ArrayList<AuthenticationResponseChart>) chartsMap);
                startActivity(intent);

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }
}
