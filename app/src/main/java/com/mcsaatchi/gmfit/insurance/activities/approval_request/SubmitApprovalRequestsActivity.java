package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubmitApprovalRequestsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Bind(R.id.reimbursementSubcategory) CustomPicker subcategory;

  @Bind(R.id.reimbursementServiceDate) CustomPicker serviceDate;

  @Bind(R.id.categoryInOutToggle) CustomToggle categoryToggle;

  @Bind(R.id.submitApprovalRequestBTN) Button submitApprovalRequestBTN;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_submit_approval_requests);
    ButterKnife.bind(this);
    setupToolbar(toolbar, "Submit Approval Request", true);

    subcategory.setUpDropDown("Subcategory", "Choose a subcategory",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    serviceDate.setUpDatePicker("Service Date", "Choose a date",
        new CustomPicker.OnDatePickerClickListener() {
          @Override public void dateSet(int year, int month, int dayOfMonth) {

          }
        });

    categoryToggle.setUp("Category", "Out", "In", new CustomToggle.OnToggleListener() {
      @Override public void selected(String option) {

      }
    });

    submitApprovalRequestBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Toast.makeText(SubmitApprovalRequestsActivity.this, "Submitted successfully",
            Toast.LENGTH_SHORT).show();
        finish();
      }
    });
  }
}
