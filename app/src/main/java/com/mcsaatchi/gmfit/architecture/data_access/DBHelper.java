package com.mcsaatchi.gmfit.architecture.data_access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class
 * also
 * usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
  private static final String DATABASE_NAME = "GMFit.db";

  private static final int DATABASE_VERSION = 1;

  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsRunTimeDAO = null;
  private RuntimeExceptionDao<NutritionWidget, Integer> nutritionWidgetsRunTimeDAO = null;
  private RuntimeExceptionDao<DataChart, Integer> dataChartRunTimeDAO = null;

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
  }

  @Override public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      TableUtils.createTable(connectionSource, DataChart.class);
      TableUtils.createTable(connectionSource, FitnessWidget.class);
      TableUtils.createTable(connectionSource, NutritionWidget.class);

      FitnessWidget fw1 =
          new FitnessWidget("Distance Traveled", "Km", 0, R.drawable.ic_distance_traveled, 1);
      FitnessWidget fw2 =
          new FitnessWidget("Active Calories", "Calories", 0, R.drawable.ic_calories_spent, 2);

      getFitnessWidgetsDAO().create(fw1);
      getFitnessWidgetsDAO().create(fw2);
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

  public RuntimeExceptionDao<DataChart, Integer> getDataChartDAO() {
    if (dataChartRunTimeDAO == null) {
      dataChartRunTimeDAO = getRuntimeExceptionDao(DataChart.class);
    }
    return dataChartRunTimeDAO;
  }

  @Override public void close() {
    super.close();
    dataChartRunTimeDAO = null;
    nutritionWidgetsRunTimeDAO = null;
    fitnessWidgetsRunTimeDAO = null;
  }
}