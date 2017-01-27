package com.mcsaatchi.gmfit.onboarding.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.UserFinalizedSetupProfileEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalConditionsResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.onboarding.adapters.MedicalConditionsSpinnerAdapter;
import com.mcsaatchi.gmfit.onboarding.adapters.TextualSpinnersAdapter;
import com.squareup.otto.Subscribe;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SetupProfile4Fragment extends Fragment
    implements CalendarDatePickerDialogFragment.OnDateSetListener {

  private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
  private static final String TAG = "SetupProfile4Fragment";

  @Bind(R.id.dateOfBirthTV) TextView dateOfBirthTV;
  @Bind(R.id.weightET) FormEditText weightET;
  @Bind(R.id.heightET) FormEditText heightET;
  @Bind(R.id.bloodTypeSpinner) Spinner bloodTypeSpinner;
  @Bind(R.id.genderSpinner) Spinner genderSpinner;
  @Bind(R.id.medicalConditionsSpinner) Spinner medicalConditionsSpinner;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

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

    //When going back and forth within the 3 Setup Profile fragments, don't reregister the Eventbus
    try {
      EventBusSingleton.getInstance().register(this);
    } catch (IllegalArgumentException ignored) {
    }

    initCustomSpinner(genderItems, genderSpinner);

    initCustomSpinner(bloodTypeItems, bloodTypeSpinner);

    getAndPopulateMedicalConditions();

    inputMethodManager =
        (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

    allFields.add(weightET);
    allFields.add(heightET);

    dateOfBirthTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        CalendarDatePickerDialogFragment cdp =
            new CalendarDatePickerDialogFragment().setOnDateSetListener(SetupProfile4Fragment.this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setDoneText(getString(R.string.accept_ok))
                .setCancelText(getString(R.string.decline_cancel))
                .setPreselectedDate(2000, 0, 1)
                .setThemeLight();
        cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
      }
    });

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

    setupUserProfile(finalDateOfBirth, finalBloodType, nationality, medicalConditionId,
        measurementSystem, goalId, activityLevelId, finalGender, finalHeight, finalWeight);
  }

  private void getAndPopulateMedicalConditions() {
    dataAccessHandler.getMedicalConditions(new Callback<MedicalConditionsResponse>() {
      @Override public void onResponse(Call<MedicalConditionsResponse> call,
          Response<MedicalConditionsResponse> response) {
        switch (response.code()) {
          case 200:
            ArrayList<MedicalConditionsResponseDatum> allMedicalData =
                (ArrayList<MedicalConditionsResponseDatum>) response.body()
                    .getData()
                    .getBody()
                    .getData();

            initMedicalConditionsSpinner(allMedicalData, medicalConditionsSpinner);

            break;
          case 401:

            break;
        }
      }

      @Override public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {

      }
    });
  }

  private void setupUserProfile(String finalDateOfBirth, String bloodType, String nationality,
      int medical_condition, String measurementSystem, int goalId, int activityLevelId,
      int finalGender, double height, double weight) {

    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.signing_up_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.signing_up_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.updateUserProfile(finalDateOfBirth, bloodType, nationality, medical_condition,
        measurementSystem, goalId, activityLevelId, finalGender, height, weight, "1",
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                getUiForSection(waitingDialog, "fitness");
                break;
              case 401:
                alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                alertDialog.show();
                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getActivity().getResources()
                .getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void getUiForSection(final ProgressDialog waitingDialog, String section) {
    dataAccessHandler.getUiForSection("http://gmfit.mcsaatchi.me/api/v1/user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                    (ArrayList<AuthenticationResponseChart>) chartsMap);
                startActivity(intent);

                getActivity().finish();

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getActivity().getResources()
                .getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
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

    spinner.setOnTouchListener(new View.OnTouchListener() {

      @Override public boolean onTouch(View v, MotionEvent event) {
        inputMethodManager.hideSoftInputFromWindow(weightET.getWindowToken(), 0);

        return false;
      }
    });
  }

  private void initMedicalConditionsSpinner(ArrayList<MedicalConditionsResponseDatum> listItems,
      Spinner spinner) {
    MedicalConditionsSpinnerAdapter medicalConditionsSpinnerAdapter =
        new MedicalConditionsSpinnerAdapter(getActivity(), listItems);
    spinner.setAdapter(medicalConditionsSpinnerAdapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    spinner.setOnTouchListener(new View.OnTouchListener() {

      @Override public boolean onTouch(View v, MotionEvent event) {
        inputMethodManager.hideSoftInputFromWindow(weightET.getWindowToken(), 0);

        return false;
      }
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
}
