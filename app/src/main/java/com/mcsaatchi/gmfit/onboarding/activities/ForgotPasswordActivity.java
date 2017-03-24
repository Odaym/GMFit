package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.presenters.ForgotPasswordActivityPresenter;
import java.util.ArrayList;

public class ForgotPasswordActivity extends BaseActivity
    implements ForgotPasswordActivityPresenter.ForgotPasswordActivityView {

  @Bind(R.id.emailET) FormEditText emailET;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ForgotPasswordActivityPresenter presenter;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_forgot_password);

    ButterKnife.bind(this);

    presenter = new ForgotPasswordActivityPresenter(this, dataAccessHandler);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.forgot_password_activity_title), true);

    allFields.add(emailET);
  }

  @OnClick(R.id.submitForgotPasswordEmailBTN) public void handleForgotPassword() {
    if (Helpers.validateFields(allFields)) {
      presenter.handleForgotPassword(emailET.getText().toString());
    }
  }

  @Override public void openResetPasswordActivity() {
    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
    startActivity(intent);
    finish();
  }
}
