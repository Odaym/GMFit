package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponseNoteAttribute;
import com.mcsaatchi.gmfit.architecture.rest.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InquiryDetailsNotesActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.mainNotesLayout) LinearLayout mainNotesLayout;
  @Bind(R.id.attachImageIV) ImageView attachImageIV;

  private InquiriesListResponseInnerData inquiryItem;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_details_notes);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      inquiryItem = getIntent().getExtras().getParcelable("INQUIRY_OBJECT");

      if (inquiryItem != null) {
        setupToolbar(getClass().getSimpleName(), toolbar, inquiryItem.getTitle(), true);

        getCRMIncidentNotes(inquiryItem.getIncidentId());
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.inquiry_details, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.inquiryMoreDetailsBTN:
        Intent intent = new Intent(this, InquiryDetailsActivity.class);
        intent.putExtra("INQUIRY_OBJECT", inquiryItem);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void getCRMIncidentNotes(String incidentId) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getCRMIncidentNotes(incidentId, new Callback<CRMNotesResponse>() {

      @Override
      public void onResponse(Call<CRMNotesResponse> call, Response<CRMNotesResponse> response) {

        switch (response.code()) {
          case 200:
            List<CRMNotesResponseNoteAttribute> noteAttributesList =
                response.body().getData().getBody().getData().getNoteAttributesLst();

            for (int i = 0; i < noteAttributesList.size(); i++) {
              LayoutInflater inflater = getLayoutInflater();

              View noteView = inflater.inflate(R.layout.individual_crm_note, null);

              TextView senderNameTV = (TextView) noteView.findViewById(R.id.senderNameTV);
              TextView sentDateTV = (TextView) noteView.findViewById(R.id.sentDateTV);
              TextView messageContentTV = (TextView) noteView.findViewById(R.id.messageContentTV);
              ImageView messageImageIV = (ImageView) noteView.findViewById(R.id.messageImageIV);

              if (noteAttributesList.get(i).getCreatedBy() != null) {
                senderNameTV.setText(noteAttributesList.get(i).getCreatedBy());
              }

              if (noteAttributesList.get(i).getNoteText() != null) {
                messageContentTV.setText(noteAttributesList.get(i).getNoteText());
              }

              if (noteAttributesList.get(i).getDocumentBody() != null) {
                messageImageIV.setImageBitmap(
                    turnBase64ToImage(noteAttributesList.get(i).getDocumentBody()));
              }

              LinearLayout.LayoutParams params =
                  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                      LinearLayout.LayoutParams.WRAP_CONTENT);

              params.setMargins(0, 0, 0,
                  getResources().getDimensionPixelSize(R.dimen.default_margin_3));
              noteView.setLayoutParams(params);

              mainNotesLayout.addView(noteView);
            }

            break;
          case 449:
            alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
            alertDialog.show();
            break;
        }

        waitingDialog.dismiss();
      }

      @Override public void onFailure(Call<CRMNotesResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  private Bitmap turnBase64ToImage(String base64String) {
    byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }
}
