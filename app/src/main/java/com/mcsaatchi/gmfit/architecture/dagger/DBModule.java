package com.mcsaatchi.gmfit.architecture.dagger;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.health.models.Medication;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class DBModule {
  public DBModule() {
  }

  @Provides @Singleton DBHelper providesDBHelper(Context app) {
    return OpenHelperManager.getHelper(app, DBHelper.class);
  }

  @Provides @Singleton RuntimeExceptionDao<FitnessWidget, Integer> providesFitnessWidgetsDAO(
      DBHelper dbHelper) {
    return dbHelper.getFitnessWidgetsDAO();
  }

  @Provides @Singleton RuntimeExceptionDao<Medication, Integer> providesMedicationDAO(DBHelper dbHelper){
    return dbHelper.getMedicationDAO();
  }
}
