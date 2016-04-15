package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;

import com.mcsaatchi.gmfit.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class Main_Activity extends Base_Activity {

    private BottomBar bottomBar;
    private Fragment fragmentReplace;
    public static int USER_AUTHORISED_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        bottomBar = BottomBar.attachShy(binding.myCoordinator,
                binding.myScrollingContent, savedInstanceState);

        bottomBar.noTopOffset();
        bottomBar.noNavBarGoodness();

        bottomBar.setItemsFromMenu(R.menu.bottom_navigation, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                fragmentReplace = null;

                switch (menuItemId) {
                    case R.id.item_one:
                        fragmentReplace = new GoogleFit_Fragment();
                        break;
                    case R.id.item_two:
                        fragmentReplace = new Second_Fragment();
                        break;
                    case R.id.item_three:
                        fragmentReplace = new Second_Fragment();
                        break;
                    case R.id.item_four:
                        fragmentReplace = new Second_Fragment();
                        break;
                    case R.id.item_five:
                        fragmentReplace = new Second_Fragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragment_container, fragmentReplace).commit();
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });

        bottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        bottomBar.mapColorForTab(1, ContextCompat.getColor(this, android.R.color.holo_green_dark));
        bottomBar.mapColorForTab(2, "#7B1FA2");
        bottomBar.mapColorForTab(3, "#FF5252");
        bottomBar.mapColorForTab(4, ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fragmentReplace.onActivityResult(USER_AUTHORISED_REQUEST_CODE, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }
}
