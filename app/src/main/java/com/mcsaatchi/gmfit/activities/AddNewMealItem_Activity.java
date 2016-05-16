package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.SimpleOneItemWithIcon_ListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class AddNewMealItem_Activity extends Base_Activity implements SearchView.OnQueryTextListener {
    private String mainMealName;

    private ArrayList<Section> sections = new ArrayList<>();

    private SearchView searchView;

    @Bind(R.id.mealItemsList)
    ListView mealItemsList;

    private List<String> meals_BREAKFAST = new ArrayList<String>() {{
        add("Nescafe - 2 in 1 coffee");
        add("Labne");
        add("Jebne");
        add("Test");
        add("Testing");
        add("Test");
        add("Testing");
        add("Breakfast 1");
        add("Breakfast 2");
        add("Sushi");
        add("Hamburger");
        add("Breakfast 1");
        add("Breakfast 2");
        add("Sushi");
        add("Hamburger");
        add("Pizza");
    }};

    private List<String> meals_LUNCH = new ArrayList<String>() {{
        add("Hamburger");
        add("Pizza");
        add("Mloukhiyye");
        add("Fish Filet");
        add("Steak Sandwich");
        add("Lunch 1");
        add("Lunch 2");
        add("Lunch 3");
        add("Sushi");
        add("Hamburger");
        add("Pizza");
    }};

    private List<String> meals_DINNER = new ArrayList<String>() {{
        add("Chicken Sub");
        add("Pizza");
        add("Mloukhiyye");
        add("Crepe");
        add("Pepperoni Pizza");
        add("Lunch 1");
        add("Lunch 2");
        add("Lunch 3");
        add("Sushi");
        add("Hamburger");
        add("Pizza");
    }};

    private String[] mHeaderNames;
    private Integer[] mHeaderPositions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (getIntent().getExtras() != null) {
            mainMealName = getString(R.string.add_new_meal_item_activity_title) + " " + getIntent().getExtras().getString(Constants.EXTRAS_MAIN_MEAL_NAME);
        } else {
            mainMealName = getString(R.string.app_name);
        }

        super.onCreate(Helpers.createActivityBundleWithProperties(mainMealName, true));
        setContentView(R.layout.activity_add_new_meal_item);

        ButterKnife.bind(this);

        mHeaderNames = new String[]{getString(R.string.list_section_recently_added), getString(R.string.list_section_popular_meals)};
        mHeaderPositions = new Integer[]{0, 3};

        initMealsList(meals_BREAKFAST);
    }

    private void initMealsList(List<String> listItems) {
        sections.clear();

        for (int i = 0; i < mHeaderPositions.length; i++) {
            sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
        }

        SimpleSectionedListAdapter simpleSectionedListAdapter = new SimpleSectionedListAdapter(this, new SimpleOneItemWithIcon_ListAdapter(this,
                listItems, R.drawable.ic_chevron_right_black_24dp),
                R.layout.view_header_add_new_meal_item_list, R.id.header);

        simpleSectionedListAdapter.setSections(sections.toArray(new Section[0]));

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<String> filteredMeals = new ArrayList<>();

        for (String meal : meals_BREAKFAST){
            if (meal.contains(newText)) {
                filteredMeals.add(meal);
            }
        }

//        initMealsList(filteredMeals);

        return false;
    }
}
