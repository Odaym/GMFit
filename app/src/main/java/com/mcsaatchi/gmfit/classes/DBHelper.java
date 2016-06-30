package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.User;

import java.sql.SQLException;
import java.util.Random;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "GMFit.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "DBHelper";

    private String[] mealTypes = new String[]{"Breakfast", "Lunch", "Dinner"};

    private String[] mealNames = new String[]{"Twice-Baked Broccoli-and-Kale-Stuffed Potatoes",
            "Zucchini Noodles With Leek-Tomato Sauce",
            "Mushroom-Stuffed Cabbage Rolls",
            "Quick and Easy Veggie Chili",
            "Butternut Squash, Chickpea, and Kale Curry",
            "Kale, Red Pepper, and Broccoli Spread Rollup",
            "Cheesy Stuffed Tomatoes",
            "Lentil and Goat Cheese Casserole",
            "Lentils with Roasted Beets and Carrots",
            "Quinoa with Broccoli-Avocado Pesto",
            "Mexican-Style Sweet Potatoes",
            "Whole-Wheat Rigatoni With Greens",
            "Kelp Noodles With Almond-Ginger Dressing",
            "Whole-Wheat Linguine With Asparagus and Lemon",
            "Pesto Cheese Pizza",
            "Fresh Fig and Onion Flatbread Pizza",
            "Meat-Free Mushroom Bolognese",
            "Vegetable and Goat Cheese Quesadilla",
            "Veggie-Stuffed Zucchini",
            "Black Bean and Quinoa Burgers",
            "Cauliflower Crust Pizza",
            "Baked Chickpea Burgers",
            "Creamy Avocado Pasta",
            "Quinoa Puttanesca",
            "Mushroom and Olive Veggie Burgers",
            "Basil Quinoa Cakes",
            "Quinoa and Sweet Potato Stuffed Mushrooms",
            "Pizza Margherita",
            "Spanish Potato and Onion Tortilla",
            "Roasted Vegetable Pizza",
            "Open-Faced Egg and Veggie Sammie",
            "Lentil and Goat Cheese Stuffed Zucchini",
            "Mushroom Risotto",
            "Spaghetti Squash Casserole",
            "Butternut Squash With Tortellini",
            "Light Spinach Pesto",
            "Stuffed Squash",
            "Easy Sesame Salmon",
            "Chicken Fajitas",
            "Sesame Chicken Bowl",
            "Turkey, Brie, and Cranberry Sandwich",
            "Baked Salmon With Avocado-Dill Yogurt",
            "Pan-Seared Fish Tacos",
            "Eggs and Potatoes in Spicy Tomato Sauce",
            "Chipotle-Honey Chicken Tenders and Sweet Potatoes",
            "Spicy Rustic Tomato Sauce",
            "Turkey Chili",
            "Manly Marmalade Chicken",
            "Smoked Salmon and Egg Tortilla",
            "Zesty Shrimp and Quinoa",
            "Broccoli Rabe “Spaghetti,” Tomatoes, and Chicken",
            "Apricot Roasted Chicken and Beets With Fall Vegetables",
            "Butternut Squash Soup",
            "Herb-Stuffed Turkey Breast"};

    private String[] snackNames = new String[]{"Snickers", "Mars", "Biscuits", "Ice cream", "Lollipops", "Muffins", "Donuts", "Sunflower seeds", "Cereal",
            "Chocolate Chip Cookies", "Waffer Biscuits", "Nuts and crackers"};

    private RuntimeExceptionDao<MealItem, Integer> mealItemRunTimeDAO = null;
    private RuntimeExceptionDao<DataChart, Integer> dataChartRunTimeDAO = null;
    private RuntimeExceptionDao<User, Integer> userRunTimeDAO = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MealItem.class);
            TableUtils.createTable(connectionSource, DataChart.class);
            TableUtils.createTable(connectionSource, User.class);

            Log.d(TAG, "DBHelper: We're here in the DATABASE constructor");

            for (int i = 0; i < mealNames.length; i++) {
                getMealItemDAO().create(new MealItem(mealNames[new Random().nextInt(mealNames.length)], mealTypes[new Random().nextInt(mealTypes.length)]));
            }

            for (int i = 0; i < snackNames.length; i++){
                getMealItemDAO().create(new MealItem(snackNames[i], "Snacks"));
            }
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
    }
}