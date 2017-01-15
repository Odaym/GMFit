package com.mcsaatchi.gmfit.health.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.models.MedicationReminder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MedicationRemindersRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<MedicationReminder> medicationReminderTimes;

  public MedicationRemindersRecyclerAdapter(Context context,
      List<MedicationReminder> medicationReminderTimes) {
    this.context = context;
    this.medicationReminderTimes = medicationReminderTimes;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_reminder_row, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.bind(medicationReminderTimes.get(position).getAlarmTime());
  }

  @Override public int getItemCount() {
    return medicationReminderTimes.size();
  }

  public MedicationReminder getItem(int position) {
    return medicationReminderTimes.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout clickableLayout;
    private TextView reminderValueTV, reminderLabelTV;

    public ViewHolder(View itemView) {
      super(itemView);

      clickableLayout = (LinearLayout) itemView.findViewById(R.id.clickableLayout);
      reminderValueTV = (TextView) itemView.findViewById(R.id.reminderValueTV);
      reminderLabelTV = (TextView) itemView.findViewById(R.id.reminderLabelTV);
    }

    public void bind(Calendar alarmTimeForMedication) {
      final DateTimeFormatter timeFormatter =
          DateTimeFormat.forPattern("hh:mm  a").withLocale(Locale.getDefault());

      reminderLabelTV.setText("Reminder " + (getAdapterPosition() + 1));

      reminderValueTV.setText(formatFinalTime(alarmTimeForMedication.get(Calendar.HOUR_OF_DAY),
          alarmTimeForMedication.get(Calendar.MINUTE)));

      clickableLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          LocalDateTime timeForClockDisplay =
              timeFormatter.parseLocalDateTime(reminderValueTV.getText().toString());

          TimePickerDialog timePicker =
              new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                  Calendar calendarInstance = Calendar.getInstance(Locale.getDefault());
                  LocalDateTime timeChosen = new LocalDateTime(calendarInstance.get(Calendar.YEAR),
                      calendarInstance.get(Calendar.MONTH) + 1,
                      calendarInstance.get(Calendar.DAY_OF_WEEK), selectedHour, selectedMinute, 0);

                  reminderValueTV.setText(timeFormatter.print(timeChosen));

                  medicationReminderTimes.get(getAdapterPosition())
                      .getAlarmTime()
                      .set(Calendar.HOUR_OF_DAY, selectedHour);

                  medicationReminderTimes.get(getAdapterPosition())
                      .getAlarmTime()
                      .set(Calendar.MINUTE, selectedMinute);

                  medicationReminderTimes.get(getAdapterPosition())
                      .getAlarmTime()
                      .set(Calendar.SECOND, 0);
                }
              }, Integer.parseInt(timeForClockDisplay.hourOfDay().getAsText()),
                  Integer.parseInt(timeForClockDisplay.minuteOfHour().getAsText()), false);

          timePicker.show();
        }
      });
    }
  }

  private String formatFinalTime(int hour, int minute) {
    String finalTime = "";

    try {
      final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
      final Date dateObj;

      dateObj = sdf.parse(hour + ":" + minute);
      finalTime = new SimpleDateFormat("hh:mm  a").format(dateObj);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return finalTime;
  }
}
