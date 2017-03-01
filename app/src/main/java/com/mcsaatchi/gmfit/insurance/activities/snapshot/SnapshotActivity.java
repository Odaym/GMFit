package com.mcsaatchi.gmfit.insurance.activities.snapshot;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.SnapshotResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SnapshotActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.webView) WebView webView;
  @Bind(R.id.startDateTV) TextView startDateTV;
  @Bind(R.id.endDateTV) TextView endDateTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_snapshot);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, getString(R.string.snapshot_activity_title),
        true);

    startDateTV.setText(Helpers.formatInsuranceDate(new LocalDate()));

    startDateTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog =
            new DatePickerDialog(SnapshotActivity.this, new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);

                Date d = new Date(calendar.getTimeInMillis());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
                String serviceDateValue = dateFormatter.format(d);
                startDateTV.setText(serviceDateValue);

                getSnapshot(startDateTV.getText().toString(), endDateTV.getText().toString());
              }
            }, year, month, day);

        datePickerDialog.show();
      }
    });

    endDateTV.setText(Helpers.formatInsuranceDate(new LocalDate()));

    endDateTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog =
            new DatePickerDialog(SnapshotActivity.this, new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);

                Date d = new Date(calendar.getTimeInMillis());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
                String serviceDateValue = dateFormatter.format(d);
                endDateTV.setText(serviceDateValue);

                getSnapshot(startDateTV.getText().toString(), endDateTV.getText().toString());
              }
            }, year, month, day);

        datePickerDialog.show();
      }
    });

    getSnapshot(startDateTV.getText().toString(), endDateTV.getText().toString());
  }

  private void getSnapshot(String startDate, String endDate) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    dataAccessHandler.getSnapshot(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        startDate, endDate, new Callback<SnapshotResponse>() {
          @Override
          public void onResponse(Call<SnapshotResponse> call, Response<SnapshotResponse> response) {
            switch (response.code()) {
              case 200:

                waitingDialog.dismiss();

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
                    .getSnapshotPdf());

                break;
            }
          }

          @Override public void onFailure(Call<SnapshotResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(SnapshotActivity.this).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }
}
