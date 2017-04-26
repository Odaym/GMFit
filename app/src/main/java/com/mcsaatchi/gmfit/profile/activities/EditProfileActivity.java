package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.ProfileUpdatedEvent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import timber.log.Timber;

public class EditProfileActivity extends BaseActivity
    implements EditProfileActivityPresenter.EditProfileActivityView,
    CalendarDatePickerDialogFragment.OnDateSetListener {

  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.firstNameValueET) EditText firstNameValueET;
  @Bind(R.id.lastNameValueET) EditText lastNameValueET;
  @Bind(R.id.emailValueTV) TextView emailValueTV;
  @Bind(R.id.mobileNumberValueET) EditText mobileNumberValueET;
  @Bind(R.id.genderValueTV) TextView genderValueTV;
  @Bind(R.id.dateOfBirthValueTV) TextView dateOfBirthValueTV;
  @Bind(R.id.bloodTypeValueTV) TextView bloodTypeValueTV;
  @Bind(R.id.weightValueET) EditText weightValueET;
  @Bind(R.id.heightValueET) EditText heightValueET;

  private String finalDOB = null;

  private EditProfileActivityPresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_edit_profile);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.edit_profile_activity_title), true);

    presenter = new EditProfileActivityPresenter(this, dataAccessHandler);

    firstNameValueET.setText(
        prefs.getString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME, "").split(" ")[0]);

    lastNameValueET.setText(
        prefs.getString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME, "").split(" ")[1]);

    emailValueTV.setText(prefs.getString(Constants.EXTRAS_USER_EMAIL, ""));

    mobileNumberValueET.setText(prefs.getString(Constants.EXTRAS_USER_PROFILE_PHONE_NUMBER, ""));

    dateOfBirthValueTV.setText(prefs.getString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH, ""));

    weightValueET.setText(String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));

    heightValueET.setText(String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_HEIGHT, 0)));

    bloodTypeValueTV.setText(prefs.getString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE, ""));

    int gender = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GENDER, 0);
    if (gender == 0) {
      genderValueTV.setText("Male");
    } else {
      genderValueTV.setText("Female");
    }
  }

  @OnClick(R.id.genderLayout) public void handleGenderLayoutPressed() {
    final String[] items = new String[] {
        "Male", "Female"
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick gender")
        .setItems(items, (dialogInterface, i) -> genderValueTV.setText(items[i]));
    builder.create();
    builder.show();
  }

  @OnClick(R.id.bloodTypeLayout) public void handleBloodTypeLayoutPressed() {
    final String[] items = new String[] {
        "O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick a blood type").setItems(items, (dialogInterface, i) -> {
      bloodTypeValueTV.setText(items[i]);
    });
    builder.create();
    builder.show();
  }

  @OnClick(R.id.dateOfBirthLayout) public void handleDateOfBirthLayoutPressed() {
    CalendarDatePickerDialogFragment cdp =
        new CalendarDatePickerDialogFragment().setOnDateSetListener(this)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setDoneText(getString(R.string.accept_ok))
            .setCancelText(getString(R.string.decline_cancel))
            .setPreselectedDate(2000, 0, 1)
            .setThemeLight();
    cdp.show(this.getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.edit_profile, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.saveBTN:
        updateUserProfile();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
      int dayOfMonth) {
    dateOfBirthValueTV.setText(
        new DateFormatSymbols().getMonths()[monthOfYear] + " " + dayOfMonth + ", " + year);

    finalDOB = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
  }

  private void updateUserProfile() {
    String firstNameValue = firstNameValueET.getText().toString();
    String lastNameValue = lastNameValueET.getText().toString();
    String mobileNumberValue = mobileNumberValueET.getText().toString();
    int genderValue = genderValueTV.getText().toString().equals("Male") ? 0 : 1;
    String bloodTypeValue = bloodTypeValueTV.getText().toString();
    String weightValue = weightValueET.getText().toString();
    String heightValue = heightValueET.getText().toString();

    String DOBToSend = "";

    if (finalDOB == null) {
      try {
        Date unformattedDOB = new SimpleDateFormat("MMM dd, yyyy").parse(
            prefs.getString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH, ""));

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

        DOBToSend = dt.format(unformattedDOB).toString();

        Timber.d(DOBToSend + " is date");
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else {
      DOBToSend = finalDOB;
    }

    presenter.updateUserProfileExplicitly(
        Helpers.toRequestBody(firstNameValue + " " + lastNameValue),
        Helpers.toRequestBody(mobileNumberValue),
        Helpers.toRequestBody(String.valueOf(genderValue)), Helpers.toRequestBody(DOBToSend),
        Helpers.toRequestBody(bloodTypeValue), Helpers.toRequestBody(String.valueOf(heightValue)),
        Helpers.toRequestBody(String.valueOf(weightValue)));
  }

  @Override public void handleSuccessfulProfileUpdate() {
    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_PHONE_NUMBER,
            mobileNumberValueET.getText().toString())
        .apply();

    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH,
            dateOfBirthValueTV.getText().toString())
        .apply();

    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME,
            firstNameValueET + " " + lastNameValueET)
        .apply();

    prefs.edit()
        .putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT,
            Float.parseFloat(weightValueET.getText().toString()))
        .apply();

    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE, bloodTypeValueTV.getText().toString())
        .apply();

    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_GENDER, genderValueTV.getText().toString())
        .apply();

    prefs.edit()
        .putFloat(Constants.EXTRAS_USER_PROFILE_HEIGHT,
            Float.parseFloat(heightValueET.getText().toString()))
        .apply();

    EventBusSingleton.getInstance().post(new ProfileUpdatedEvent());

    finish();
  }
}
