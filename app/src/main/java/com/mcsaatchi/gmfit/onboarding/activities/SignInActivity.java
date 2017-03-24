package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.presenters.SignInActivityPresenter;
import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends BaseActivity
    implements SignInActivityPresenter.SignInActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.forgotPasswordTV) TextView forgotPasswordTV;
  @Bind(R.id.showPasswordTV) TextView showPasswordTV;

  private boolean passwordShowing = false;

  private SignInActivityPresenter presenter;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_in);

    ButterKnife.bind(this);

    presenter = new SignInActivityPresenter(this, dataAccessHandler);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.sign_in_activity_title), true);

    allFields.add(emailET);
    allFields.add(passwordET);

    passwordET.setTypeface(Typeface.DEFAULT);
    emailET.setSelection(emailET.getText().length());

    forgotPasswordTV.setText(Html.fromHtml(getString(R.string.forgot_password_button)));

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

  @OnClick(R.id.forgotPasswordTV) public void handleForgotPassword() {
    startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
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

  @OnClick(R.id.signInBTN) public void handleSignInClicked() {
    if (Helpers.validateFields(allFields)) {
      if (checkInternetAvailable()) {
        presenter.signIn(emailET.getText().toString(), passwordET.getText().toString());
      } else {
        showNoInternetDialog();
      }
    } else {
      showPasswordTV.setVisibility(View.GONE);
    }
  }

  @Override public void saveUserSignInDetails(String accessToken, String email, String password) {
    prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + accessToken).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, email).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, password).apply();
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();
  }

  @Override public void openAccountVerificationActivity() {
    EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

    Intent userNotVerifiedIntent =
        new Intent(SignInActivity.this, AccountVerificationActivity.class);
    startActivity(userNotVerifiedIntent);
    finish();
  }

  @Override public void openSetupProfileActivity() {
    EventBusSingleton.getInstance().post(new SignedInSuccessfullyEvent());

    Intent intent = new Intent(SignInActivity.this, SetupProfileActivity.class);
    startActivity(intent);
    finish();
  }

  @Override public void openMainActivity(List<AuthenticationResponseChart> chartsMap) {
    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
        (ArrayList<AuthenticationResponseChart>) chartsMap);
    startActivity(intent);
    finish();
  }
}
