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
import java.util.Calendar;
import java.util.Locale;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MealRemindersActivity extends BaseActivity {
  private final DateTimeFormatter timeFormatter =
      DateTimeFormat.forPattern("hh:mm a").withLocale(Locale.getDefault());
  private final DateTimeFormatter reverseTimeFormatter =
      DateTimeFormat.forPattern("HH:mm").withLocale(Locale.getDefault());
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
  private LocalTime timeChosen;
  private LocalTime timeForDisplay;

  public static void setupMealRemindersAlarm(Context context, SharedPreferences prefs,
      String mealType, int selectedHour, int selectedMinute, String finalTimeString) {

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
    calendar.set(Calendar.MINUTE, selectedMinute);
    calendar.set(Calendar.SECOND, 0);

    if (calendar.before(Calendar.getInstance())) {
      calendar.add(Calendar.DATE, 1);
    }

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
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, finalTimeString).apply();
        break;
      case "Lunch":
        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.LUNCH_REMINDER_ALARM_TIME, finalTimeString).apply();
        break;
      case "Dinner":
        intent.putExtra("MEAL_TYPE", "Dinner");
        pendingIntent =
            PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.DINNER_REMINDER_ALARM_TIME, finalTimeString).apply();
        break;
    }

    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_meal_reminders);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.set_reminders_entry), true);

    areAlarmsEnabled = prefs.getBoolean(Constants.ARE_ALARMS_ENABLED, false);

    breakfastAlarmTime = prefs.getString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00 AM");
    breakfastReminderValueTV.setText(breakfastAlarmTime);

    lunchAlarmTime = prefs.getString(Constants.LUNCH_REMINDER_ALARM_TIME, "02:45 PM");
    lunchReminderValueTV.setText(lunchAlarmTime);

    dinnerAlarmTime = prefs.getString(Constants.DINNER_REMINDER_ALARM_TIME, "08:00 PM");
    dinnerReminderValueTV.setText(dinnerAlarmTime);

    if (prefs.getBoolean(Constants.ARE_ALARMS_ENABLED, true)) {
      enableRemindersSwitch.setChecked(true);
    } else {
      enableRemindersSwitch.setChecked(false);
    }

    enableRemindersSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
      if (checked) {
        triggerAlarmsState(true);

        setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Breakfast", Integer.parseInt(
            reverseTimeFormatter.print(getLocalTimeFormatted(breakfastAlarmTime)).split(":")[0]),
            Integer.parseInt(reverseTimeFormatter.print(getLocalTimeFormatted(breakfastAlarmTime))
                .split(":")[1]), breakfastAlarmTime);

        setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Lunch", Integer.parseInt(
            reverseTimeFormatter.print(getLocalTimeFormatted(lunchAlarmTime)).split(":")[0]),
            Integer.parseInt(
                reverseTimeFormatter.print(getLocalTimeFormatted(lunchAlarmTime)).split(":")[1]),
            lunchAlarmTime);

        setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Dinner", Integer.parseInt(
            reverseTimeFormatter.print(getLocalTimeFormatted(dinnerAlarmTime)).split(":")[0]),
            Integer.parseInt(
                reverseTimeFormatter.print(getLocalTimeFormatted(dinnerAlarmTime)).split(":")[1]),
            dinnerAlarmTime);
      } else {
        triggerAlarmsState(false);
        cancelAllPendingAlarms();
      }

      EventBusSingleton.getInstance().post(new RemindersStatusChangedEvent(areAlarmsEnabled));
    });

    /**
     * BREAKFAST TIMER
     */
    breakfastReminderLayout.setOnClickListener(view -> {

      breakfastAlarmTime = prefs.getString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "08:00 PM");

      timeForDisplay = getLocalTimeFormatted(breakfastAlarmTime);

      TimePickerDialog timePicker =
          new TimePickerDialog(MealRemindersActivity.this,
              (timePicker13, selectedHour, selectedMinute) -> {
                timeChosen = LocalTime.parse(selectedHour + ":" + selectedMinute,
                    DateTimeFormat.forPattern("HH:mm"));

                breakfastReminderValueTV.setText(timeFormatter.print(timeChosen));

                setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Breakfast", selectedHour,
                    selectedMinute, timeFormatter.print(timeChosen));
              }, Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[0]),
              Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[1]), false);

      timePicker.show();
    });

    /**
     * LUNCH TIMER
     */
    lunchReminderLayout.setOnClickListener(view -> {

      lunchAlarmTime = prefs.getString(Constants.LUNCH_REMINDER_ALARM_TIME, "02:45 PM");

      timeForDisplay = getLocalTimeFormatted(lunchAlarmTime);

      TimePickerDialog timePicker =
          new TimePickerDialog(MealRemindersActivity.this,
              (timePicker12, selectedHour, selectedMinute) -> {
                timeChosen = LocalTime.parse(selectedHour + ":" + selectedMinute,
                    DateTimeFormat.forPattern("HH:mm"));

                lunchReminderValueTV.setText(timeFormatter.print(timeChosen));

                setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Lunch", selectedHour,
                    selectedMinute, timeFormatter.print(timeChosen));
              }, Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[0]),
              Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[1]), false);

      timePicker.show();
    });

    /**
     * DINNER TIMER
     */
    dinnerReminderLayout.setOnClickListener(view -> {

      dinnerAlarmTime = prefs.getString(Constants.DINNER_REMINDER_ALARM_TIME, "08:00 PM");

      timeForDisplay = getLocalTimeFormatted(dinnerAlarmTime);

      TimePickerDialog timePicker =
          new TimePickerDialog(MealRemindersActivity.this,
              (timePicker1, selectedHour, selectedMinute) -> {
                timeChosen = LocalTime.parse(selectedHour + ":" + selectedMinute,
                    DateTimeFormat.forPattern("HH:mm"));

                dinnerReminderValueTV.setText(timeFormatter.print(timeChosen));

                setupMealRemindersAlarm(MealRemindersActivity.this, prefs, "Dinner", selectedHour,
                    selectedMinute, timeFormatter.print(timeChosen));
              }, Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[0]),
              Integer.parseInt(reverseTimeFormatter.print(timeForDisplay).split(":")[1]), false);

      timePicker.show();
    });
  }

  private LocalTime getLocalTimeFormatted(String time) {
    return DateTimeFormat.forPattern("hh:mm a").withLocale(Locale.ENGLISH).parseLocalTime(time);
  }

  private void cancelAllPendingAlarms() {
    Intent intent = new Intent(MealRemindersActivity.this, AlarmReceiver.class);
    PendingIntent breakfastPendingAlarm =
        PendingIntent.getBroadcast(MealRemindersActivity.this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent lunchPendingAlarm = PendingIntent.getBroadcast(MealRemindersActivity.this, 1, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent dinnerPendingAlarm = PendingIntent.getBroadcast(MealRemindersActivity.this, 2, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    breakfastPendingAlarm.cancel();
    lunchPendingAlarm.cancel();
    dinnerPendingAlarm.cancel();
  }

  private void triggerAlarmsState(boolean enabled) {
    if (enabled) {
      prefs.edit().putBoolean(Constants.ARE_ALARMS_ENABLED, true).apply();
    } else {
      prefs.edit().putBoolean(Constants.ARE_ALARMS_ENABLED, false).apply();
    }

    areAlarmsEnabled = enabled;
  }
}
