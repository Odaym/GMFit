package com.mcsaatchi.gmfit.common.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.health.models.MedicationReminder;
import com.mcsaatchi.gmfit.nutrition.activities.AddNewMealItemActivity;
import org.joda.time.LocalDate;
import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver {
  private int mealNotificationID = 0;

  @Override public void onReceive(Context context, Intent intent) {
    long when = System.currentTimeMillis();

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

          NotificationCompat.Builder mNotifyBuilder =
              (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(
                  R.drawable.app_logo)
                  .setContentTitle(mealType + " meals")
                  .setContentText("Remember to add what you had for " + mealType + " today!")
                  .setSound(alarmSound)
                  .setAutoCancel(true)
                  .setWhen(when)
                  .setContentIntent(pendingIntent)
                  .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

          notificationManager.notify(mealNotificationID, mNotifyBuilder.build());
          mealNotificationID++;
          break;
        case "medications":
          MedicationReminder medReminder =
              intent.getExtras().getParcelable(Constants.EXTRAS_MEDICATION_REMINDER_ITEM);

          if (medReminder != null) {
            Timber.d("Reminder just triggered : " + medReminder.toString());
          }

          break;
      }
    }
  }
}