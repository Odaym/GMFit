package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.presenters.AccountVerificationActivityPresenter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class AccountVerificationActivity extends BaseActivity
    implements AccountVerificationActivityPresenter.AccountVerificationActivityView {

  @Bind(R.id.getStartedIMG) ImageView getStartedIMG;
  @Bind(R.id.verifyCodeET) FormEditText verifyCodeET;

  private AccountVerificationActivityPresenter presenter;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_account_verification);

    ButterKnife.bind(this);

    presenter = new AccountVerificationActivityPresenter(this, dataAccessHandler);

    allFields.add(verifyCodeET);

    Picasso.with(this)
        .load(R.drawable.verification_banner)
        .transform(new CircleTransform())
        .into(getStartedIMG);
  }

  @OnClick(R.id.setupProfileBTN) public void handleSetupProfile() {
    if (Helpers.validateFields(allFields)) {
      presenter.setupProfile(verifyCodeET.getText().toString());
    }
  }

  @Override public void openSetupProfileActivity() {
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();

    Intent intent = new Intent(AccountVerificationActivity.this, SetupProfileActivity.class);
    startActivity(intent);
    finish();
  }

  @Override public void displayWrongCodeDialog() {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.verifying_email_dialog_title);
    alertDialog.setMessage(getString(R.string.wrong_verification_code));
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();
    });
    alertDialog.show();
  }
}
