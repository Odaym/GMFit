package com.mcsaatchi.gmfit.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
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
    @Bind(R.id.dateOfBirthBTN)
    Button dateOfBirthBTN;
    @Bind(R.id.firstNameET)
    FormEditText firstNameET;
    @Bind(R.id.lastNameET)
    FormEditText lastNameET;
    @Bind(R.id.weightET)
    FormEditText weightET;
    @Bind(R.id.heightET)
    FormEditText heightET;
    @Bind(R.id.genderSpinner)
    Spinner genderSpinner;
    @Bind(R.id.bloodTypeSpinner)
    Spinner bloodTypeSpinner;
    private SharedPreferences prefs;
    private ArrayList<FormEditText> allFields = new ArrayList<>();
    private String dateOfBirth = "";

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

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        allFields.add(firstNameET);
        allFields.add(lastNameET);
        allFields.add(weightET);
        allFields.add(heightET);

        dateOfBirthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(Setup_Profile_3_Fragment.this)
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setDoneText(getString(R.string.accept_ok))
                        .setCancelText(getString(R.string.decline_cancel))
                        .setThemeDark(true);
                cdp.show(getActivity().getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        return fragmentView;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(getActivity(), "Date : " + dayOfMonth + " - " + monthOfYear + " " + year, Toast.LENGTH_SHORT).show();
        dateOfBirthBTN.setText(year + " / " + monthOfYear + " / " + dayOfMonth);
        dateOfBirth = year + "-" + monthOfYear + "-" + dayOfMonth;
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_USER_FINALIZE_SETUP_PROFILE:
                if (Helpers.validateFields(allFields)) {
                    String finalName = firstNameET.getText().toString() + " " + lastNameET.getText().toString();

                    int finalGender;

                    finalGender = genderSpinner.getSelectedItem().toString().equals("Male") ? 1 : 0;

                    Log.d(TAG, "handle_BusEvents: finalGender is : " + finalGender);

                    String finalDateOfBirth = null;

                    if (!dateOfBirth.isEmpty())
                        finalDateOfBirth = dateOfBirth;

                    double finalWeight = Double.parseDouble(weightET.getText().toString());
                    double finalHeight = Double.parseDouble(heightET.getText().toString());

                    String finalBloodType = bloodTypeSpinner.getSelectedItem().toString();

                    setupUserProfile(finalName, finalDateOfBirth, finalBloodType, finalGender, finalHeight,
                            finalWeight, calculateBMI(finalWeight, finalHeight));
                }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
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
