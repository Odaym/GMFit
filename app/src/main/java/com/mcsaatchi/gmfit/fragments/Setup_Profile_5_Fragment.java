package com.mcsaatchi.gmfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;

import java.util.Calendar;

public class Setup_Profile_5_Fragment extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private Button dateOfBirthBTN;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_5, container, false);

        dateOfBirthBTN = (Button) fragmentView.findViewById(R.id.dateOfBirthBTN);

        dateOfBirthBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(Setup_Profile_5_Fragment.this)
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
        dateOfBirthBTN.setText(dayOfMonth + " - " + monthOfYear + " " + year);
    }
}
