package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;

public class CustomAttachmentPicker extends LinearLayout {
  public static final int REQUEST_PICK_IMAGE_GALLERY = 586;
  public static final int CAPTURE_NEW_PICTURE_REQUEST_CODE = 871;
  @Bind(R.id.headerText) TextView headerTextTV;

  public CustomAttachmentPicker(Context context, AttributeSet attrs) {
    super(context, attrs);

    LayoutInflater mInflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = mInflater.inflate(R.layout.custom_attachement_picker, this, true);

    ButterKnife.bind(v);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemHeader, 0, 0);
    String headerText = a.getString(R.styleable.ItemHeader_header_text);
    headerTextTV.setText(headerText);
    a.recycle();
  }
}