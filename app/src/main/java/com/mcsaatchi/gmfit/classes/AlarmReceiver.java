package com.mcsaatchi.gmfit.classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewMealItem_Activity;
import com.mcsaatchi.gmfit.activities.Reminders_Activity;
import org.joda.time.LocalDate;

public class AlarmReceiver extends BroadcastReceiver {
  private int notificationID = 0;

  @Override public void onReceive(Context context, Intent intent) {
    long when = System.currentTimeMillis();

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    String mealType = intent.getExtras().getString("MEAL_TYPE");

    Intent openMealEntryPicker = new Intent(context, AddNewMealItem_Activity.class);
    openMealEntryPicker.putExtra(Constants.EXTRAS_MAIN_MEAL_NAME, mealType);
    openMealEntryPicker.putExtra(Constants.EXTRAS_DATE_TO_ADD_MEAL_ON,
        Helpers.prepareDateForAPIRequest(new LocalDate()));
    openMealEntryPicker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(openMealEntryPicker);

    Intent notificationIntent = new Intent(context, Reminders_Activity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,
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

    notificationManager.notify(notificationID, mNotifyBuilder.build());
    notificationID++;
  }
}