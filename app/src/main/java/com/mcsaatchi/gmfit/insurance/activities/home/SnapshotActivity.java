package com.mcsaatchi.gmfit.insurance.activities.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import org.joda.time.LocalDate;

public class SnapshotActivity extends BaseActivity
    implements SnapshotActivityPresenter.SnapshotActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.webView) WebView webView;
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
          new String[] { "1 month", "3 months", "6 months", "12 months", "24 months" };

      AlertDialog.Builder builder = new AlertDialog.Builder(SnapshotActivity.this);
      builder.setTitle("Pick currency").setItems(items, (dialogInterface, i) -> {
        presenter.getSnapshot(items[i], prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
        selectPeriodTV.setText(items[i]);
      });
      builder.create();
      builder.show();
    });

    presenter.getSnapshot(selectPeriodTV.getText().toString(), prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));
  }

  @Override public void displaySnapshotPDF(String pdfData) {
    WebSettings settings = webView.getSettings();
    settings.setLoadWithOverviewMode(true);
    settings.setBuiltInZoomControls(true);
    settings.setUseWideViewPort(true);
    settings.setJavaScriptEnabled(true);
    settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

    webView.setVerticalScrollBarEnabled(false);
    webView.setHorizontalScrollBarEnabled(false);
    webView.setWebViewClient(new WebViewClient());
    webView.setInitialScale(1);

    webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfData);
  }
}
