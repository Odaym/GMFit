package com.mcsaatchi.gmfit.onboarding.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.SignedInSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.otto.SignedUpSuccessfullyEvent;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.UiResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.MainActivity;
import com.mcsaatchi.gmfit.common.classes.DefaultIndicatorController;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.fragments.IntroSliderFragment;
import com.mcsaatchi.gmfit.onboarding.presenters.LoginActivityPresenter;
import com.squareup.otto.Subscribe;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LoginActivity extends BaseActivity
    implements LoginActivityPresenter.LoginActivityView {

  @Bind(R.id.viewpager) ViewPager viewPager;
  @Bind(R.id.loginFacebookBTN) LoginButton loginFacebookBTN;
  @Bind(R.id.signUpBTN) Button signUpBTN;
  @Bind(R.id.signInBTN) Button signInBTN;

  private LoginActivityPresenter presenter;
  private DefaultIndicatorController indicatorController;
  private CallbackManager callbackManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(Helpers.createActivityBundleWithProperties(0, false));
    Fabric.with(this, new Crashlytics());

    FacebookSdk.sdkInitialize(this);
    callbackManager = CallbackManager.Factory.create();

    setContentView(R.layout.activity_login);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    presenter = new LoginActivityPresenter(this, dataAccessHandler);

    initializeFacebookLogin();

    setupViewPager();
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    EventBusSingleton.getInstance().unregister(this);
  }

  @OnClick(R.id.signInBTN) public void handleSignInClicked() {
    Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.signUpBTN) public void handleSignUpClicked() {
    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
    startActivity(intent);
  }

  @Subscribe public void handleSuccessfulSignUp(SignedUpSuccessfullyEvent event) {
    finish();
  }

  @Subscribe public void handleSuccessfulSignIn(SignedInSuccessfullyEvent event) {
    finish();
  }

  private void setupViewPager() {
    viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
        indicatorController.selectPosition(position);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });

    initController();
  }

  @Override public void initializeFacebookLogin() {
    loginFacebookBTN.setReadPermissions("email", "public_profile", "user_friends");
    loginFacebookBTN.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    loginFacebookBTN.setCompoundDrawablePadding(0);
    loginFacebookBTN.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override public void onSuccess(LoginResult loginResult) {
        final AccessToken accessToken = loginResult.getAccessToken();

        Timber.d("onSuccess: FACEBOOK ACCESS TOKEN IS : %s", accessToken.getToken());

        prefs.edit()
            .putString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, accessToken.getToken())
            .apply();

        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
          try {

            String userID = (String) object.get("id");
            String userName = (String) object.get("name");
            String userEmail = (String) object.get("email");

            SharedPreferences.Editor prefsEditor = prefs.edit();

            prefsEditor.putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true);

            prefsEditor.putString(Constants.EXTRAS_USER_FULL_NAME, userName);
            prefsEditor.putString(Constants.EXTRAS_USER_DISPLAY_PHOTO,
                "https://graph.facebook.com/" + userID + "/picture?type=large");
            prefsEditor.putString(Constants.EXTRAS_USER_EMAIL, userEmail);

            prefsEditor.apply();

            registerUserWithFacebook(accessToken.getToken());
          } catch (JSONException e) {
            e.printStackTrace();
          }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link,birthday,picture");
        request.setParameters(parameters);
        request.executeAsync();
      }

      @Override public void onCancel() {
        // App code
      }

      @Override public void onError(FacebookException exception) {
        // App code
      }
    });
  }

  private void registerUserWithFacebook(String accessToken) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();

      if (waitingDialog.isShowing()) waitingDialog.dismiss();
    });


  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  private void initController() {
    if (indicatorController == null) indicatorController = new DefaultIndicatorController();

    FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
    indicatorContainer.addView(indicatorController.newInstance(this));

    indicatorController.initialize(7);
  }

  public void getOnboardingStatus(final ProgressDialog waitingDialog) {
    dataAccessHandler.getOnboardingStatus(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {

        Intent intent;

        switch (response.code()) {
          case 200:
            String userOnBoard = response.body().getData().getBody().getData().getOnboard();

            if (userOnBoard.equals("1")) {
              getUiForSection(waitingDialog, "fitness");
            } else {
              EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

              intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
              startActivity(intent);
              finish();
            }

            break;
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
      }
    });
  }

  private void getUiForSection(final ProgressDialog waitingDialog, String section) {
    dataAccessHandler.getUiForSection(Constants.BASE_URL_ADDRESS + "user/ui?section=" + section,
        new Callback<UiResponse>() {
          @Override public void onResponse(Call<UiResponse> call, Response<UiResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                EventBusSingleton.getInstance().post(new SignedUpSuccessfullyEvent());

                List<AuthenticationResponseChart> chartsMap =
                    response.body().getData().getBody().getCharts();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP,
                    (ArrayList<AuthenticationResponseChart>) chartsMap);
                startActivity(intent);

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<UiResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_1);
        case 1:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_2);
        case 2:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_3);
        case 3:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_4);
        case 4:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_5);
        case 5:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_6);
        case 6:
          return IntroSliderFragment.newInstance(R.layout.fragment_intro_slide_7);
        default:
          return null;
      }
    }

    @Override public int getCount() {
      return 7;
    }
  }
}