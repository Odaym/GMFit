package com.mcsaatchi.gmfit.onboarding.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.UserFinalizedSetupProfileEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.onboarding.adapters.MedicalConditionsSpinnerAdapter;
import com.mcsaatchi.gmfit.onboarding.adapters.TextualSpinnersAdapter;
import com.mcsaatchi.gmfit.onboarding.presenters.SetupProfileFragmentsPresenter;
import com.squareup.otto.Subscribe;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;

public class SetupProfile4Fragment extends BaseFragment
    implements SetupProfileFragmentsPresenter.SetupProfileFragmentsView_4,
    CalendarDatePickerDialogFragment.OnDateSetListener {

  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

  @Bind(R.id.dateOfBirthTV) TextView dateOfBirthTV;
  @Bind(R.id.weightET) FormEditText weightET;
  @Bind(R.id.heightET) FormEditText heightET;
  @Bind(R.id.bloodTypeSpinner) Spinner bloodTypeSpinner;
  @Bind(R.id.genderSpinner) Spinner genderSpinner;
  @Bind(R.id.medicalConditionsSpinner) Spinner medicalConditionsSpinner;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private SetupProfileFragmentsPresenter presenter;

  private float finalWeight = 0, finalHeight = 0;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private String dateOfBirth = "";

  private InputMethodManager inputMethodManager;

  private ArrayList<String> bloodTypeItems = new ArrayList<String>() {{
    add("O-");
    add("O+");
    add("A-");
    add("A+");
    add("B-");
    add("B+");
    add("AB-");
    add("AB+");
  }};

  private ArrayList<String> genderItems = new ArrayList<String>() {{
    add("Male");
    add("Female");
  }};

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_4, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    presenter = new SetupProfileFragmentsPresenter(this, dataAccessHandler);

    //When going back and forth within the 3 Setup Profile fragments, don't reregister the Eventbus
    try {
      EventBusSingleton.getInstance().register(this);
    } catch (IllegalArgumentException ignored) {
    }

    initCustomSpinner(genderItems, genderSpinner);

    initCustomSpinner(bloodTypeItems, bloodTypeSpinner);

    presenter.getMedicalConditions();

    inputMethodManager =
        (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

    allFields.add(weightET);
    allFields.add(heightET);

    weightET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!charSequence.toString().isEmpty()) {
          finalWeight = Float.parseFloat(charSequence.toString());
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });

    heightET.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!charSequence.toString().isEmpty()) {
          finalHeight = Float.parseFloat(charSequence.toString());
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });

    return fragmentView;
  }

  @OnClick(R.id.dateOfBirthTV) public void handleChooseDateOfBirth() {
    CalendarDatePickerDialogFragment cdp =
        new CalendarDatePickerDialogFragment().setOnDateSetListener(SetupProfile4Fragment.this)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setDoneText(getString(R.string.accept_ok))
            .setCancelText(getString(R.string.decline_cancel))
            .setPreselectedDate(2000, 0, 1)
            .setThemeLight();
    cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
  }

  @Override
  public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
      int dayOfMonth) {
    dateOfBirthTV.setText(
        new DateFormatSymbols().getMonths()[monthOfYear] + " " + dayOfMonth + ", " + year);
  }

  @Subscribe public void handleFinalizeSetupProfile(UserFinalizedSetupProfileEvent event) {
    int finalGender;

    finalGender = genderSpinner.getSelectedItem().toString().equals("Male") ? 0 : 1;

    String finalDateOfBirth;

    if (!dateOfBirth.isEmpty()) {
      finalDateOfBirth = dateOfBirth;
    } else {
      finalDateOfBirth = "1990-01-01";
    }

    String finalBloodType = bloodTypeSpinner.getSelectedItem().toString();

    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH, finalDateOfBirth).apply();
    prefs.edit().putInt(Constants.EXTRAS_USER_PROFILE_GENDER, finalGender).apply();
    prefs.edit().putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, finalWeight).apply();
    prefs.edit().putFloat(Constants.EXTRAS_USER_PROFILE_HEIGHT, finalHeight).apply();
    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE, finalBloodType).apply();

    /**
     * These values are from previous steps 1 and 2 in the Setup Profile process
     */
    String nationality = prefs.getString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, "");
    String measurementSystem =
        prefs.getString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "");
    int goalId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, 0);
    int activityLevelId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, 0);
    int medicalConditionId = Integer.parseInt(
        ((MedicalConditionsResponseDatum) medicalConditionsSpinner.getSelectedItem()).getId());

    prefs.edit()
        .putInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID, medicalConditionId)
        .apply();
    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION,
            ((MedicalConditionsResponseDatum) medicalConditionsSpinner.getSelectedItem()).getName())
        .apply();

    presenter.setupUserProfile(finalDateOfBirth, finalBloodType, nationality, medicalConditionId,
        measurementSystem, goalId, activityLevelId, finalGender, finalHeight, finalWeight);
  }

  private void initCustomSpinner(ArrayList<String> listItems, Spinner spinner) {
    TextualSpinnersAdapter medicalConditionsSpinnerAdapter =
        new TextualSpinnersAdapter(getActivity(), listItems);
    spinner.setAdapter(medicalConditionsSpinnerAdapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    spinner.setOnTouchListener((v, event) -> {
      inputMethodManager.hideSoftInputFromWindow(weightET.getWindowToken(), 0);

      return false;
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  public float getFinalWeight() {
    return finalWeight;
  }

  public float getFinalHeight() {
    return finalHeight;
  }

  @Override public void populateMedicalConditionsSpinner(
      ArrayList<MedicalConditionsResponseDatum> allMedicalData) {
    MedicalConditionsSpinnerAdapter medicalConditionsSpinnerAdapter =
        new MedicalConditionsSpinnerAdapter(getActivity(), allMedicalData);
    medicalConditionsSpinner.setAdapter(medicalConditionsSpinnerAdapter);
    medicalConditionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    medicalConditionsSpinner.setOnTouchListener((v, event) -> {
      inputMethodManager.hideSoftInputFromWindow(weightET.getWindowToken(), 0);

      return false;
    });
  }

  @Override public void openMainActivity(List<AuthenticationResponseChart> chartsMap) {
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();

    Intent intent = new Intent(getActivity(), MainActivity.class);
    intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
        (ArrayList<AuthenticationResponseChart>) chartsMap);
    startActivity(intent);

    getActivity().finish();
  }
}
