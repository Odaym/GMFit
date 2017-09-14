package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;

public class CustomAttachmentPicker extends LinearLayout {
  public static final int REQUEST_PICK_IMAGE_GALLERY = 586;
  public static final int CAPTURE_NEW_PICTURE_REQUEST_CODE = 871;
  @Bind(R.id.headerTextTV) TextView headerTextTV;
  @Bind(R.id.imagePicker1) ImageView imagePicker1;
  @Bind(R.id.imagePicker2) ImageView imagePicker2;
  @Bind(R.id.imagePicker3) ImageView imagePicker3;
  @Bind(R.id.imagePicker4) ImageView imagePicker4;
  @Bind(R.id.imagePicker5) ImageView imagePicker5;
  //@Bind(R.id.deleteImagePicker1) ImageView deleteImagePicker1;
  //@Bind(R.id.deleteImagePicker2) ImageView deleteImagePicker2;
  //@Bind(R.id.deleteImagePicker3) ImageView deleteImagePicker3;
  //@Bind(R.id.deleteImagePicker4) ImageView deleteImagePicker4;
  //@Bind(R.id.deleteImagePicker5) ImageView deleteImagePicker5;

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

  public void hideRemainingImages() {
    imagePicker2.setVisibility(View.GONE);
    imagePicker3.setVisibility(View.GONE);
    imagePicker4.setVisibility(View.GONE);
    imagePicker5.setVisibility(View.GONE);
  }

  //public void showDeletionButton(ArrayList<String> imagePaths, int which) {
  //  switch (which) {
  //    case 0:
  //      deleteImagePicker1.setVisibility(View.VISIBLE);
  //
  //      deleteImagePicker1.setOnClickListener(view -> {
  //        imagePicker1.setImageResource(R.drawable.ic_camera_icon_attachments);
  //        deleteImagePicker1.setVisibility(View.GONE);
  //        imagePaths.remove(which);
  //      });
  //      break;
  //    case 1:
  //      deleteImagePicker2.setVisibility(View.VISIBLE);
  //
  //      deleteImagePicker2.setOnClickListener(view -> {
  //        imagePicker2.setImageResource(R.drawable.ic_camera_icon_attachments);
  //        deleteImagePicker2.setVisibility(View.GONE);
  //        imagePaths.remove(which - 1);
  //      });
  //      break;
  //    case 2:
  //      deleteImagePicker3.setVisibility(View.VISIBLE);
  //
  //      deleteImagePicker3.setOnClickListener(view -> {
  //        imagePicker3.setImageResource(R.drawable.ic_camera_icon_attachments);
  //        deleteImagePicker3.setVisibility(View.GONE);
  //        imagePaths.remove(which - 2);
  //      });
  //      break;
  //    case 3:
  //      deleteImagePicker4.setVisibility(View.VISIBLE);
  //
  //      deleteImagePicker4.setOnClickListener(view -> {
  //        imagePicker4.setImageResource(R.drawable.ic_camera_icon_attachments);
  //        deleteImagePicker4.setVisibility(View.GONE);
  //        imagePaths.remove(which - 3);
  //      });
  //      break;
  //    case 4:
  //      deleteImagePicker5.setVisibility(View.VISIBLE);
  //
  //      deleteImagePicker5.setOnClickListener(view -> {
  //        imagePicker5.setImageResource(R.drawable.ic_camera_icon_attachments);
  //        deleteImagePicker5.setVisibility(View.GONE);
  //        imagePaths.remove(which - 4);
  //      });
  //      break;
  //  }
  //}

  public ImageView returnImagePicker(int position) {
    switch (position) {
      case 0:
        return imagePicker1;
      case 1:
        return imagePicker2;
      case 2:
        return imagePicker3;
      case 3:
        return imagePicker4;
      case 4:
        return imagePicker5;
    }

    return null;
  }
}