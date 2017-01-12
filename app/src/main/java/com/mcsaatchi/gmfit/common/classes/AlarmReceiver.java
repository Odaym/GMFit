package com.mcsaatchi.gmfit.common.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.health.models.MedicationReminder;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealItemActivity;
import java.util.Calendar;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
  private int mealNotificationID = 0;

  private DBHelper dbHelper;
  private RuntimeExceptionDao<MedicationReminder, Integer> medicationReminderDAO;

  @Override public void onReceive(Context context, Intent intent) {
    long when = System.currentTimeMillis();

    dbHelper = new DBHelper(context);

    medicationReminderDAO = dbHelper.getMedicationReminderDAO();

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    String alarmType = intent.getExtras().getString(Constants.EXTRAS_ALARM_TYPE);

    if (alarmType != null) {
      switch (alarmType) {
        case "meals":
          String mealType = intent.getExtras().getString("MEAL_TYPE");

          Intent openMealEntryPickerIntent = new Intent(context, AddNewMealItemActivity.class);
          openMealEntryPickerIntent.putExtra(Constants.EXTRAS_MAIN_MEAL_NAME, mealType);
          openMealEntryPickerIntent.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON,
              Helpers.prepareDateForAPIRequest(new LocalDate()));
          openMealEntryPickerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

          PendingIntent pendingIntent =
              PendingIntent.getActivity(context, mealNotificationID, openMealEntryPickerIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);

          Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

          NotificationCompat.Builder notifyBuilder =
              (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(
                  R.drawable.app_logo)
                  .setContentTitle(mealType + " meals")
                  .setContentText("Remember to add what you had for " + mealType + " today!")
                  .setSound(alarmSound)
                  .setAutoCancel(true)
                  .setWhen(when)
                  .setContentIntent(pendingIntent)
                  .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

          notificationManager.notify(mealNotificationID, notifyBuilder.build());
          mealNotificationID++;
          break;
        case "medications":
          MedicationReminder medReminder =
              intent.getExtras().getParcelable(Constants.EXTRAS_MEDICATION_REMINDER_ITEM);

          Calendar calendar = Calendar.getInstance(Locale.getDefault());
          calendar.setTimeInMillis(System.currentTimeMillis());

          DateTime timeNow = new DateTime();

          Timber.d("Inside the receiver");

          if (medReminder != null) {
            for (int i = 0; i < medReminder.getDays_of_week().length; i++) {
              if (medReminder.getDays_of_week()[i] != 0) {
                calendar.set(Calendar.DAY_OF_WEEK, medReminder.getDays_of_week()[i]);
                calendar.set(Calendar.HOUR_OF_DAY, medReminder.getHour());
                calendar.set(Calendar.MINUTE, medReminder.getMinute());
                calendar.set(Calendar.SECOND, medReminder.getSecond());

                medReminder.getDays_of_week()[i] = 0;

                break;
              }
            }

            Timber.d("Time now is : " + timeNow.toString());

            Timber.d("Alarm time supposedly : " + calendar.getTime());

            if (calendar.after(timeNow)) {
              Timber.d("Reminder just triggered : " + medReminder.toString());
              medReminder.setTotalTimesToTrigger(medReminder.getTotalTimesToTrigger() - 1);
              medicationReminderDAO.update(medReminder);
            } else {
              Timber.d("Calendar is before time now");
            }
          }
          //  calendar.set(Calendar.MINUTE, medReminder.getMinute());
          //  calendar.set(Calendar.SECOND, medReminder.getSecond());
          //  calendar.set(Calendar.MILLISECOND, 0);
          //
          //  Intent alarmIntent = new Intent(context, AlarmReceiver.class);
          //  alarmIntent.putExtra(Constants.EXTRAS_ALARM_TYPE, "medications");
          //  alarmIntent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM,
          //      (Parcelable) medReminder);
          //
          //  PendingIntent pendingAlarmIntent =
          //      PendingIntent.getBroadcast(context, medReminder.getId(), alarmIntent,
          //          PendingIntent.FLAG_UPDATE_CURRENT);
          //
          //  AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
          //  am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
          //      AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
          //}
          //
          //Calendar calendar = Calendar.getInstance();
          //calendar.setTimeInMillis(System.currentTimeMillis());
          //
          //break;
      }
    }
  }
}