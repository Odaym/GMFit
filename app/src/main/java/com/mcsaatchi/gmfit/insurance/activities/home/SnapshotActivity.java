package com.mcsaatchi.gmfit.insurance.activities.home;

import android.app.ProgressDialog;
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
import com.mcsaatchi.gmfit.architecture.rest.CertainPDFResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SnapshotActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.webView) WebView webView;
  @Bind(R.id.selectPeriodTV) TextView selectPeriodTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_snapshot);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, getString(R.string.snapshot_activity_title),
        true);

    selectPeriodTV.setText(Helpers.formatInsuranceDate(new LocalDate()));

    selectPeriodTV.setOnClickListener(view -> {
      final String[] items =
          new String[] { "1 month", "3 months", "6 months", "12 months", "24 months" };

      AlertDialog.Builder builder = new AlertDialog.Builder(SnapshotActivity.this);
      builder.setTitle("Pick currency")
          .setItems(items, (dialogInterface, i) -> selectPeriodTV.setText(items[i]));
      builder.create();
      builder.show();
    });

    getSnapshot(selectPeriodTV.getText().toString());
  }

  private void getSnapshot(String periodSelected) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getSnapshot(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        periodSelected, new Callback<CertainPDFResponse>() {
          @Override public void onResponse(Call<CertainPDFResponse> call,
              Response<CertainPDFResponse> response) {
            switch (response.code()) {
              case 200:
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

                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + response.body()
                    .getData()
                    .getBody()
                    .getData());

                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<CertainPDFResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(SnapshotActivity.this).create();
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }
}
