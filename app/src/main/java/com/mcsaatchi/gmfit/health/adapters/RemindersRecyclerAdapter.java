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
import java.util.Calendar;
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
          Calendar currentTime = Calendar.getInstance();
          int hour = currentTime.get(Calendar.HOUR_OF_DAY);
          int minute = currentTime.get(Calendar.MINUTE);
          TimePickerDialog mTimePicker;
          mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
              reminderItem.setMinute(selectedMinute);
              reminderItem.setHour(selectedHour);
              reminderValueTV.setText(selectedHour + ":" + selectedMinute);
            }
          }, hour, minute, true);
          mTimePicker.setTitle("Select Time\n");
          mTimePicker.show();
        }
      });
    }
  }
}
