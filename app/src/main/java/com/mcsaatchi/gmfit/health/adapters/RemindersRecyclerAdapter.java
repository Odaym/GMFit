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
import com.mcsaatchi.gmfit.health.models.ReminderTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemindersRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<ReminderTime> reminderTimes;

  public RemindersRecyclerAdapter(Context context, List<ReminderTime> reminderTimes) {
    this.context = context;
    this.reminderTimes = reminderTimes;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_reminder_row, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.bind(reminderTimes.get(position));
  }

  @Override public int getItemCount() {
    return reminderTimes.size();
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

  private class ViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout clickableLayout;
    private TextView reminderValueTV, reminderLabelTV;

    public ViewHolder(View itemView) {
      super(itemView);

      clickableLayout = (LinearLayout) itemView.findViewById(R.id.clickableLayout);
      reminderValueTV = (TextView) itemView.findViewById(R.id.reminderValueTV);
      reminderLabelTV = (TextView) itemView.findViewById(R.id.reminderLabelTV);
    }

    public void bind(final ReminderTime reminderItem) {
      reminderValueTV.setText(reminderItem.getFullTime());
      reminderLabelTV.setText("Reminder " + (getAdapterPosition() + 1));

      clickableLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

          String[] timeValues = reminderValueTV.getText().toString().split(":");

          TimePickerDialog timePicker =
              new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                  String finalTimeForAlarm = formatFinalTime(selectedHour, selectedMinute);

                  String finalTimeForDisplay =
                      reverseFormatFinalTime(selectedHour + ":" + selectedMinute);

                  String[] timeValuesForDisplay = finalTimeForDisplay.split(":");

                  reminderValueTV.setText(timeValuesForDisplay[0]
                      + ":"
                      + timeValuesForDisplay[1]
                      + " "
                      + timeValuesForDisplay[2]);

                  //if (areAlarmsEnabled) {
                  //  setupMealRemindersAlarm(RemindersActivity.this, prefs, "Dinner",
                  //      finalTimeForAlarm);
                  //}
                }
              }, Integer.parseInt(timeValues[0]), Integer.parseInt(timeValues[1].split(" ")[0]), false);

          timePicker.show();
        }
      });
    }
  }
}
