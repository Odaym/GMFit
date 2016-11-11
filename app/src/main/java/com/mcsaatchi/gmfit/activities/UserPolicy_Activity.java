package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;

public class UserPolicy_Activity extends Base_Activity {
  @Bind(R.id.userPolicyWebView) WebView userPolicyWebView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_user_policy);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.user_policy_activity_title), true);

    if (getIntent().getExtras() != null) {
      String userPolicyString = getIntent().getExtras().getString(Constants.EXTRAS_USER_POLICY);

      WebSettings settings = userPolicyWebView.getSettings();
      settings.setLoadWithOverviewMode(true);
      settings.setBuiltInZoomControls(true);
      settings.setUseWideViewPort(true);
      userPolicyWebView.setVerticalScrollBarEnabled(false);
      userPolicyWebView.setHorizontalScrollBarEnabled(false);

      //No effect
      settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
      userPolicyWebView.setInitialScale(300);

      String finalHTMLString = "<!DOCTYPE html>\n" +
          "<html>\n" +
          "<body>\n" +
          userPolicyString +
          "</body>\n" +
          "</html>\n";

      userPolicyWebView.loadData(finalHTMLString, "text/html", "UTF-8");
    }
  }
}
