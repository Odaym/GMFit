package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.AddCRMNoteResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponseNoteAttribute;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class InquiryNotesActivityPresenter extends BaseActivityPresenter {
  private InquiryNotesActivityView view;
  private DataAccessHandler dataAccessHandler;

  InquiryNotesActivityPresenter(InquiryNotesActivityView view, DataAccessHandler dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody) {
    dataAccessHandler.addCRMNote(incidentId, subject, noteText, mimeType, fileName, documentBody,
        new Callback<AddCRMNoteResponse>() {

          @Override public void onResponse(Call<AddCRMNoteResponse> call,
              Response<AddCRMNoteResponse> response) {

            switch (response.code()) {
              case 200:
                view.clearViews();

                getCRMIncidentNotes(incidentId);
                break;
              case 449:
                view.displayRequestErrorDialog(
                    Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            view.dismissWaitingDialog();
          }

          @Override public void onFailure(Call<AddCRMNoteResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  void getCRMIncidentNotes(String incidentId) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getCRMIncidentNotes(incidentId, new Callback<CRMNotesResponse>() {
      @Override
      public void onResponse(Call<CRMNotesResponse> call, Response<CRMNotesResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayCRMIncidentNotes(response.body().getData().getBody().getData().getNoteAttributesLst());
            break;
          case 449:
            view.displayRequestErrorDialog(
                Helpers.provideErrorStringFromJSON(response.errorBody()));
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<CRMNotesResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface InquiryNotesActivityView extends BaseActivityView {
    void dismissWaitingDialog();

    void clearViews();

    void displayCRMIncidentNotes(List<CRMNotesResponseNoteAttribute> noteAttributesList);
  }
}
