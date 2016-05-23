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

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.SimpleOneItemWithIcon_ListAdapter;
import com.mcsaatchi.gmfit.models.MealItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class AddNewMealItem_Activity extends Base_Activity implements SearchView.OnQueryTextListener {

    private SearchView searchView;

    private QueryBuilder<MealItem, Integer> mealsQueryBuilder;
    private PreparedQuery<MealItem> pq;

    @Bind(R.id.mealItemsList)
    ListView mealItemsList;
    private String mealType;
    private String[] mHeaderNames;
    private Integer[] mHeaderPositions;
    private List<MealItem> mealsList = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
    private SimpleSectionedListAdapter simpleSectionedListAdapter;
    private SimpleOneItemWithIcon_ListAdapter simpleOneItem_ListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        String actionBarTitle;

        if (getIntent().getExtras() != null) {
            actionBarTitle = getString(R.string.add_new_meal_item_activity_title) + " " + getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);
            mealType = getIntent().getExtras().getString(Cons.EXTRAS_MAIN_MEAL_NAME);
        } else {
            actionBarTitle = getString(R.string.app_name);
            mealType = "BREAKFAST";
        }

        super.onCreate(Helpers.createActivityBundleWithProperties(actionBarTitle, true));
        setContentView(R.layout.activity_add_new_meal_item);

        ButterKnife.bind(this);

        mHeaderNames = new String[]{getString(R.string.list_section_recently_added), getString(R.string.list_section_popular_meals)};
        mHeaderPositions = new Integer[]{0, 7};

        prepareQueryForAllMealTypeItems(mealType);
        mealsList = getDBHelper().getMealItemDAO().query(pq);
        initMealsList();
        addListSections();

        mealItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_PICKED_MEAL_ENTRY, mealsList.get(position)));
                Log.d(TAG, "onItemClick: MEAL TYPE " + mealsList.get(position).getType());
                finish();
            }
        });
    }

    private void initMealsList() {
        simpleOneItem_ListAdapter = new SimpleOneItemWithIcon_ListAdapter(this,
                mealsList, R.drawable.ic_chevron_right_black_24dp);
        simpleSectionedListAdapter = new SimpleSectionedListAdapter(this, simpleOneItem_ListAdapter,
                R.layout.view_header_add_new_meal_item_list, R.id.header);

        mealItemsList.setAdapter(simpleSectionedListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_new_meal_item_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
            mealsQueryBuilder = getDBHelper().getMealItemDAO().queryBuilder();

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
            mealsQueryBuilder = getDBHelper().getMealItemDAO().queryBuilder();

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

        Log.d(TAG, "addListSections: WE are here");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private static final String TAG = "AddNewMealItem_Activity";

    @Override
    public boolean onQueryTextChange(String newText) {

        if (!newText.isEmpty()) {
            //Run actual search query for this meal type
            prepareQueryForSearchTerm(newText, mealType);

            mealsList = getDBHelper().getMealItemDAO().query(pq);
            sections.clear();

            initMealsList();
        } else {
            //Show all results for this meal type
            prepareQueryForAllMealTypeItems(mealType);

            mealsList = getDBHelper().getMealItemDAO().query(pq);

            initMealsList();
            addListSections();
        }

        return true;
    }
}
