package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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
import com.mcsaatchi.gmfit.adapters.OneItemWithIcon_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.models.MealItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class AddNewMealItem_Activity extends Base_Activity implements SearchView.OnQueryTextListener {

    private static final String TAG = "AddNewMealItem_Activity";
    @Bind(R.id.mealItemsList)
    ListView mealItemsList;
    private RuntimeExceptionDao<MealItem, Integer> mealItemsDAO;
    private QueryBuilder<MealItem, Integer> mealsQueryBuilder;
    private PreparedQuery<MealItem> pq;
    private String mealType;
    private String[] mHeaderNames;
    private Integer[] mHeaderPositions;
    private List<MealItem> mealsList = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private SimpleSectionedListAdapter simpleSectionedListAdapter;
    private OneItemWithIcon_ListAdapter oneItem_ListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        String actionBarTitle;

        if (getIntent().getExtras() != null) {
            actionBarTitle = getString(R.string.add_new_meal_item_activity_title) + " " + getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);
            mealType = getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);
        } else {
            actionBarTitle = getString(R.string.app_name);
            mealType = getString(R.string.meal_headline_title_breakfast);
        }

        super.onCreate(Helpers.createActivityBundleWithProperties(actionBarTitle, true));
        setContentView(R.layout.activity_add_new_meal_item);

        ButterKnife.bind(this);

        mealItemsDAO = getDBHelper().getMealItemDAO();

        mHeaderNames = new String[]{getString(R.string.list_section_recently_added), getString(R.string.list_section_popular_meals)};
        mHeaderPositions = new Integer[]{0, 7};

        prepareQueryForAllMealTypeItems(mealType);

        mealsList = getDBHelper().getMealItemDAO().query(pq);

        initMealsList();
        addListSections();

        mealItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 7)
                    position -= 2;
                else
                    position -= 1;

                Log.d(TAG, "onItemClick: Selected " + mealsList.get(position).getName() + " at index " + position);
                EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_PICKED_MEAL_ENTRY, mealsList.get(position)));
                finish();
            }
        });
    }

    private void initMealsList() {
        List<String> mealNames = new ArrayList<>();

        for (MealItem meal :
                mealsList) {
            mealNames.add(meal.getName());
        }
        oneItem_ListAdapter = new OneItemWithIcon_ListAdapter(this,
                mealNames, R.drawable.ic_chevron_right_black_24dp);
        simpleSectionedListAdapter = new SimpleSectionedListAdapter(this, oneItem_ListAdapter,
                R.layout.view_header_add_new_meal_item_list, R.id.header);

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

    public void addListSections() {
        for (int i = 0; i < mHeaderPositions.length; i++) {
            sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
        }

        simpleSectionedListAdapter.setSections(sections.toArray(new Section[sections.size()]));
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
            sections.clear();

            initMealsList();
        } else {
            //Show all results for this meal type
            prepareQueryForAllMealTypeItems(mealType);

            mealsList = mealItemsDAO.query(pq);

            initMealsList();
            addListSections();
        }

        return true;
    }
}
