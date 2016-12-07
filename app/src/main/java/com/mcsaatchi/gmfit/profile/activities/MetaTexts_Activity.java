package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.common.Constants;

public class MetaTexts_Activity extends Base_Activity {
  @Bind(R.id.metaTextsWebView) WebView metaTextsWebView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_meta_texts);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      String metaTextString = getIntent().getExtras().getString(Constants.EXTRAS_META_HTML_CONTENT);

      String activityTitle = getIntent().getExtras().getString(Constants.BUNDLE_ACTIVITY_TITLE);

      setupToolbar(toolbar, activityTitle, true);

      WebSettings settings = metaTextsWebView.getSettings();
      settings.setLoadWithOverviewMode(true);
      settings.setBuiltInZoomControls(true);
      settings.setUseWideViewPort(true);
      metaTextsWebView.setVerticalScrollBarEnabled(false);
      metaTextsWebView.setHorizontalScrollBarEnabled(false);

      //No effect
      settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
      metaTextsWebView.setInitialScale(300);

      String finalHTMLString = "<!DOCTYPE html>\n" +
          "<html>\n" +
          "<body>\n" +
          metaTextString +
          "</body>\n" +
          "</html>\n";

      metaTextsWebView.loadData(finalHTMLString, "text/html", "UTF-8");
    }
  }
}
