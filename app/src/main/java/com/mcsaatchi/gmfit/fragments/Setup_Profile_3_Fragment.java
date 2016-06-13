package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

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
    private String finalDateOfBirth = "";

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
        finalDateOfBirth = year + "-" + monthOfYear + "-" + dayOfMonth;
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_USER_FINALIZE_SETUP_PROFILE:
                if (Helpers.validateFields(allFields)) {
                    try {
                        final JSONObject jsonForRequest = new JSONObject();
                        jsonForRequest.put(Cons.REQUEST_PARAM_NAME, firstNameET.getText().toString() + " " + lastNameET.getText().toString());
                        if (!finalDateOfBirth.isEmpty())
                            jsonForRequest.put(Cons.REQUEST_PARAM_BIRTHDAY, finalDateOfBirth);

                        jsonForRequest.put(Cons.REQUEST_PARAM_BLOOD_TYPE, bloodTypeSpinner.getSelectedItem());

                        boolean finalGender;

                        finalGender = genderSpinner.getSelectedItem().toString().equals("Male");

                        jsonForRequest.put(Cons.REQUEST_PARAM_GENDER, finalGender);
                        jsonForRequest.put(Cons.REQUEST_PARAM_HEIGHT, heightET.getText().toString());
                        jsonForRequest.put(Cons.REQUEST_PARAM_WEIGHT, weightET.getText().toString());
                        jsonForRequest.put(Cons.REQUEST_PARAM_BMI, calculateBMI(Double.parseDouble(weightET.getText().toString()), Double.parseDouble(heightET.getText().toString())));

//                        ApiHelper.runApiAsyncTask(getActivity(), Cons.API_NAME_UPDATE_PROFILE, Cons.POST_REQUEST_TYPE, jsonForRequest, R.string
//                                .setting_up_profile_dialog_title, R.string.setting_up_profile_dialog_message, new Callable<Void>() {
//                            @Override
//                            public Void call() throws Exception {
//                                Intent intent = new Intent(getActivity(), Main_Activity.class);
//                                startActivity(intent);
//                                getActivity().finish();
//
//                                return null;
//                            }
//                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
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
}
