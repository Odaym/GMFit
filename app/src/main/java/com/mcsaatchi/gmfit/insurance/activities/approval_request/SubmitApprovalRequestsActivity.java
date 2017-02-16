package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SubmitApprovalRequestsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.reimbursementSubcategory) CustomPicker subcategory;
  @Bind(R.id.reimbursementServiceDate) CustomPicker serviceDate;
  @Bind(R.id.categoryInOutToggle) CustomToggle categoryToggle;
  @Bind(R.id.submitApprovalRequestBTN) Button submitApprovalRequestBTN;
  private List<SubCategoriesResponseDatum> subCategoriesList;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_approval_requests_submit);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Approval Request", true);

    getSubCategories();

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

  private void getSubCategories() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    dataAccessHandler.getSubCategories("1892870", new Callback<SubCategoriesResponse>() {
      @Override public void onResponse(Call<SubCategoriesResponse> call,
          Response<SubCategoriesResponse> response) {
        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            subCategoriesList = response.body().getData().getBody().getData();
            String[] finalCategoryNames = new String[subCategoriesList.size()];

            for (int i = 0; i < subCategoriesList.size(); i++) {
              finalCategoryNames[i] = subCategoriesList.get(i).getName();
            }

            subcategory.setUpDropDown("Subcategory", "Choose a subcategory", finalCategoryNames,
                new CustomPicker.OnDropDownClickListener() {
                  @Override public void onClick(int index, String selected) {

                  }
                });
        }
      }

      @Override public void onFailure(Call<SubCategoriesResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }
}
