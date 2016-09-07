package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.SimpleSectioned_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.models.MealItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewMealItem_Activity extends Base_Activity implements SearchView.OnQueryTextListener {

    private static final String TAG = "AddNewMealItem_Activity";
    private static final int SECTION_VIEWTYPE = 1;
    private static final int ITEM_VIEWTYPE = 2;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mealItemsList)
    ListView mealItemsList;
    private RuntimeExceptionDao<MealItem, Integer> mealItemsDAO;
    private QueryBuilder<MealItem, Integer> mealsQueryBuilder;
    private PreparedQuery<MealItem> pq;

    private String mealType;

    private List<MealItem> mealsList = new ArrayList<>();
    private SimpleSectioned_ListAdapter simpleSectionedListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        String actionBarTitle;

        if (getIntent().getExtras() != null) {
            mealType = getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);
            actionBarTitle = getString(R.string.add_new_meal_item_activity_title) + " " + mealType;
        } else {
            actionBarTitle = getString(R.string.app_name);
            mealType = getString(R.string.meal_headline_title_breakfast);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_meal_item);

        ButterKnife.bind(this);

        setupToolbar(toolbar, actionBarTitle, true);

        mealItemsDAO = getDBHelper().getMealItemDAO();

        prepareQueryForAllMealTypeItems(mealType);

        mealsList = getDBHelper().getMealItemDAO().query(pq);

        initMealsList();

        mealItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: Selected " + mealsList.get(position).getName() + " at index " + position);

                if (mealsList.get(position).getSectionType() == ITEM_VIEWTYPE) {
                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_PICKED_MEAL_ENTRY, mealsList.get(position)));
                    finish();
                }
            }
        });
    }

    private void initMealsList() {
        simpleSectionedListAdapter = new SimpleSectioned_ListAdapter(this, mealsList);

        mealItemsList.setAdapter(simpleSectionedListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_new_meal_item_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    public void prepareQueryForSearchTerm(String searchQuery, String mealType) {
        try {
            mealsQueryBuilder = mealItemsDAO.queryBuilder();

            SelectArg nameSelectArg = new SelectArg("%" + searchQuery + "%");

            Where where = mealsQueryBuilder.where();
            where.eq("type", mealType).and().like("name", nameSelectArg);
            pq = mealsQueryBuilder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void prepareQueryForAllMealTypeItems(String mealType) {
        try {
            mealsQueryBuilder = mealItemsDAO.queryBuilder();

            Where where = mealsQueryBuilder.where();
            where.eq("type", mealType);
            pq = mealsQueryBuilder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (!newText.isEmpty()) {
            //Run actual search query for this meal type
            prepareQueryForSearchTerm(newText, mealType);

            mealsList = mealItemsDAO.query(pq);

            initMealsList();
        } else {
            //Show all results for this meal type
            prepareQueryForAllMealTypeItems(mealType);

            mealsList = mealItemsDAO.query(pq);

            initMealsList();
        }

        return true;
    }
}
