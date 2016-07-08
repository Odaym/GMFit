package com.mcsaatchi.gmfit.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewMedicalCondition_Activity;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    @Bind(R.id.addMedicalConditionsBTN)
    Button addMedicalConditionsBTN;
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

        try {
            EventBus_Singleton.getInstance().register(this);
        } catch (IllegalArgumentException ignored) {
        }

        initCustomSpinner(genderItems, genderSpinner);

        initCustomSpinner(bloodTypeItems, bloodTypeSpinner);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);


//        allFields.add(weightET);
//        allFields.add(heightET);

        addMedicalConditionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewMedicalCondition_Activity.class);
                startActivity(intent);
            }
        });

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


//        Typeface fontRegular = Typeface.create("sans-serif", Typeface.NORMAL);
//        Typeface fontLight = Typeface.create("sans-serif-light", Typeface.NORMAL);

        SpannableStringBuilder SS = new SpannableStringBuilder(getString(R.string.add_medical_condition_button));
        SS.setSpan(Typeface.create("sans-serif-light", Typeface.NORMAL), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        SS.setSpan (new CustomTypefaceSpan("sans-serif", fontRegular), 6, getString(R.string.add_medical_condition_button).length(),Spanned
//                .SPAN_EXCLUSIVE_INCLUSIVE);
//        wordtoSpan.setSpan(fontRegular, 5, getString(R.string.add_medical_condition_button).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        addMedicalConditionsBTN.setText(SS);

//        addMedicalConditionsBTN.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
//        addMedicalConditionsBTN.setText(getString(R.string.add_medical_condition_button));

        return fragmentView;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
//        Toast.makeText(getActivity(), "Date : " + dayOfMonth + " - " + monthOfYear + " " + year, Toast.LENGTH_SHORT).show();
//        dateOfBirthBTN.setText(year + " / " + monthOfYear + " / " + dayOfMonth);
//        dateOfBirth = year + "-" + monthOfYear + "-" + dayOfMonth;
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_USER_FINALIZE_SETUP_PROFILE:
//                if (Helpers.validateFields(allFields)) {
//                    String finalName = firstNameET.getText().toString() + " " + lastNameET.getText().toString();
//
//                    int finalGender;
//
//                    finalGender = genderSpinner.getSelectedItem().toString().equals("Male") ? 1 : 0;
//
//                    Log.d(TAG, "handle_BusEvents: finalGender is : " + finalGender);
//
//                    String finalDateOfBirth = null;
//
//                    if (!dateOfBirth.isEmpty())
//                        finalDateOfBirth = dateOfBirth;
//
//                    double finalWeight = Double.parseDouble(weightET.getText().toString());
//                    double finalHeight = Double.parseDouble(heightET.getText().toString());
//
//                    String finalBloodType = bloodTypeSpinner.getSelectedItem().toString();
//
//                    setupUserProfile(finalName, finalDateOfBirth, finalBloodType, finalGender, finalHeight,
//                            finalWeight, calculateBMI(finalWeight, finalHeight));
//                }

                break;
        }
    }

    private void setupUserProfile(String finalName, String finalDateOfBirth, String bloodType, int finalGender, double height, double weight, double BMI) {
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
                Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new UpdateProfileRequest(finalName, finalDateOfBirth, bloodType, finalGender, height, weight, BMI));

        registerUserCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                if (response.body() != null) {
                    switch (response.code()) {
                        case Cons.API_REQUEST_SUCCEEDED_CODE:
                            waitingDialog.dismiss();

                            Intent intent = new Intent(getActivity(), Main_Activity.class);
                            startActivity(intent);
                            getActivity().finish();

                            break;
                        case Cons.LOGIN_API_WRONG_CREDENTIALS:
                            alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                            alertDialog.show();
                            break;
                    }
                } else {
                    waitingDialog.dismiss();

                    //Handle the error
                    try {
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        JSONObject errorData = errorBody.getJSONObject("data");
                        int errorCodeInData = errorData.getInt("code");

                        Log.d(TAG, "onResponse: Response failed with this : " + errorBody.toString());

//                        if (errorCodeInData == Cons.API_RESPONSE_INVALID_PARAMETERS) {
//                            alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
//                            alertDialog.show();
//                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
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
        final String name;
        final String birthday;
        final String bloodType;
        final int gender;
        final double height;
        final double weight;
        final double BMI;

        public UpdateProfileRequest(String name, String birthday, String bloodType, int gender, double height, double weight, double BMI) {
            this.name = name;
            this.birthday = birthday;
            this.bloodType = bloodType;
            this.gender = gender;
            this.height = height;
            this.weight = weight;
            this.BMI = BMI;
        }
    }
}
