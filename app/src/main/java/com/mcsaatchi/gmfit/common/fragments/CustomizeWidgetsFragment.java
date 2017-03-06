package com.mcsaatchi.gmfit.common.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.FitnessWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.HealthWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.NutritionWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.reorderable_listview.DragSortListView;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.fitness.adapters.FitnessWidgetsListAdapter;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.health.adapters.HealthWidgetsListAdapter;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import com.mcsaatchi.gmfit.nutrition.adapters.NutritionWidgetsListAdapter;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import java.util.ArrayList;

public class CustomizeWidgetsFragment extends Fragment {
  @Bind(R.id.widgetsListView) DragSortListView widgetsListView;

  private FitnessWidgetsListAdapter customizeFitnessWidgetsAdapter;
  private NutritionWidgetsListAdapter customizeNutritionWidgetsAdapter;
  private HealthWidgetsListAdapter customizeHealthWidgetsAdapter;

  private Activity parentActivity;
  private String typeOfFragmentToCustomiseFor;

  private ArrayList<FitnessWidget> itemsMapFitness = new ArrayList<>();
  private ArrayList<NutritionWidget> itemsMapNutrition = new ArrayList<>();
  private ArrayList<HealthWidget> itemsMapHealth = new ArrayList<>();

  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO;
  private RuntimeExceptionDao<NutritionWidget, Integer> nutritionWidgetsDAO;

  /**
   * DROPPERS
   */
  private DragSortListView.DropListener onDropFitnessItems = new DragSortListView.DropListener() {
    @Override public void drop(int from, int to) {

      EventBusSingleton.getInstance().post(new FitnessWidgetsOrderChangedEvent(itemsMapFitness));

      customizeFitnessWidgetsAdapter.notifyData();

      for (int i = 0; i < itemsMapFitness.size(); i++) {
        fitnessWidgetsDAO.update(itemsMapFitness.get(i));
      }
    }
  };

  private DragSortListView.DropListener onDropNutritionItems = new DragSortListView.DropListener() {
    @Override public void drop(int from, int to) {

      EventBusSingleton.getInstance()
          .post(new NutritionWidgetsOrderChangedEvent(itemsMapNutrition));

      customizeNutritionWidgetsAdapter.notifyData();

      for (int i = 0; i < itemsMapNutrition.size(); i++) {
        nutritionWidgetsDAO.update(itemsMapNutrition.get(i));
      }
    }
  };

  private DragSortListView.DropListener onDropHealthItems = new DragSortListView.DropListener() {
    @Override public void drop(int from, int to) {

      EventBusSingleton.getInstance().post(new HealthWidgetsOrderChangedEvent(itemsMapHealth));

      customizeHealthWidgetsAdapter.notifyData();
    }
  };

  /**
   * DRAGGERS
   */
  private DragSortListView.DragListener onDragFitnessItems = (from, to) -> {
    if (to < itemsMapFitness.size() && from < itemsMapFitness.size()) {

      FitnessWidget tempItem = itemsMapFitness.get(from);

      int toPosition = itemsMapFitness.get(to).getPosition();

      tempItem.setPosition(toPosition);

      itemsMapFitness.set(from, itemsMapFitness.get(to));

      itemsMapFitness.get(to).setPosition(from);

      itemsMapFitness.set(to, tempItem);
    }
  };

  private DragSortListView.DragListener onDragNutritionItems = (from, to) -> {
    if (to < itemsMapNutrition.size() && from < itemsMapNutrition.size()) {

      NutritionWidget tempItem = itemsMapNutrition.get(from);

      int toPosition = itemsMapNutrition.get(to).getPosition();

      tempItem.setPosition(toPosition);

      itemsMapNutrition.set(from, itemsMapNutrition.get(to));

      itemsMapNutrition.get(to).setPosition(from);

      itemsMapNutrition.set(to, tempItem);
    }
  };

  private DragSortListView.DragListener onDragHealthItems = (from, to) -> {
    if (to < itemsMapHealth.size() && from < itemsMapHealth.size()) {

      HealthWidget tempItem = itemsMapHealth.get(from);

      int toPosition = itemsMapHealth.get(to).getPosition();

      tempItem.setPosition(toPosition);

      itemsMapHealth.set(from, itemsMapHealth.get(to));

      itemsMapHealth.get(to).setPosition(from);

      itemsMapHealth.set(to, tempItem);
    }
  };

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof Activity) {
      parentActivity = (Activity) context;
      fitnessWidgetsDAO = ((BaseActivity) parentActivity).dbHelper.getFitnessWidgetsDAO();
      nutritionWidgetsDAO = ((BaseActivity) parentActivity).dbHelper.getNutritionWidgetsDAO();
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_customize_widgets, null);

    ButterKnife.bind(this, fragmentView);

    Bundle fragmentBundle = getArguments();

    if (fragmentBundle != null) {
      typeOfFragmentToCustomiseFor = fragmentBundle.getString(Constants.EXTRAS_FRAGMENT_TYPE);

      if (typeOfFragmentToCustomiseFor != null) {
        switch (typeOfFragmentToCustomiseFor) {
          case Constants.EXTRAS_FITNESS_FRAGMENT:
            itemsMapFitness =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_FITNESS_WIDGETS_MAP);
            hookUpListWithFitnessItems(itemsMapFitness);
            break;
          case Constants.EXTRAS_NUTRITION_FRAGMENT:
            itemsMapNutrition =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_NUTRITION_WIDGETS_MAP);
            hookUpListWithNutritionItems(itemsMapNutrition);
            break;
          case Constants.EXTRAS_HEALTH_FRAGMENT:
            itemsMapHealth =
                fragmentBundle.getParcelableArrayList(Constants.BUNDLE_HEALTH_WIDGETS_MAP);
            hookUpListWithHealthItems(itemsMapHealth);
            break;
        }
      }
    }

    return fragmentView;
  }

  private void hookUpListWithFitnessItems(ArrayList<FitnessWidget> fitnessItems) {
    widgetsListView.setDragListener(onDragFitnessItems);
    widgetsListView.setDropListener(onDropFitnessItems);

    customizeFitnessWidgetsAdapter =
        new FitnessWidgetsListAdapter(parentActivity, fitnessItems, R.drawable.ic_menu_black_24dp);
    widgetsListView.setAdapter(customizeFitnessWidgetsAdapter);
  }

  private void hookUpListWithNutritionItems(ArrayList<NutritionWidget> itemsMapNutrition) {
    widgetsListView.setDragListener(onDragNutritionItems);
    widgetsListView.setDropListener(onDropNutritionItems);

    customizeNutritionWidgetsAdapter =
        new NutritionWidgetsListAdapter(parentActivity, itemsMapNutrition,
            R.drawable.ic_menu_black_24dp);
    widgetsListView.setAdapter(customizeNutritionWidgetsAdapter);
  }

  private void hookUpListWithHealthItems(ArrayList<HealthWidget> healthItems) {
    widgetsListView.setDragListener(onDragHealthItems);
    widgetsListView.setDropListener(onDropHealthItems);

    customizeHealthWidgetsAdapter =
        new HealthWidgetsListAdapter(parentActivity, healthItems, R.drawable.ic_menu_black_24dp);
    widgetsListView.setAdapter(customizeHealthWidgetsAdapter);
  }
}