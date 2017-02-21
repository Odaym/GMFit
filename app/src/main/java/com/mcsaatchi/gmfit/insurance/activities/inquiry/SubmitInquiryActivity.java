package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SubmitInquiryActivity extends BaseActivity {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.categoryPicker) CustomPicker categoryPicker;
  @Bind(R.id.subCategoryPicker) CustomPicker subCategoryPicker;
  @Bind(R.id.areaPicker) CustomPicker areaPicker;
  private List<SubCategoriesResponseDatum> subCategoriesList;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Complaint/Inquiry", true);

    getSubCategories();

    categoryPicker.setUpDropDown("Category", "Choose a category",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    areaPicker.setUpDropDown("Area", "Choose an area",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA)) {
      requestCapturePermissions(Manifest.permission.CAMERA);
    }
  }

  private void requestCapturePermissions(String missingPermission) {
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
        || !ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this, new String[] { missingPermission },
          REQUEST_CAPTURE_PERMISSIONS);
      return;
    }
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

            subCategoryPicker.setUpDropDown("Subcategory", "Choose a subcategory", finalCategoryNames,
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
