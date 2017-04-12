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
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.onboarding.activities.MedicalConditionsChoiceListActivity;
import com.mcsaatchi.gmfit.onboarding.activities.SetupProfileFragmentsPresenter;
import com.mcsaatchi.gmfit.onboarding.adapters.TextualSpinnersAdapter;
import com.mcsaatchi.gmfit.onboarding.models.MedicalCondition;
import com.squareup.otto.Subscribe;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import okhttp3.RequestBody;
import timber.log.Timber;

public class SetupProfile4Fragment extends BaseFragment
    implements SetupProfileFragmentsPresenter.SetupProfileFragmentsView_4,
    CalendarDatePickerDialogFragment.OnDateSetListener {

  public static final int MEDICAL_CONDITIONS_SELECTED = 708;
  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
  @Bind(R.id.dateOfBirthTV) TextView dateOfBirthTV;
  @Bind(R.id.weightET) FormEditText weightET;
  @Bind(R.id.heightET) FormEditText heightET;
  @Bind(R.id.bloodTypeSpinner) Spinner bloodTypeSpinner;
  @Bind(R.id.genderSpinner) Spinner genderSpinner;
  @Bind(R.id.medicalConditionsValueTV) TextView medicalConditionsValueTV;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private SetupProfileFragmentsPresenter presenter;

  private HashMap<String, RequestBody> medicalConditions;
  private ArrayList<Integer> medicalConditionIDs = new ArrayList<>();

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

  @Override
  public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
      int dayOfMonth) {
    dateOfBirthTV.setText(
        new DateFormatSymbols().getMonths()[monthOfYear] + " " + dayOfMonth + ", " + year);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case MEDICAL_CONDITIONS_SELECTED:
        if (data != null) {
          ArrayList<MedicalCondition> conditionsReturned =
              data.getExtras().getParcelableArrayList("MEDICAL_CONDITIONS");

          if (conditionsReturned != null) {
            for (int i = 0; i < conditionsReturned.size(); i++) {
              if (conditionsReturned.get(i).isSelected()) {
                medicalConditionIDs.add(conditionsReturned.get(i).getId());
              }
            }
          }

          medicalConditions = constructMedicalConditionsForRequest(medicalConditionIDs);
        } else {
          Timber.d("Conditions returned is null");
        }

        break;
    }
  }

  @Override public void populateMedicalConditionsSpinner(
      ArrayList<MedicalConditionsResponseDatum> allMedicalData) {

    ArrayList<MedicalCondition> medicalConditions = new ArrayList<>();

    for (int i = 0; i < allMedicalData.size(); i++) {
      if (allMedicalData.get(i).getName() != null) {
        medicalConditions.add(new MedicalCondition(Integer.parseInt(allMedicalData.get(i).getId()),
            allMedicalData.get(i).getName(), false));
      }
    }

    medicalConditionsValueTV.setText(medicalConditions.get(0).getMedicalCondition());

    medicalConditionsValueTV.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity(), MedicalConditionsChoiceListActivity.class);
      intent.putExtra("MEDICAL_CONDITIONS", medicalConditions);
      startActivityForResult(intent, MEDICAL_CONDITIONS_SELECTED);
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

    presenter.setupUserProfile(Helpers.toRequestBody(finalDateOfBirth),
        Helpers.toRequestBody(finalBloodType), Helpers.toRequestBody(nationality),
        medicalConditions, Helpers.toRequestBody(measurementSystem),
        Helpers.toRequestBody(String.valueOf(goalId)),
        Helpers.toRequestBody(String.valueOf(activityLevelId)),
        Helpers.toRequestBody(String.valueOf(finalGender)),
        Helpers.toRequestBody(String.valueOf(finalHeight)),
        Helpers.toRequestBody(String.valueOf(finalWeight)));
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

  private HashMap<String, RequestBody> constructMedicalConditionsForRequest(
      ArrayList<Integer> medicalConditionIDs) {
    HashMap<String, RequestBody> medicalConditionParts = new HashMap<>();

    for (int i = 0; i < medicalConditionIDs.size(); i++) {
      medicalConditionParts.put("medical_conditions[" + i + "]",
          Helpers.toRequestBody(String.valueOf(medicalConditionIDs.get(i))));
    }

    return medicalConditionParts;
  }

  public float getFinalWeight() {
    return finalWeight;
  }

  public float getFinalHeight() {
    return finalHeight;
  }
}
