package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserPolicy_Activity extends Base_Activity {
    @Bind(R.id.userPolicyWebView)
    WebView userPolicyWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.user_policy_activity_title, true));

        setContentView(R.layout.activity_user_policy);

        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            String userPolicyString = getIntent().getExtras().getString(Cons.EXTRAS_USER_POLICY);

            WebSettings settings = userPolicyWebView.getSettings();
            settings.setLoadWithOverviewMode(true);
            settings.setBuiltInZoomControls(true);
            settings.setUseWideViewPort(true);
            userPolicyWebView.setVerticalScrollBarEnabled(false);
            userPolicyWebView.setHorizontalScrollBarEnabled(false);

            //No effect
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            userPolicyWebView.setInitialScale(1);

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
