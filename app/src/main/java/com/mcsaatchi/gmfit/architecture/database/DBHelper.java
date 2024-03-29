package com.mcsaatchi.gmfit.architecture.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.health.models.Medication;
import com.mcsaatchi.gmfit.health.models.MedicationReminder;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class
 * also
 * usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
  private static final String DATABASE_NAME = "GlobeMedFit.db";

  private static final int DATABASE_VERSION = 1;

  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsRunTimeDAO = null;
  private RuntimeExceptionDao<NutritionWidget, Integer> nutritionWidgetsRunTimeDAO = null;
  private RuntimeExceptionDao<Medication, Integer> medicationRunTimeDAO = null;
  private RuntimeExceptionDao<MedicationReminder, Integer> medicationReminderRunTimeDAO = null;

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, FitnessWidget.class);
      TableUtils.createTable(connectionSource, NutritionWidget.class);
      TableUtils.createTable(connectionSource, Medication.class);
      TableUtils.createTable(connectionSource, MedicationReminder.class);

      /**
       * Create starting Fitness Widgets
       */
      getFitnessWidgetsDAO().create(
          new FitnessWidget("Distance Traveled", "Km", 0, R.drawable.ic_distance_traveled, 1));
      getFitnessWidgetsDAO().create(
          new FitnessWidget("Active Calories", "Calories", 0, R.drawable.ic_calories_spent, 2));
    } catch (SQLException e) {
      Log.e(DBHelper.class.getName(), "Can't create database", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i,
      int i1) {

  }

  public RuntimeExceptionDao<FitnessWidget, Integer> getFitnessWidgetsDAO() {
    if (fitnessWidgetsRunTimeDAO == null) {
      fitnessWidgetsRunTimeDAO = getRuntimeExceptionDao(FitnessWidget.class);
    }
    return fitnessWidgetsRunTimeDAO;
  }

  public RuntimeExceptionDao<NutritionWidget, Integer> getNutritionWidgetsDAO() {
    if (nutritionWidgetsRunTimeDAO == null) {
      nutritionWidgetsRunTimeDAO = getRuntimeExceptionDao(NutritionWidget.class);
    }
    return nutritionWidgetsRunTimeDAO;
  }

  public RuntimeExceptionDao<Medication, Integer> getMedicationDAO() {
    if (medicationRunTimeDAO == null) {
      medicationRunTimeDAO = getRuntimeExceptionDao(Medication.class);
    }
    return medicationRunTimeDAO;
  }

  public RuntimeExceptionDao<MedicationReminder, Integer> getMedicationReminderDAO() {
    if (medicationReminderRunTimeDAO == null) {
      medicationReminderRunTimeDAO = getRuntimeExceptionDao(MedicationReminder.class);
    }
    return medicationReminderRunTimeDAO;
  }

  @Override public void close() {
    super.close();
    nutritionWidgetsRunTimeDAO = null;
    fitnessWidgetsRunTimeDAO = null;
    medicationRunTimeDAO = null;
    medicationRunTimeDAO = null;
  }
}
