package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Profile;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponse;
import com.mcsaatchi.gmfit.rest.MedicalConditionsResponseDatum;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.squareup.otto.Subscribe;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setup_Profile_3_Fragment extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String TAG = "Setup_Profile_3_Fragment";

    @Bind(R.id.dateOfBirthTV)
    TextView dateOfBirthTV;
    @Bind(R.id.weightET)
    FormEditText weightET;
    @Bind(R.id.heightET)
    FormEditText heightET;
    @Bind(R.id.bloodTypeSpinner)
    Spinner bloodTypeSpinner;
    @Bind(R.id.genderSpinner)
    Spinner genderSpinner;
    @Bind(R.id.medicalConditionsSpinner)
    Spinner medicalConditionsSpinner;

    private SharedPreferences prefs;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_3, container, false);

        ButterKnife.bind(this, fragmentView);

        //When going back and forth within the 3 Setup Profile fragments, don't reregister the Eventbus
        try {
            EventBus_Singleton.getInstance().register(this);
        } catch (IllegalArgumentException ignored) {
        }

        initCustomSpinner(genderItems, genderSpinner);

        initCustomSpinner(bloodTypeItems, bloodTypeSpinner);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        getAndPopulateMedicalConditions();

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        allFields.add(weightET);
        allFields.add(heightET);

        dateOfBirthTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(Setup_Profile_3_Fragment.this)
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDoneText(getString(R.string.accept_ok))
                        .setCancelText(getString(R.string.decline_cancel))
                        .setThemeLight();
                cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        return fragmentView;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        dateOfBirthTV.setText(dayOfMonth + " " + new DateFormatSymbols().getMonths()[monthOfYear - 1] + " " + year);
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_USER_FINALIZE_SETUP_PROFILE:
                if (!weightET.getText().toString().isEmpty() && !heightET.getText().toString().isEmpty()) {
                    double weight = Double.parseDouble(weightET.getText().toString());
                    double height = Double.parseDouble(heightET.getText().toString());

                    int finalGender;

                    finalGender = genderSpinner.getSelectedItem().toString().equals("Male") ? 1 : 0;

                    String finalDateOfBirth = null;

                    if (!dateOfBirth.isEmpty())
                        finalDateOfBirth = dateOfBirth;

                    double finalWeight = Double.parseDouble(weightET.getText().toString());
                    double finalHeight = Double.parseDouble(heightET.getText().toString());

                    String finalBloodType = bloodTypeSpinner.getSelectedItem().toString();

                    setupUserProfile(finalDateOfBirth, finalBloodType, finalGender, finalHeight,
                            finalWeight, calculateBMI(finalWeight, finalHeight));
                }

                break;
        }
    }

    private void getAndPopulateMedicalConditions() {
        Call<MedicalConditionsResponse> getMedicalConditionsCall = new RestClient().getGMFitService().getMedicalConditions(prefs.getString(Cons
                        .PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        getMedicalConditionsCall.enqueue(new Callback<MedicalConditionsResponse>() {
            @Override
            public void onResponse(Call<MedicalConditionsResponse> call, Response<MedicalConditionsResponse> response) {
                Log.d(TAG, "onResponse: Response code is : " + response.code());

                switch (response.code()) {
                    case 200:

                        ArrayList<MedicalConditionsResponseDatum> allMedicalData = (ArrayList<MedicalConditionsResponseDatum>) response.body().getData().getBody().getData();

                        ArrayList<String> medicalConditions = new ArrayList<>();

                        Log.d(TAG, "onResponse: Results size : " + allMedicalData.size());


                        for (int i = 0; i < allMedicalData.size(); i++) {
                            medicalConditions.add(allMedicalData.get(i).getName());
                        }

                        initCustomSpinner(medicalConditions, medicalConditionsSpinner);

                        break;
                    case 401:

                        break;
                }
            }

            @Override
            public void onFailure(Call<MedicalConditionsResponse> call, Throwable t) {
            }
        });

    }

    private void setupUserProfile(String finalDateOfBirth, String bloodType, int finalGender, double height, double weight, double BMI) {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getString(R.string.signing_up_dialog_title));
        waitingDialog.setMessage(getString(R.string.signing_up_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.signing_up_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<DefaultGetResponse> registerUserCall = new RestClient().getGMFitService().updateUserProfile(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN,
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new UpdateProfileRequest(new Profile()));

        registerUserCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        Intent intent = new Intent(getActivity(), Main_Activity.class);
                        startActivity(intent);
                        getActivity().finish();

                        break;
                    case 401:
                        alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                        alertDialog.show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    private double calculateBMI(double weight, double height) {
        //centimeters to meters
        double finalHeight = height / 100;

        return weight / (finalHeight * finalHeight);
    }

    private void initCustomSpinner(ArrayList<String> listItems, Spinner spinner) {
        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getActivity(), listItems);
        spinner.setAdapter(customSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputMethodManager.hideSoftInputFromWindow(weightET.getWindowToken(), 0);

                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> listItems;

        public CustomSpinnerAdapter(Context context, ArrayList<String> listItems) {
            this.listItems = listItems;
            activity = context;
        }


        public int getCount() {
            return listItems.size();
        }

        public Object getItem(int i) {
            return listItems.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(activity);
            txt.setPadding(getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16, 16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(listItems.get(position));
            txt.setTextColor(getResources().getColor(android.R.color.black));
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(activity);
            txt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            txt.setPadding(getResources().getDimensionPixelSize(R.dimen.default_margin_2), 16, 16, 16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            txt.setText(listItems.get(i));
            txt.setTextColor(getResources().getColor(android.R.color.white));
            return txt;
        }

    }

    public class UpdateProfileRequest {
        final String birthday;
        final String bloodType;
        final String nationality;
        final String measurementSystem;
        final String goal;
        final int gender;
        final double height;
        final double weight;
        final double BMI;

        public UpdateProfileRequest(Profile profile) {
            this.birthday = profile.getBirthday();
            this.bloodType = profile.getBloodType();
            this.gender = profile.getGender();
            this.height = profile.getHeight();
            this.weight = profile.getWeight();
            this.BMI = profile.getBMI();
            this.nationality = profile.getNationality();
            this.measurementSystem = profile.getMeasurementSystem();
            this.goal = profile.getGoal();
        }
    }
}
