package com.mcsaatchi.gmfit.profile.fragments;

import android.os.AsyncTask;
import android.os.Environment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MetaTextsResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.common.fragments.BaseFragmentPresenter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MainProfileFragmentPresenter extends BaseFragmentPresenter {
  private MainProfileFragmentView view;
  private DataAccessHandlerImpl dataAccessHandler;

  MainProfileFragmentPresenter(MainProfileFragmentView view,
      DataAccessHandlerImpl dataAccessHandler) {
    this.view = view;
    this.dataAccessHandler = dataAccessHandler;
  }

  void getUserProfile() {
    dataAccessHandler.getUserProfile(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        switch (response.code()) {
          case 200:
            UserProfileResponseDatum userProfileData =
                response.body().getData().getBody().getData();

            view.populateUserProfileInformation(userProfileData);
            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getMetaTexts(final String section) {
    view.callDisplayWaitingDialog(R.string.loading_data_dialog_title);

    dataAccessHandler.getMetaTexts(section, new Callback<MetaTextsResponse>() {
      @Override
      public void onResponse(Call<MetaTextsResponse> call, Response<MetaTextsResponse> response) {
        switch (response.code()) {
          case 200:
            view.openMetaTextsActivity(response.body().getData().getBody(), section);
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<MetaTextsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void updateUserProfile(RequestBody finalDateOfBirth, RequestBody bloodType,
      RequestBody nationality, HashMap<String, RequestBody> medicalConditions,
      RequestBody measurementSystem, RequestBody goalId, RequestBody activityLevelId,
      RequestBody finalGender, RequestBody height, RequestBody weight, RequestBody onboard,
      boolean profilePictureChanged) {

    view.callDisplayWaitingDialog(R.string.updating_user_profile_dialog_title);

    dataAccessHandler.updateUserProfile(finalDateOfBirth, bloodType, nationality, medicalConditions,
        measurementSystem, goalId, activityLevelId, finalGender, height, weight, onboard,
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                if (profilePictureChanged) view.updateProfilePicture();
                break;
            }

            view.callDismissWaitingDialog();
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            view.displayRequestErrorDialog(t.getMessage());
          }
        });
  }

  void updateUserPicture(HashMap<String, RequestBody> profilePictureParts) {
    dataAccessHandler.updateUserPicture(profilePictureParts, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayPictureChangeSuccessful();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void signOutUser() {
    view.callDisplayWaitingDialog(R.string.signing_out_dialog_title);

    dataAccessHandler.signOutUser(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            view.wipeCredentialsOnSignOut();
            break;
        }

        view.callDismissWaitingDialog();
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void getUserAchievements() {
    dataAccessHandler.getUserAchievements(new Callback<AchievementsResponse>() {
      @Override public void onResponse(Call<AchievementsResponse> call,
          Response<AchievementsResponse> response) {
        switch (response.code()) {
          case 200:
            view.displayUserAchievements(response.body().getData().getBody());
            break;
        }
      }

      @Override public void onFailure(Call<AchievementsResponse> call, Throwable t) {
        view.displayRequestErrorDialog(t.getMessage());
      }
    });
  }

  void requestEmergencyProfile() {
    new DownloadPDFFile().execute("https://mobileapp.globemedfit.com/api/v1/emergency_response",
        "my_emergency_profile.pdf");
  }

  interface MainProfileFragmentView extends BaseFragmentView {
    void populateUserProfileInformation(UserProfileResponseDatum userProfileData);

    void wipeCredentialsOnSignOut();

    void openMetaTextsActivity(String metaContents, String section);

    void updateProfilePicture();

    void displayPictureChangeSuccessful();

    void openShareFileDialog();

    void displayUserAchievements(List<AchievementsResponseBody> achievementsResponseBodies);
  }

  private class DownloadPDFFile extends AsyncTask<String, Void, Void> {
    @Override protected Void doInBackground(String... strings) {
      String fileUrl = strings[0];
      String fileName = strings[1];
      String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
      File folder = new File(extStorageDirectory, "GMFit");
      folder.mkdir();

      File pdfFile = new File(folder, fileName);

      try {
        pdfFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }

      FileDownloader.downloadFile(fileUrl, pdfFile);

      return null;
    }

    @Override protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      view.openShareFileDialog();
    }
  }

  private static class FileDownloader {
    private static final int MEGABYTE = 1024 * 1024;

    static void downloadFile(String fileUrl, File directory) {
      try {

        URL url = new URL(fileUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(directory);

        byte[] buffer = new byte[MEGABYTE];
        int bufferLength = 0;
        while ((bufferLength = inputStream.read(buffer)) > 0) {
          fileOutputStream.write(buffer, 0, bufferLength);
        }

        fileOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
