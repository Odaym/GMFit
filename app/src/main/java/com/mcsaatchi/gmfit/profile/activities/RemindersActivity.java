package com.mcsaatchi.gmfit.profile.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.RemindersStatusChangedEvent;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.AlarmReceiver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RemindersActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.breakfastReminderLayout) RelativeLayout breakfastReminderLayout;
  @Bind(R.id.lunchReminderLayout) RelativeLayout lunchReminderLayout;
  @Bind(R.id.dinnerReminderLayout) RelativeLayout dinnerReminderLayout;
  @Bind(R.id.enableRemindersSwitch) Switch enableRemindersSwitch;
  @Bind(R.id.breakfastReminderValueTV) TextView breakfastReminderValueTV;
  @Bind(R.id.lunchReminderValueTV) TextView lunchReminderValueTV;
  @Bind(R.id.dinnerReminderValueTV) TextView dinnerReminderValueTV;

  private boolean areAlarmsEnabled = false;

  private String breakfastAlarmTime;
  private String lunchAlarmTime;
  private String dinnerAlarmTime;

  public static void setupMealRemindersAlarm(Context context, SharedPreferences prefs,
      String mealType, String finalTime) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(finalTime.split(":")[0]));
    calendar.set(Calendar.MINUTE, Integer.parseInt(finalTime.split(":")[1]));
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Intent intent = new Intent(context, AlarmReceiver.class);
    intent.putExtra(Constants.EXTRAS_ALARM_TYPE, "meals");

    PendingIntent pendingIntent = null;

    /**
     * Concatenate the alarm instance ID to the final time of the alarm,
     * so that it can be grabbed later
     */
    switch (mealType) {
      case "Breakfast":
        intent.putExtra("MEAL_TYPE", "Breakfast");
        pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, finalTime).apply();
        break;
      case "Lunch":
        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.LUNCH_REMINDER_ALARM_TIME, finalTime).apply();
        break;
      case "Dinner":
        intent.putExtra("MEAL_TYPE", "Dinner");
        pendingIntent =
            PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.DINNER_REMINDER_ALARM_TIME, finalTime).apply();
        break;
    }

    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reminders);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.set_reminders_entry), true);

    areAlarmsEnabled = prefs.getBoolean(Constants.ARE_ALARMS_ENABLED, false);

    breakfastAlarmTime = prefs.getString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am");
    lunchAlarmTime = prefs.getString(Constants.LUNCH_REMINDER_ALARM_TIME, "02:45:pm");
    dinnerAlarmTime = prefs.getString(Constants.DINNER_REMINDER_ALARM_TIME, "08:00:pm");

    breakfastReminderValueTV.setText(breakfastAlarmTime.split(":")[0]
        + ":"
        + breakfastAlarmTime.split(":")[1]
        + " "
        + breakfastAlarmTime.split(":")[2]);

    lunchReminderValueTV.setText(lunchAlarmTime.split(":")[0]
        + ":"
        + lunchAlarmTime.split(":")[1]
        + " "
        + lunchAlarmTime.split(":")[2]);

    dinnerReminderValueTV.setText(dinnerAlarmTime.split(":")[0]
        + ":"
        + dinnerAlarmTime.split(":")[1]
        + " "
        + dinnerAlarmTime.split(":")[2]);

    if (prefs.getBoolean(Constants.ARE_ALARMS_ENABLED, true)) {
      enableRemindersSwitch.setChecked(true);
    } else {
      enableRemindersSwitch.setChecked(false);
    }

    enableRemindersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
          triggerAlarmsState(true);

          setupMealRemindersAlarm(RemindersActivity.this, prefs, "Breakfast", breakfastAlarmTime);
          setupMealRemindersAlarm(RemindersActivity.this, prefs, "Lunch", lunchAlarmTime);
          setupMealRemindersAlarm(RemindersActivity.this, prefs, "Dinner", dinnerAlarmTime);
        } else {
          triggerAlarmsState(false);
          cancelAllPendingAlarms();
        }

        EventBusSingleton.getInstance()
            .post(new RemindersStatusChangedEvent(areAlarmsEnabled));
      }
    });

    /**
     * BREAKFAST TIMER
     */
    breakfastReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        breakfastAlarmTime = prefs.getString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am");
        final String[] timeValues = breakfastAlarmTime.split(":");

        TimePickerDialog timePicker =
            new TimePickerDialog(RemindersActivity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTimeForAlarm = formatFinalTime(selectedHour, selectedMinute);

                String finalTimeForDisplay =
                    reverseFormatFinalTime(selectedHour + ":" + selectedMinute);

                String[] timeValuesForDisplay = finalTimeForDisplay.split(":");

                breakfastReminderValueTV.setText(timeValuesForDisplay[0]
                    + ":"
                    + timeValuesForDisplay[1]
                    + " "
                    + timeValuesForDisplay[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(RemindersActivity.this, prefs, "Breakfast",
                      finalTimeForAlarm);
                }
              }
            }, Integer.parseInt(timeValues[0]), Integer.parseInt(timeValues[1]), false);

        timePicker.show();
      }
    });

    /**
     * LUNCH TIMER
     */
    lunchReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        lunchAlarmTime = prefs.getString(Constants.LUNCH_REMINDER_ALARM_TIME, "02:45:pm");
        String[] timeValues = lunchAlarmTime.split(":");

        TimePickerDialog timePicker =
            new TimePickerDialog(RemindersActivity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTimeForAlarm = formatFinalTime(selectedHour, selectedMinute);

                String finalTimeForDisplay =
                    reverseFormatFinalTime(selectedHour + ":" + selectedMinute);

                String[] timeValuesForDisplay = finalTimeForDisplay.split(":");

                lunchReminderValueTV.setText(timeValuesForDisplay[0]
                    + ":"
                    + timeValuesForDisplay[1]
                    + " "
                    + timeValuesForDisplay[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(RemindersActivity.this, prefs, "Lunch",
                      finalTimeForAlarm);
                }
              }
            }, Integer.parseInt(timeValues[0]), Integer.parseInt(timeValues[1]), false);

        timePicker.show();
      }
    });

    /**
     * DINNER TIMER
     */
    dinnerReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        dinnerAlarmTime = prefs.getString(Constants.DINNER_REMINDER_ALARM_TIME, "08:00:pm");

        String[] timeValues = dinnerAlarmTime.split(":");

        TimePickerDialog timePicker =
            new TimePickerDialog(RemindersActivity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTimeForAlarm = formatFinalTime(selectedHour, selectedMinute);

                String finalTimeForDisplay =
                    reverseFormatFinalTime(selectedHour + ":" + selectedMinute);

                String[] timeValuesForDisplay = finalTimeForDisplay.split(":");

                dinnerReminderValueTV.setText(timeValuesForDisplay[0]
                    + ":"
                    + timeValuesForDisplay[1]
                    + " "
                    + timeValuesForDisplay[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(RemindersActivity.this, prefs, "Dinner",
                      finalTimeForAlarm);
                }
              }
            }, Integer.parseInt(timeValues[0]), Integer.parseInt(timeValues[1]), false);

        timePicker.show();
      }
    });
  }

  private String formatFinalTime(int hour, int minute) {
    String finalTime = "";

    try {
      final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
      final Date dateObj;

      dateObj = sdf.parse(hour + ":" + minute);
      finalTime = new SimpleDateFormat("HH:mm:a").format(dateObj);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return finalTime;
  }

  private String reverseFormatFinalTime(String originalTime) {
    String finalTime = "";

    try {
      final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
      final Date dateObj;

      dateObj = sdf.parse(originalTime);
      finalTime = new SimpleDateFormat("hh:mm:a").format(dateObj);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return finalTime;
  }

  private void cancelAllPendingAlarms() {
    Intent intent = new Intent(RemindersActivity.this, AlarmReceiver.class);
    PendingIntent breakfastPendingAlarm =
        PendingIntent.getBroadcast(RemindersActivity.this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent lunchPendingAlarm = PendingIntent.getBroadcast(RemindersActivity.this, 1, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent dinnerPendingAlarm = PendingIntent.getBroadcast(RemindersActivity.this, 2, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    breakfastPendingAlarm.cancel();
    lunchPendingAlarm.cancel();
    dinnerPendingAlarm.cancel();
  }

  private void triggerAlarmsState(boolean enabled) {
    if (enabled) {
      prefs.edit().putBoolean(Constants.ARE_ALARMS_ENABLED, true).apply();
      areAlarmsEnabled = true;
    } else {
      prefs.edit().putBoolean(Constants.ARE_ALARMS_ENABLED, false).apply();
      areAlarmsEnabled = false;
    }
  }
}
