package com.mcsaatchi.gmfit.onboarding.activities;

import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplashActivityPresenterTest {
  private SplashActivityPresenter.SplashActivityView mockView;
  private DataAccessHandlerImpl mockDataAccessHandler;

  private SplashActivityPresenter presenter;

  @Before public void setUp() throws Exception {
    mockDataAccessHandler = Mockito.mock(DataAccessHandlerImpl.class);
    mockView = Mockito.mock(SplashActivityPresenter.SplashActivityView.class);

    presenter = new SplashActivityPresenter(mockView, mockDataAccessHandler);
  }

  @Test public void testSilentLoginWithoutInternet() {
    when(mockView.checkInternetAvailable()).thenReturn(false);

    presenter.login("omar.aboumrad@gmail.com", "123123");

    verify(mockView).displayNoInternetDialog();
  }
}