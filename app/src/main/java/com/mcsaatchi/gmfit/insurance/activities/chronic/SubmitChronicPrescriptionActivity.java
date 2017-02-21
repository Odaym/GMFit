package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class SubmitChronicPrescriptionActivity extends BaseActivity {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chronic_prescription_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Chronic Prescription", true);

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
}