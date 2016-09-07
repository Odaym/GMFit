package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.FitnessWidget;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionWidget;
import com.mcsaatchi.gmfit.models.User;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "GMFit.db";

    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBHelper";
    private Context context;
    private String[] mealTypes = new String[]{"Breakfast", "Lunch", "Dinner"};

    private String[] snackNames = new String[]{"Snickers", "Mars", "Biscuits", "Ice cream", "Lollipops", "Muffins", "Donuts", "Sunflower seeds", "Cereal",
            "Chocolate Chip Cookies", "Waffer Biscuits", "Nuts and crackers"};

    private RuntimeExceptionDao<MealItem, Integer> mealItemRunTimeDAO = null;
    private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsRunTimeDAO = null;
    private RuntimeExceptionDao<NutritionWidget, Integer> nutritionWidgetsRunTimeDAO = null;
    private RuntimeExceptionDao<DataChart, Integer> dataChartRunTimeDAO = null;
    private RuntimeExceptionDao<User, Integer> userRunTimeDAO = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MealItem.class);
            TableUtils.createTable(connectionSource, DataChart.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, FitnessWidget.class);
            TableUtils.createTable(connectionSource, NutritionWidget.class);

            Log.d(TAG, "DBHelper: We're here in the DATABASE constructor");

            SharedPreferences prefs = context.getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

            getMealItemDAO().create(new MealItem("Recently Added", "Breakfast", 1));
            getMealItemDAO().create(new MealItem("Twice-Baked Potatoes", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Butternut Squash, Chickpea", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("All Items", "Breakfast", 1));
            getMealItemDAO().create(new MealItem("Lentils with Roasted Beets and Carrots", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Quinoa with Broccoli-Avocado Pesto", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Mexican-Style Sweet Potatoes", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Whole-Wheat Rigatoni With Greens", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Kelp Noodles With Almond-Ginger Dressing", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Whole-Wheat Linguine With Asparagus and Lemon", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Pesto Cheese Pizza", "Breakfast", 2));
            getMealItemDAO().create(new MealItem("Fresh Fig and Onion Flatbread Pizza", "Breakfast", 2));

            getMealItemDAO().create(new MealItem("Recently Added", "Lunch", 1));
            getMealItemDAO().create(new MealItem("Meat-Free Mushroom Bolognese", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Vegetable and Goat Cheese Quesadilla", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Veggie-Stuffed Zucchini", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Another Added", "", 1));
            getMealItemDAO().create(new MealItem("Black Bean and Quinoa Burgers", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Cauliflower Crust Pizza", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Baked Chickpea Burgers", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Creamy Avocado Pasta", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Quinoa Puttanesca", "Lunch", 2));
            getMealItemDAO().create(new MealItem("All Items", "Lunch", 1));
            getMealItemDAO().create(new MealItem("Mushroom and Olive Veggie Burgers", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Basil Quinoa Cakes", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Quinoa and Sweet Potato Stuffed Mushrooms", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Pizza Margherita", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Spanish Potato and Onion Tortilla", "Lunch", 2));
            getMealItemDAO().create(new MealItem("Roasted Vegetable Pizza", "Lunch", 2));

            getMealItemDAO().create(new MealItem("Open-Faced Egg and Veggie Sammie", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Lentil and Goat Cheese Stuffed Zucchini", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Mushroom Risotto", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Spaghetti Squash Casserole", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Butternut Squash With Tortellini", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Light Spinach Pesto", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Stuffed Squash", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Easy Sesame Salmon", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Chicken Fajitas", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Sesame Chicken Bowl", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Turkey Brie and Cranberry Sandwich", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Baked Salmon With Avocado-Dill Yogurt", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Pan-Seared Fish Tacos", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Eggs and Potatoes in Spicy Tomato Sauce", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Chipotle-Honey Chicken Tenders", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Spicy Rustic Tomato Sauce", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Turkey Chili", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Manly Marmalade Chicken", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Smoked Salmon and Egg Tortilla", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Zesty Shrimp and Quinoa", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Broccoli Rabe “Spaghetti,”", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Apricot Roasted Chicken", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Butternut Squash Soup", "Dinner", 2));
            getMealItemDAO().create(new MealItem("Herb-Stuffed Turkey Breast", "Dinner", 2));

            FitnessWidget fw1 = new FitnessWidget("Walking", "Km/hour", 0, R.drawable.ic_running, 0);
            FitnessWidget fw2 = new FitnessWidget("Biking", "meters", 0, R.drawable.ic_biking, 1);
            FitnessWidget fw3 = new FitnessWidget("Steps", "steps", 0, R.drawable.ic_steps, 2);
            FitnessWidget fw4 = new FitnessWidget("Calories", "Calories", 0, R.drawable.ic_calories, 3);

            getFitnessWidgetsDAO().create(fw1);
            getFitnessWidgetsDAO().create(fw2);
            getFitnessWidgetsDAO().create(fw3);
            getFitnessWidgetsDAO().create(fw4);

        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    public RuntimeExceptionDao<MealItem, Integer> getMealItemDAO() {
        if (mealItemRunTimeDAO == null) {
            mealItemRunTimeDAO = getRuntimeExceptionDao(MealItem.class);
        }
        return mealItemRunTimeDAO;
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

    public RuntimeExceptionDao<User, Integer> getUserDAO() {
        if (userRunTimeDAO == null) {
            userRunTimeDAO = getRuntimeExceptionDao(User.class);
        }
        return userRunTimeDAO;
    }

    @Override
    public void close() {
        super.close();
        mealItemRunTimeDAO = null;
        dataChartRunTimeDAO = null;
        userRunTimeDAO = null;
        nutritionWidgetsRunTimeDAO = null;
        fitnessWidgetsRunTimeDAO = null;
    }
}