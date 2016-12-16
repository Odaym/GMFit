package com.mcsaatchi.gmfit.architecture.dagger;

import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.RestClient;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.fitness.fragments.FitnessFragment;
import com.mcsaatchi.gmfit.fitness.pedometer.SensorListener;
import com.mcsaatchi.gmfit.health.fragments.HealthFragment;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMealsRecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.nutrition.fragments.NutritionFragment;
import com.mcsaatchi.gmfit.onboarding.activities.SplashActivity;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile1Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile2Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile3Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile4Fragment;
import com.mcsaatchi.gmfit.profile.fragments.MainProfileFragment;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { AppModule.class }) public interface AppComponent {

  void inject(GMFitApplication application);

  void inject(BaseActivity base_activity);

  void inject(SplashActivity splash_activity);

  void inject(ApiCallsHandler apiCallsHandler);

  void inject(DataAccessHandler dataAccessHandler);

  void inject(FitnessFragment fitness_fragment);

  void inject(NutritionFragment nutrition_fragment);

  void inject(HealthFragment health_fragment);

  void inject(UserMealsRecyclerAdapterDragSwipe userMealsRecyclerAdapterDragSwipe);

  void inject(MainProfileFragment mainProfileFragment);

  void inject(SensorListener sensorListener);

  void inject(RestClient restClient);

  void inject(SetupProfile1Fragment setup_profile_1_fragment);

  void inject(SetupProfile2Fragment setup_profile_2_fragment);

  void inject(SetupProfile3Fragment setup_profile_3_fragment);

  void inject(SetupProfile4Fragment setup_profile_4_fragment);
}
