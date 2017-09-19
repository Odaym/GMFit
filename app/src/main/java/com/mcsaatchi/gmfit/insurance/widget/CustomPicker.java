package com.mcsaatchi.gmfit.insurance.widget;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
  private ImageView arrowImage;
  private Context context;
  private AlertDialog.Builder builder;
  private DatePickerDialog datePickerDialog;
  private CustomPickerType customPickerType;
  private View touchableContainer;
  private LinearLayout wholeContainer;

  public CustomPicker(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.context = context;
    customPickerType = NonSelected;
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.custom_picker, this, true);
    itemNameTv = v.findViewById(R.id.item_name);
    itemSelectedTv = v.findViewById(R.id.item_selected);
    arrowImage = v.findViewById(R.id.arrowImage);
    touchableContainer = v.findViewById(R.id.touchableContainer);
    wholeContainer = v.findViewById(R.id.wholeContainer);
    touchableContainer.setOnClickListener(this);
  }

  public void setUpDropDown(String itemName, String selected, String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    customPickerType = CustomPickerType.DropDownPicker;
    setItemName(itemName);
    setSelectedItem(selected);
    setDropDownItems(itemName, items, onDropDownClickListener);
  }

  public void setUpDatePicker(String itemName, String selected,
      OnDatePickerClickListener onDatePickerClickListener) {
    customPickerType = DatePicker;
    setItemName(itemName);
    setSelectedItem(selected);
    setDatePicker(onDatePickerClickListener);
  }

  public void hide() {
    wholeContainer.setVisibility(View.GONE);
  }

  public void show() {
    wholeContainer.setVisibility(View.VISIBLE);
  }

  public void setArrowTintColor(int resColor) {
    arrowImage.setColorFilter(ContextCompat.getColor(context, resColor));
  }

  public void setTextColorOnLabels(int resColor) {
    itemNameTv.setTextColor(getResources().getColor(resColor));
    itemSelectedTv.setTextColor(getResources().getColor(resColor));
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
    if (!selected.contains(getContext().getString(R.string.choose_picker_label))) itemSelectedTv.setAlpha(1f);

    itemSelectedTv.setText(selected);
  }

  private void setDropDownItems(String itemName, final String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    builder = new AlertDialog.Builder(context);
    builder.setTitle(getContext().getString(R.string.pick_picker_label) + itemName).setItems(items, (dialogInterface, i) -> {
      setSelectedItem(items[i]);
      onDropDownClickListener.onClick(i, items[i]);
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