package com.mcsaatchi.gmfit.insurance.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import java.util.Calendar;

import static com.mcsaatchi.gmfit.insurance.widget.CustomPicker.CustomPickerType.DatePicker;
import static com.mcsaatchi.gmfit.insurance.widget.CustomPicker.CustomPickerType.NonSelected;

public class CustomPicker extends LinearLayout implements View.OnClickListener {
  private static final String TAG = CustomPicker.class.getSimpleName();
  private TextView itemNameTv;
  private TextView itemSelectedTv;
  private Context context;
  private AlertDialog.Builder builder;
  private DatePickerDialog datePickerDialog;
  private CustomPickerType customPickerType;

  public CustomPicker(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.context = context;
    customPickerType = NonSelected;
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.custom_picker, this, true);
    itemNameTv = (TextView) v.findViewById(R.id.item_name);
    itemSelectedTv = (TextView) v.findViewById(R.id.item_selected);
    View touchableContainer = v.findViewById(R.id.touchableContainer);
    touchableContainer.setOnClickListener(this);
  }

  public void setUpDropDown(String itemName, String selected, String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    customPickerType = CustomPickerType.DropDownPicker;
    setItemName(itemName);
    setSelected(selected);
    setDropDownItems(itemName, items, onDropDownClickListener);
  }

  public void setUpDatePicker(String itemName, String selected,
      OnDatePickerClickListener onDatePickerClickListener) {
    customPickerType = DatePicker;
    setItemName(itemName);
    setSelected(selected);
    setDatePicker(onDatePickerClickListener);
  }

  private void setDatePicker(final OnDatePickerClickListener datePickerClickListener) {
    final Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);
    int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
      @Override public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        datePickerClickListener.dateSet(year, month, dayOfMonth);
      }
    }, hour, minute, dayOfMonth);
  }

  private void setItemName(String itemName) {
    itemNameTv.setText(itemName);
  }

  private void setSelected(String selected) {
    itemSelectedTv.setText(selected);
  }

  private void setDropDownItems(String itemName, final String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    builder = new AlertDialog.Builder(context);
    builder.setTitle("Pick " + itemName).setItems(items, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialogInterface, int i) {
        setSelected(items[i]);
        onDropDownClickListener.onClick(i, items[i]);
      }
    });
    builder.create();
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.touchableContainer:
        switch (customPickerType) {
          case DatePicker:
            datePickerDialog.show();
            break;
          case DropDownPicker:
            builder.show();
            break;
          default:
            Log.w(TAG, "Set up a picker type");
        }
    }
  }

  enum CustomPickerType {
    DropDownPicker, DatePicker, NonSelected
  }

  public interface OnDropDownClickListener {
    void onClick(int index, String selected);
  }

  public interface OnDatePickerClickListener {
    void dateSet(int year, int month, int dayOfMonth);
  }
}