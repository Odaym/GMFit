package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;

public class ItemHeader extends LinearLayout {
  public ItemHeader(Context context, AttributeSet attrs) {
    super(context, attrs);
    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.item_header, this, true);
    TextView tv = v.findViewById(R.id.headerText);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemHeader, 0, 0);
    String headerText = a.getString(R.styleable.ItemHeader_header_text);
    tv.setText(headerText);
    a.recycle();
  }
}
