package com.mcsaatchi.gmfit.onboarding.activities;

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
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.presenters.SignUpActivityPresenter;
import com.mcsaatchi.gmfit.profile.activities.TOSActivity;
import java.util.ArrayList;

public class SignUpActivity extends BaseActivity
    implements SignUpActivityPresenter.SignUpActivityView {

  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.firstNameET) FormEditText firstNameET;
  @Bind(R.id.lastNameET) FormEditText lastNameET;
  @Bind(R.id.showPasswordTV) TextView showPasswordTV;
  @Bind(R.id.creatingAccountTOSTV) TextView creatingAccountTOSTV;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private boolean passwordShowing = false;
  private SignUpActivityPresenter presenter;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_up);

    ButterKnife.bind(this);

    presenter = new SignUpActivityPresenter(this, dataAccessHandler);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.sign_up_activity_title), true);

    allFields.add(firstNameET);
    allFields.add(lastNameET);
    allFields.add(emailET);
    allFields.add(passwordET);

    passwordET.setTypeface(Typeface.DEFAULT);

    creatingAccountTOSTV.setText(Html.fromHtml(getString(R.string.creating_account_TOS)));

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

  @OnClick(R.id.creatingAccountTOSTV) public void handleShowTermsOfService() {
    startActivity(new Intent(SignUpActivity.this, TOSActivity.class));
  }

  @OnClick(R.id.showPasswordTV) public void handleShowPassword() {
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

  @OnClick(R.id.createAccountBTN) public void handleCreateAccount() {
    if (Helpers.validateFields(allFields)) {
      presenter.signUserUp(firstNameET.getText().toString() + " " + lastNameET.getText().toString(),
          emailET.getText().toString(), passwordET.getText().toString());
    } else {
      showPasswordTV.setVisibility(View.GONE);
    }
  }

  @Override public void saveUserSignUpDetails(String accessToken, String full_name, String email,
      String password) {
    prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + accessToken).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_FULL_NAME, full_name).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, email).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, password).apply();
  }

  @Override public void openAccountVerificationActivity() {
    EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

    Intent intent = new Intent(SignUpActivity.this, AccountVerificationActivity.class);
    startActivity(intent);

    finish();
  }

  @Override public void showEmailTakenErrorDialog() {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_up_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        (dialog, which) -> dialog.dismiss());
    alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
    alertDialog.show();
  }
}
