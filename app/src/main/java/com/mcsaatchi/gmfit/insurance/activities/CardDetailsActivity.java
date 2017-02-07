package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import timber.log.Timber;

public class CardDetailsActivity extends BaseActivity {
  @Bind(R.id.webView) WebView webView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private String pdfUrl;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_card_details);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.card_details_activity_title), true);

    if (getIntent().getExtras() != null) {
      pdfUrl = getIntent().getExtras().getString("PDF");

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

      Timber.d("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);

      webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl);
    }
  }
}
