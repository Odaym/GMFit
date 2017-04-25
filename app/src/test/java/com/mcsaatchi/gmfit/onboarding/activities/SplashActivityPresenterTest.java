package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandlerImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;

public class SplashActivityPresenterTest {

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock SplashActivityPresenter.SplashActivityView view;

  SplashActivityPresenter presenter;

  @Mock DataAccessHandlerImpl dataAccessHandler;

  @Before public void setUp() {
    presenter = new SplashActivityPresenter(view, dataAccessHandler);
  }


  @Test public void doesLoginSilently() {
    String email = "cura.app@gmail.com", password = "odayoday";

    verify(view).showLoginActivity();

    //if (connected) {
    //  presenter.signInUserSilently(email, password);
    //} else {
    //  verify(view).displayNoInternetDialog();
    //}
  }

  @Test public void doesFacebookLogin() {
    String facebookToken = "091u2i30912kj310231203ij123";

    boolean connected = verify(view).checkInternetAvailable();

    if (connected) {
      presenter.loginWithFacebook(facebookToken);
    } else {
      verify(view).displayNoInternetDialog();
    }
  }
}