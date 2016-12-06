package com.mcsaatchi.gmfit.pedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

  @Override public void onReceive(final Context context, final Intent intent) {
    context.startService(new Intent(context, SensorListener.class));
  }
}