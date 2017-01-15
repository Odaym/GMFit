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

          if (medReminder != null) {
            //Timber.d("Medication Reminder ID: "
            //    + medReminder.getId()
            //    + " with time : "
            //    + medReminder.getAlarmTime().get(Calendar.HOUR_OF_DAY)
            //    + medReminder.getAlarmTime().get(Calendar.MINUTE)
            //    + " on day "
            //    + medReminder.getAlarmTime().get(Calendar.DAY_OF_WEEK)
            //    + " for "
            //    + medReminder.getMedication().getName());

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, medReminder.getAlarmTime().get(Calendar.DAY_OF_WEEK));
            cal.set(Calendar.HOUR_OF_DAY, medReminder.getAlarmTime().get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, medReminder.getAlarmTime().get(Calendar.MINUTE));

            if (Calendar.getInstance().compareTo(cal) > 0) {
              Timber.d("MedReminder is after time now");
              if (medReminder.getTotalTimesToTrigger() == 0) {
                Timber.d("Reminder id : " + medReminder.getId() + " about to be deleted");
                medicationReminderDAO.delete(medReminder);
              } else {
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("Medication Reminder")
                        .setContentText("Remember to take your "
                            + medReminder.getMedication().getName()
                            + " medication")
                        .setSound(sound)
                        .setAutoCancel(true)
                        .setWhen(when)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                notificationManager.notify(medReminder.getId(), notificationBuilder.build());

                medReminder.setTotalTimesToTrigger(medReminder.getTotalTimesToTrigger() - 1);
                medicationReminderDAO.update(medReminder);

                Timber.d("Total times remaining for reminder ID : "
                    + medReminder.getId()
                    + " to trigger is : "
                    + medReminder.getTotalTimesToTrigger());
              }
            } else {
              Timber.d("MedReminder is BEFORE time now");
            }
          }
      }
    }
  }
}