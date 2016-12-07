package com.mcsaatchi.gmfit.architecture.dagger;

import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.onboarding.activities.Splash_Activity;
import com.mcsaatchi.gmfit.nutrition.adapters.UserMeals_RecyclerAdapterDragSwipe;
import com.mcsaatchi.gmfit.architecture.GMFit_Application;
import com.mcsaatchi.gmfit.architecture.data_access.ApiCallsHandler;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.fitness.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.health.fragments.Health_Fragment;
import com.mcsaatchi.gmfit.profile.fragments.MainProfile_Fragment;
import com.mcsaatchi.gmfit.nutrition.fragments.Nutrition_Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.Setup_Profile_1_Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.Setup_Profile_2_Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.Setup_Profile_3_Fragment;
import com.mcsaatchi.gmfit.onboarding.fragments.Setup_Profile_4_Fragment;
import com.mcsaatchi.gmfit.fitness.pedometer.SensorListener;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { AppModule.class }) public interface AppComponent {

  void inject(GMFit_Application application);

  void inject(Base_Activity base_activity);

  void inject(Splash_Activity splash_activity);

  void inject(ApiCallsHandler apiCallsHandler);

  void inject(DataAccessHandler dataAccessHandler);

  void inject(Fitness_Fragment fitness_fragment);

  void inject(Nutrition_Fragment nutrition_fragment);

  void inject(Health_Fragment health_fragment);

  void inject(UserMeals_RecyclerAdapterDragSwipe userMealsRecyclerAdapterDragSwipe);

  void inject(MainProfile_Fragment mainProfileFragment);

  void inject(SensorListener sensorListener);

  void inject(Setup_Profile_1_Fragment setup_profile_1_fragment);
  void inject(Setup_Profile_2_Fragment setup_profile_2_fragment);
  void inject(Setup_Profile_3_Fragment setup_profile_3_fragment);
  void inject(Setup_Profile_4_Fragment setup_profile_4_fragment);
}
