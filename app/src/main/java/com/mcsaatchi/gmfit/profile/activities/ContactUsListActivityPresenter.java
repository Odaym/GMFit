package com.mcsaatchi.gmfit.profile.activities;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivityPresenter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ContactUsListActivityPresenter extends BaseActivityPresenter {
  private ContactUsListActivityView view;
  private DataAccessHandlerImpl dataAccessHandler;

  ContactUsListActivityPresenter(ContactUsListActivityView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getOperationContacts() {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getOperationContacts(new Callback<OperationContactsResponse>() {
      @Override public void onResponse(Call<OperationContactsResponse> call,
          Response<OperationContactsResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayOperationContacts(response.body().getData().getBody());
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<OperationContactsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  interface ContactUsListActivityView extends BaseActivityView {
    void displayOperationContacts(List<OperationContactsResponseBody>  operationContactsResponseBody);
  }
}
