package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;

public class SubmitInquiryActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.categoryPicker) CustomPicker categoryPicker;
  @Bind(R.id.subCategoryPicker) CustomPicker subCategoryPicker;
  @Bind(R.id.areaPicker) CustomPicker areaPicker;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Complaint/Inquiry", true);

    categoryPicker.setUpDropDown("Category", "Choose a category",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    subCategoryPicker.setUpDropDown("SubCategory", "Choose a subcategory",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    areaPicker.setUpDropDown("Area", "Choose an area",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });
  }
}
