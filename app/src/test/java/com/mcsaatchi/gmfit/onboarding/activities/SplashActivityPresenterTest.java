package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SplashActivityPresenterTest {

  private DataAccessHandlerImpl mockDataAccessHandler;
  private SplashActivityPresenter.SplashActivityView mockView;

  private SplashActivityPresenter presenter;

  @Before public void setUp() {
    mockDataAccessHandler = Mockito.mock(DataAccessHandlerImpl.class);
    mockView = Mockito.mock(SplashActivityPresenter.SplashActivityView.class);

    presenter = new SplashActivityPresenter(mockView, mockDataAccessHandler);
  }

  @Test public void displayDialogIfLoginNotConnected() {
    Mockito.when(mockView.checkInternetAvailable()).thenReturn(false);

    presenter.login("foo@bar.com", "password");

    Mockito.verify(mockView).displayNoInternetDialog();
  }

  @Test public void signInSilentlyIfLoginConnected() {
    Mockito.when(mockView.checkInternetAvailable()).thenReturn(true);

    presenter.login("foo@bar.com", "password");

    Mockito.verify(mockView).saveAccessToken(Mockito.anyString());
  }
}