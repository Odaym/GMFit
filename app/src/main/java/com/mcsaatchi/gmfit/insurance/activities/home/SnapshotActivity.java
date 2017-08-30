package com.mcsaatchi.gmfit.insurance.activities.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.io.File;
import okhttp3.ResponseBody;
import org.joda.time.LocalDate;

public class SnapshotActivity extends BaseActivity
    implements SnapshotActivityPresenter.SnapshotActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.selectPeriodTV) TextView selectPeriodTV;

  private SnapshotActivityPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_snapshot);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, getString(R.string.snapshot_activity_title),
        true);

    presenter = new SnapshotActivityPresenter(this, dataAccessHandler);

    selectPeriodTV.setText(Helpers.formatInsuranceDate(new LocalDate()));

    selectPeriodTV.setOnClickListener(view -> {
      final String[] items = new String[] { "3 months", "6 months", "12 months", "24 months" };

      AlertDialog.Builder builder = new AlertDialog.Builder(SnapshotActivity.this);
      builder.setTitle("Pick Period").setItems(items, (dialogInterface, i) -> {
        presenter.getSnapshot(items[i],
            prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
        selectPeriodTV.setText(items[i]);
      });
      builder.create();
      builder.show();
    });

    presenter.getSnapshot(selectPeriodTV.getText().toString(),
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
  }

  @Override public void saveAndOpenPDF(ResponseBody responseBody, String PDFname) {
    Helpers.writeResponseBodyToDisk(responseBody, PDFname);

    File file = new File(Environment.getExternalStorageDirectory()
        + File.separator
        + "GMFit"
        + File.separator
        + PDFname);

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    startActivity(intent);
  }
}
