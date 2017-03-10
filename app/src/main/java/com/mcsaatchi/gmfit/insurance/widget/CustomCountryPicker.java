package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;

import static com.mcsaatchi.gmfit.insurance.widget.CustomCountryPicker.CustomPickerType.NonSelected;

public class CustomCountryPicker extends LinearLayout implements View.OnClickListener {
  private static final String TAG = CustomCountryPicker.class.getSimpleName();
  private TextView itemNameTv;
  private TextView itemSelectedTv;
  private Context context;
  private AlertDialog.Builder builder;
  private CustomPickerType customPickerType;

  public CustomCountryPicker(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    this.context = context;
    customPickerType = NonSelected;
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.insurance_login_country_picker, this, true);
    itemNameTv = (TextView) v.findViewById(R.id.item_name);
    itemSelectedTv = (TextView) v.findViewById(R.id.item_selected);
    View touchableContainer = v.findViewById(R.id.touchableContainer);
    touchableContainer.setOnClickListener(this);
  }

  public void setUpDropDown(String itemName, String selected, String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    customPickerType = CustomPickerType.DropDownPicker;
    setItemName(itemName);
    setSelectedItem(selected);
    setDropDownItems(itemName, items, onDropDownClickListener);
  }

  private void setItemName(String itemName) {
    itemNameTv.setText(itemName);
  }

  public void setSelectedItem(String selected) {
    itemSelectedTv.setText(selected);
  }

  private void setDropDownItems(String itemName, final String[] items,
      final OnDropDownClickListener onDropDownClickListener) {
    builder = new AlertDialog.Builder(context);
    builder.setTitle(itemName).setItems(items, (dialogInterface, i) -> {
      setSelectedItem(items[i]);
      onDropDownClickListener.onClick(i, items[i]);
    });
    builder.create();
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.touchableContainer:
        switch (customPickerType) {
          case DropDownPicker:
            builder.show();
            break;
          default:
            Log.w(TAG, "Set up a picker type");
        }
    }
  }

  enum CustomPickerType {
    DropDownPicker, NonSelected
  }

  public interface OnDropDownClickListener {
    void onClick(int index, String selected);
  }

}