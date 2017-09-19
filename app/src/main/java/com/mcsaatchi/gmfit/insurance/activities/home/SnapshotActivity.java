package com.mcsaatchi.gmfit.insurance.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.PDFViewerActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
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
      final String[] items =
          new String[] { getString(R.string.snapshot_last_3_months), getString(R.string.snapshot_last_6_months), getString(
                        R.string.snapshot_last_12_months), getString(R.string.snapshot_last_24_months) };

      AlertDialog.Builder builder = new AlertDialog.Builder(SnapshotActivity.this);
      builder.setTitle(R.string.period_dialog_title).setItems(items, (dialogInterface, i) -> {
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

  @Override public void saveAndOpenPDF(ResponseBody responseBody) {
    Helpers.setPDFResponseBody(responseBody);

    Intent intent = new Intent(this, PDFViewerActivity.class);
    intent.putExtra("PDF_FILE_NAME", "GM Fit - "
        + prefs.getString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME, "")
        + " - Snapshot.pdf");
    startActivity(intent);
  }
}
