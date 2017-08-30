package com.mcsaatchi.gmfit.insurance.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import java.util.Calendar;

import static com.mcsaatchi.gmfit.insurance.widget.CustomDatePickerFitnessActivity.CustomPickerType.DatePicker;
import static com.mcsaatchi.gmfit.insurance.widget.CustomDatePickerFitnessActivity.CustomPickerType.NonSelected;

public class CustomDatePickerFitnessActivity extends LinearLayout implements View.OnClickListener {
  private static final String TAG = CustomDatePickerFitnessActivity.class.getSimpleName();
  private TextView itemNameTv;
  private TextView itemSelectedTv;
  private Context context;
  private AlertDialog.Builder builder;
  private DatePickerDialog datePickerDialog;
  private CustomPickerType customPickerType;

  public CustomDatePickerFitnessActivity(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.context = context;
    customPickerType = NonSelected;
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.custom_date_picker_fitness_activity, this, true);
    itemNameTv = v.findViewById(R.id.item_name);
    itemSelectedTv = v.findViewById(R.id.item_selected);
    View touchableContainer = v.findViewById(R.id.touchableContainer);
    touchableContainer.setOnClickListener(this);
  }

  public void setUpDatePicker(String itemName, String selected,
      OnDatePickerClickListener onDatePickerClickListener) {
    customPickerType = DatePicker;
    setItemName(itemName);
    setSelectedItem(selected);
    setDatePicker(onDatePickerClickListener);
  }

  private void setDatePicker(final OnDatePickerClickListener datePickerClickListener) {
    final Calendar c = Calendar.getInstance();
    int day = c.get(Calendar.DAY_OF_MONTH);
    int month = c.get(Calendar.MONTH);
    int year = c.get(Calendar.YEAR);
    datePickerDialog = new DatePickerDialog(context,
        (datePicker, year1, month1, dayOfMonth) -> datePickerClickListener.dateSet(year1, month1,
            dayOfMonth), year, month, day);

    c.add(Calendar.YEAR, -25);

    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
  }

  private void setItemName(String itemName) {
    itemNameTv.setText(itemName);
  }

  public void setSelectedItem(String selected) {
    if (!selected.contains("Choose")) itemSelectedTv.setAlpha(1f);

    itemSelectedTv.setText(selected);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.touchableContainer:
        switch (customPickerType) {
          case DatePicker:
            datePickerDialog.show();
            break;
          default:
            Log.w(TAG, "Set up a picker type");
        }
    }
  }

  enum CustomPickerType {
    DatePicker, NonSelected
  }

  public interface OnDatePickerClickListener {
    void dateSet(int year, int month, int dayOfMonth);
  }
}