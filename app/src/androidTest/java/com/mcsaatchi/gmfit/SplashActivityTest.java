package com.mcsaatchi.gmfit;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import com.mcsaatchi.gmfit.onboarding.activities.SplashActivity;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class SplashActivityTest {

  @Rule public ActivityTestRule<SplashActivity> activityRule =
      new ActivityTestRule<>(SplashActivity.class, true, false);

  @Test public void doesLoginSuccessfully() throws IOException {
    MockWebServer server = new MockWebServer();
    server.start();

    GMFitTestApplication app =
        (GMFitTestApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
    app.setBaseURL(server.url("/").toString());

    server.enqueue(new MockResponse().setBody("{\n"
        + "  \"data\": {\n"
        + "    \"message\": \"The request has succeeded\",\n"
        + "    \"body\": {\n"
        + "      \"token\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIxLCJpc3MiOiJodHRwczpcL1wvbW9iaWxlYXBwLmdsb2JlbWVkZml0LmNvbVwvYXBpXC92MVwvbG9naW4iLCJpYXQiOjE0OTM3MzAwMjgsImV4cCI6MTQ5NjQzMDAyOCwibmJmIjoxNDkzNzMwMDI4LCJqdGkiOiJlMjcyYWQxZGQ2YzQzYzdkMGYwODFjYzFlYzdlOWQyMyJ9.s_PjDK1QDgCKFllmJRFXv4sbWkvVH5entGzLSwVaHrk\"\n"
        + "    },\n"
        + "    \"code\": 200\n"
        + "  }\n"
        + "}"));

    activityRule.launchActivity(null);

    onView(withId(R.id.signInBTN)).check(matches(withText("Sign In")));

    server.shutdown();
  }
}