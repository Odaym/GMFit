package com.mcsaatchi.gmfit.profile.activities;

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
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;

public class ChangePasswordActivity extends BaseActivity
    implements ChangePasswordActivityPresenter.ChangePasswordActivityView {
  @Bind(R.id.oldPasswordET) FormEditText oldPasswordET;
  @Bind(R.id.retypeNewPasswordET) FormEditText retypeNewPasswordET;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private ChangePasswordActivityPresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_change_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.change_password_activity_title), true);

    presenter = new ChangePasswordActivityPresenter(this, dataAccessHandler);

    //allFields.add(oldPasswordET);
    allFields.add(retypeNewPasswordET);
    allFields.add(newPasswordET);

    oldPasswordET.setTypeface(Typeface.DEFAULT);
    retypeNewPasswordET.setTypeface(Typeface.DEFAULT);
    newPasswordET.setTypeface(Typeface.DEFAULT);
  }

  void fibonacci(int tracy) {
    int n1 = 0;
    int n2 = 1;
    int n3;
    int i;
    int count = 10;

    System.out.print(n1 + " " + n2);//printing 0 and 1

    for (i = 2; i < count; ++i)//loop starts from 2 because 0 and 1 are already printed

    {
      n3 = n1 + n2;
      System.out.print(" " + n3);
      n1 = n2;
      n2 = n3;
    }
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
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(
            getResources().getString(R.string.old_password_conflict_existing_password));
        alertDialog.show();
      } else if (!newPasswordET.getText()
          .toString()
          .equals(retypeNewPasswordET.getText().toString())) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getResources().getString(R.string.old_password_retype_no_match));
        alertDialog.show();

        newPasswordET.setText("");
        retypeNewPasswordET.setText("");
      } else if (oldPasswordET.getText().toString().equals(newPasswordET.getText().toString())) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getResources().getString(R.string.conflicting_old_new_password));
        alertDialog.show();
      } else {
        presenter.changePassword(oldPasswordET.getText().toString(),
            newPasswordET.getText().toString());
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void displaySuccessToastAndSavePassword(String new_password) {
    prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, new_password).apply();

    Toast.makeText(ChangePasswordActivity.this, getString(R.string.password_change_successful),
        Toast.LENGTH_SHORT).show();
    finish();
  }
}
