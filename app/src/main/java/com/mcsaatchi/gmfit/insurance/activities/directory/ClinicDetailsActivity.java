package com.mcsaatchi.gmfit.insurance.activities.directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.models.Clinic;

public class ClinicDetailsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Bind(R.id.clinicNameTV) TextView clinicNameTV;
  @Bind(R.id.clinicAddressTV) TextView clinicAddressTV;
  @Bind(R.id.phoneTV) TextView phoneTV;
  @Bind(R.id.mobileTV) TextView mobileTV;
  @Bind(R.id.emailAddressTV) TextView emailAddressTV;

  @Bind(R.id.withinNetworkLayout) LinearLayout withinNetworkLayout;
  @Bind(R.id.open247Layout) LinearLayout open247Layout;
  @Bind(R.id.onlineNowLayout) LinearLayout onlineNowLayout;
  @Bind(R.id.mondayTV) TextView workingHoursMonday;
  @Bind(R.id.tuesdayTV) TextView workingHoursTuesday;
  @Bind(R.id.wednesdayTV) TextView workingHoursWednesday;
  @Bind(R.id.thursdayTV) TextView workingHoursThursday;
  @Bind(R.id.fridayTV) TextView workingHoursFriday;
  @Bind(R.id.saturdayTV) TextView workingHoursSaturday;
  @Bind(R.id.sundayTV) TextView workingHoursSunday;

  private Clinic clinicObject;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_clinic_details);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.clinic_details_activity_title), true);

    if (getIntent().getExtras() != null) {
      clinicObject = getIntent().getExtras().getParcelable("CLINIC_OBJECT");

      if (clinicObject != null) {
        clinicNameTV.setText(clinicObject.getName());
        clinicAddressTV.setText(clinicObject.getAddress());

        phoneTV.setText("+961 1 884 001");
        mobileTV.setText("+961 70 770 441");
        emailAddressTV.setText(clinicObject.getName().split(" ")[0] + "_clinic@gmail.com");

        if (clinicObject.isOnline()) onlineNowLayout.setVisibility(View.VISIBLE);

        if (clinicObject.isWithin_network()) withinNetworkLayout.setVisibility(View.VISIBLE);

        if (clinicObject.isOpen_247()) open247Layout.setVisibility(View.VISIBLE);

        if (clinicObject.getOpeningHours().getMonday().equals("closed")) {
          workingHoursMonday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursMonday.setText("closed");
        } else {
          workingHoursMonday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getTuesday().equals("closed")) {
          workingHoursTuesday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursTuesday.setText("closed");
        } else {
          workingHoursTuesday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getWednesday().equals("closed")) {
          workingHoursWednesday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursWednesday.setText("closed");
        } else {
          workingHoursWednesday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getThursday().equals("closed")) {
          workingHoursThursday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursThursday.setText("closed");
        } else {
          workingHoursThursday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getFriday().equals("closed")) {
          workingHoursFriday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursFriday.setText("closed");
        } else {
          workingHoursFriday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getSaturday().equals("closed")) {
          workingHoursSaturday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursSaturday.setText("closed");
        } else {
          workingHoursSaturday.setText(clinicObject.getOpeningHours().getMonday());
        }

        if (clinicObject.getOpeningHours().getSunday().equals("closed")) {
          workingHoursSunday.setTextColor(getResources().getColor(R.color.bpRed));
          workingHoursSunday.setText("closed");
        } else {
          workingHoursSunday.setText(clinicObject.getOpeningHours().getMonday());
        }
      }
    }
  }
}
