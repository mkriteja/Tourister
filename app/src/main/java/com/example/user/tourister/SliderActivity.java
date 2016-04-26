package com.example.user.tourister;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import java.util.ArrayList;

public class SliderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,PlacesFragment.OnDataAvailable {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MaterialViewPager mViewPager;
    private PhotoFragment photofragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        photofragment = new PhotoFragment();
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }
                return null;
            }
        });

        Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }

        ViewPager viewPager = mViewPager.getViewPager();
        viewPager.setAdapter(new MyFragmentStateAdapter(getSupportFragmentManager()));
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        navigationView=(NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        int id = item.getItemId();

        switch (id){

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MyFragmentStateAdapter extends FragmentStatePagerAdapter {

        int count;

        public MyFragmentStateAdapter(FragmentManager fm) {
            super(fm);
            count = 4;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0) return PlacesFragment.newInstance();
            else if(position==1) return photofragment;
            else if(position==2)return FavFragment.newInstance();
            return new BlankFragment();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0) return "Places";
            else if(position==1) return "Photos";
            else if(position==2) return "Favorites";
            else return "Tours";
        }
    }

    public void onDataSaved(ArrayList<String> data){
        photofragment.dataAvailable(data);
    }

}
