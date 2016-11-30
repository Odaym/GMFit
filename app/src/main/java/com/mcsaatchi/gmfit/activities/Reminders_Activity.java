package com.mcsaatchi.gmfit.activities;

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
import com.mcsaatchi.gmfit.classes.AlarmReceiver;
import com.mcsaatchi.gmfit.classes.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Reminders_Activity extends Base_Activity {
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

    enableRemindersSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
          triggerAlarmsState(true);

          setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Breakfast", breakfastAlarmTime);
          setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Lunch", lunchAlarmTime);
          setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Dinner", dinnerAlarmTime);
        } else {
          triggerAlarmsState(false);
          cancelAllPendingAlarms();
        }
      }
    });

    /**
     * BREAKFAST TIMER
     */
    breakfastReminderLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        breakfastAlarmTime = prefs.getString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am");

        String[] timeValues = breakfastAlarmTime.split(":");

        TimePickerDialog timePicker =
            new TimePickerDialog(Reminders_Activity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTime = formatFinalTime(selectedHour, selectedMinute);

                breakfastAlarmTime = finalTime;

                String[] timeValues = finalTime.split(":");

                breakfastReminderValueTV.setText(
                    timeValues[0] + ":" + timeValues[1] + " " + timeValues[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Breakfast", finalTime);
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
            new TimePickerDialog(Reminders_Activity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTime = formatFinalTime(selectedHour, selectedMinute);

                lunchAlarmTime = finalTime;

                String[] timeValues = finalTime.split(":");

                lunchReminderValueTV.setText(
                    timeValues[0] + ":" + timeValues[1] + " " + timeValues[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Lunch", finalTime);
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
            new TimePickerDialog(Reminders_Activity.this, new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String finalTime = formatFinalTime(selectedHour, selectedMinute);

                dinnerAlarmTime = finalTime;

                String[] timeValues = finalTime.split(":");

                dinnerReminderValueTV.setText(
                    timeValues[0] + ":" + timeValues[1] + " " + timeValues[2]);

                if (areAlarmsEnabled) {
                  setupMealRemindersAlarm(Reminders_Activity.this, prefs, "Dinner", finalTime);
                }
              }
            }, Integer.parseInt(timeValues[0]), Integer.parseInt(timeValues[1]), false);

        timePicker.show();
      }
    });
  }

  private String formatFinalTime(int hour, int minute) {
    String finalTime = "12:45:PM";

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

  private void cancelAllPendingAlarms() {
    Intent intent = new Intent(Reminders_Activity.this, AlarmReceiver.class);
    PendingIntent breakfastPendingAlarm =
        PendingIntent.getBroadcast(Reminders_Activity.this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent lunchPendingAlarm = PendingIntent.getBroadcast(Reminders_Activity.this, 1, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent dinnerPendingAlarm =
        PendingIntent.getBroadcast(Reminders_Activity.this, 2, intent,
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
