package com.mcsaatchi.gmfit.onboarding.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.onboarding.presenters.ResetPasswordActivityPresenter;
import java.util.ArrayList;

public class ResetPasswordActivity extends BaseActivity
    implements ResetPasswordActivityPresenter.ResetPasswordActivityView {

  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.verifyCodeET) FormEditText verifyCodeET;
  @Bind(R.id.submitResetPasswordBTN) Button submitResetPasswordBTN;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ResetPasswordActivityPresenter presenter;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reset_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.reset_password_activity_title), true);

    presenter = new ResetPasswordActivityPresenter(this, dataAccessHandler);

    allFields.add(passwordET);
    allFields.add(verifyCodeET);
  }

  @OnClick(R.id.submitResetPasswordBTN) public void handleResetPassword() {
    presenter.resetPassword(verifyCodeET.getText().toString(), passwordET.getText().toString());
  }
}
