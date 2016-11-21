package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;

public class Reminders_Activity extends Base_Activity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.breakfastReminderLayout) RelativeLayout breakfastReminderLayout;
  @Bind(R.id.lunchReminderLayout) RelativeLayout lunchReminderLayout;
  @Bind(R.id.dinnerReminderLayout) RelativeLayout dinnerReminderLayout;
  @Bind(R.id.enableRemindersSwitch) Switch enableRemindersSwitch;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reminders);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.set_reminders_entry), true);

    final RadialTimePickerDialogFragment rtpd =
        new RadialTimePickerDialogFragment().setForced12hFormat()
            .setThemeDark()
            .setDoneText(getResources().getString(R.string.ok))
            .setCancelText(getResources().getString(R.string.decline_cancel));

    enableRemindersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
          Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Breakfast", 4, 9);
          Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Lunch", 4, 10);
          Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Dinner", 4, 11);
        }
      }
    });

    /**
     * BREAKFAST TIMER
     */
    breakfastReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        rtpd.setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
          @Override
          public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
            rtpd.setStartTime(10, 0);

            Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Breakfast", hourOfDay,
                minute);
          }
        });

        rtpd.show(getSupportFragmentManager(), null);
      }
    });

    /**
     * LUNCH TIMER
     */
    lunchReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        rtpd.setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
          @Override
          public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
            rtpd.setStartTime(2, 45);

            Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Lunch", hourOfDay, minute);
          }
        });

        rtpd.show(getSupportFragmentManager(), null);
      }
    });

    /**
     * DINNER TIMER
     */
    dinnerReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        rtpd.setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
          @Override
          public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
            rtpd.setStartTime(9, 0);

            Helpers.setupMealRemindersAlarm(Reminders_Activity.this, "Dinner", hourOfDay, minute);
          }
        });

        rtpd.show(getSupportFragmentManager(), null);
      }
    });
  }
}
