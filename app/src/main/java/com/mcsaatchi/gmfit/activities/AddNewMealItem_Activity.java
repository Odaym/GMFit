package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.SimpleSectioned_ListAdapter;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponse;
import com.mcsaatchi.gmfit.rest.SearchMealItemResponseDatum;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewMealItem_Activity extends Base_Activity {

    private static final String TAG = "AddNewMealItem_Activity";
    private static final int SECTION_VIEWTYPE = 1;
    private static final int ITEM_VIEWTYPE = 2;
    private static final int MEAL_AMOUNT_SPECIFIED = 536;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mealItemsList)
    ListView mealItemsList;
    @Bind(R.id.searchMealsAutoCompleTV)
    EditText searchMealsAutoCompleTV;
    @Bind(R.id.searchIconIV)
    ImageView searchIconIV;
    @Bind(R.id.searchResultsHintTV)
    TextView searchResultsHintTV;
    @Bind(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;

    private SharedPreferences prefs;

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

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        setupToolbar(toolbar, actionBarTitle, true);

        mealItemsDAO = getDBHelper().getMealItemDAO();

        loadMealsFromDB(mealType);

        mealItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: Selected " + mealsList.get(position).getName() + " at index " + position);

                Intent intent = new Intent(AddNewMealItem_Activity.this, SpecifyMealAmount_Activity.class);
                intent.putExtra(Cons.EXTRAS_MEAL_OBJECT_DETAILS, mealsList.get(position));
                startActivityForResult(intent, MEAL_AMOUNT_SPECIFIED);

//                if (mealsList.get(position).getSectionType() == ITEM_VIEWTYPE) {
//                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_PICKED_MEAL_ENTRY, mealsList.get(position)));
//                    finish();
//                }
            }
        });

        mealItemsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                /**
                 * Hide keyboard
                 */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });

        searchMealsAutoCompleTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                /**
                 * EditText is empty
                 */
                if (charSequence.toString().isEmpty()) {
                    searchResultsHintTV.setVisibility(View.GONE);

                    searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
                    searchIconIV.setOnClickListener(null);

                    loadMealsFromDB(mealType);
                } else if (charSequence.toString().length() > 2) {

                    pb_loading_indicator.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchIconIV.setImageResource(R.drawable.ic_clear_search);
                            searchIconIV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchMealsAutoCompleTV.setText("");

                                    /**
                                     * Hide keyboard
                                     */
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            });

                            findMeals(charSequence.toString(), new Callback<SearchMealItemResponse>() {
                                @Override
                                public void onResponse(Call<SearchMealItemResponse> call, Response<SearchMealItemResponse> response) {

                                    final List<MealItem> mealsReturned = new ArrayList<>();

                                    List<SearchMealItemResponseDatum> mealsResponse = response.body().getData().getBody().getData();

                                    for (int i = 0; i < mealsResponse.size(); i++) {
                                        MealItem mealItem = new MealItem();

                                        mealItem.setName(mealsResponse.get(i).getName());
                                        mealItem.setMeasurementUnit(mealsResponse.get(i).getMeasurementUnit());
                                        mealItem.setMeal_id(mealsResponse.get(i).getId());
                                        if (mealType.equals("Snacks"))
                                            mealItem.setType("Snack");
                                        else
                                            mealItem.setType(mealType);
                                        mealItem.setSectionType(ITEM_VIEWTYPE);

                                        mealsReturned.add(mealItem);
                                    }

                                    mealsList = mealsReturned;

                                    initMealsList();

                                    searchResultsHintTV.setVisibility(View.VISIBLE);
                                    pb_loading_indicator.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {

                                }
                            });
                        }
                    }, 500);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case MEAL_AMOUNT_SPECIFIED:
                if (data != null) {
                    MealItem mealItem = data.getParcelableExtra(Cons.EXTRAS_MEAL_OBJECT_DETAILS);

                    if (mealItem != null) {
                        EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_PICKED_MEAL_ENTRY, mealItem, true));
                        finish();
                    }
                }

                break;
        }
    }

    private void findMeals(String mealName, final Callback<SearchMealItemResponse> mealItemsResponse) {

        DataAccessHandler.findMeals(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), mealName, new Callback<SearchMealItemResponse>() {
            @Override
            public void onResponse(Call<SearchMealItemResponse> call, Response<SearchMealItemResponse> response) {
                switch (response.code()) {
                    case 200:
                        mealItemsResponse.onResponse(null, response);
                        break;
                }
            }

            @Override
            public void onFailure(Call<SearchMealItemResponse> call, Throwable t) {

            }
        });
    }

    private void loadMealsFromDB(String mealType) {
        prepareQueryForAllMealTypeItems(mealType);

        mealsList = getDBHelper().getMealItemDAO().query(pq);

        initMealsList();
    }

    private void initMealsList() {
        simpleSectionedListAdapter = new SimpleSectioned_ListAdapter(this, mealsList);

        mealItemsList.setAdapter(simpleSectionedListAdapter);
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
}
