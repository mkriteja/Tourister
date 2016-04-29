package com.example.user.tourister;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import DataModel.Place;
import retrofit2.Call;

public class SliderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PhotoFragment.OnPhotoInteractionListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MaterialViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    private android.location.Location lastLocation;
    private Fragment placesFragmentinstance;
    private Fragment photoFragmentinstance;
    private Fragment favoriteFragmentinstance;
    private boolean savedstatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (savedInstanceState != null) {
            placesFragmentinstance = getSupportFragmentManager().getFragment(savedInstanceState, "places");
            photoFragmentinstance = getSupportFragmentManager().getFragment(savedInstanceState, "photos");
            favoriteFragmentinstance = getSupportFragmentManager().getFragment(savedInstanceState,"favorites");
            savedstatus = savedInstanceState.getBoolean("savedstatus");
            if (placesFragmentinstance == null) {
                placesFragmentinstance = PlacesFragment.newInstance();
                photoFragmentinstance = PhotoFragment.newInstance();
                favoriteFragmentinstance = FavFragment.newInstance();
            }
        } else {
            placesFragmentinstance = PlacesFragment.newInstance();
            photoFragmentinstance = PhotoFragment.newInstance();
            favoriteFragmentinstance = FavFragment.newInstance();
        }
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorAndDrawable(
                                Color.parseColor("#26A69A"),
                                getDrawable(R.drawable.icon1));
                    case 1:
                        return HeaderDesign.fromColorAndDrawable(
                                Color.parseColor("#FF8A65"),
                                getDrawable(R.drawable.icon2));
                    case 2:
                        return HeaderDesign.fromColorAndDrawable(
                                Color.parseColor("#795548"),
                                getDrawable(R.drawable.icon3));
                    case 3:
                        return HeaderDesign.fromColorAndDrawable(
                                Color.parseColor("#00BCD4"),
                                getDrawable(R.drawable.icon4));
                }
                return null;
            }
        });

        Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        ViewPager viewPager = mViewPager.getViewPager();
        viewPager.setAdapter(new MyFragmentStateAdapter(getSupportFragmentManager()));
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        navigationView = (NavigationView) findViewById(R.id.navigation);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

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
            if (position == 0) return placesFragmentinstance;
            else if (position == 1) return photoFragmentinstance;
            else if (position == 2) return favoriteFragmentinstance;
            return new BlankFragment();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Places";
            else if (position == 1) return "Photos";
            else if (position == 2) return "Favorites";
            else return "Tours";
        }
    }

    public void onPhotoInteraction(int position, View sharedImage) {

        AppManager.setSelectedPhoto(((BitmapDrawable) ((ImageView) sharedImage).getDrawable()).getBitmap());
        AppManager.setTransitionName(sharedImage.getTransitionName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, sharedImage, sharedImage.getTransitionName());
            savedstatus = true;
            startActivity(new Intent(this, PhotoDetailActivity.class), options.toBundle());

        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            savedstatus = true;
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !savedstatus ) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if(!savedstatus)
            startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        } else {
            android.location.Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation !=null) {
                lastLocation = mLastLocation;
                String currlocation = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
                Call<Place> call = service.getCurrentPlacesDetails(currlocation, "10000", "attractions", AppManager.getApiKey());
                callPlacesExecuteAsync(call);
            }
            LocationRequest locationRequest = LocationRequest.create()
                    .setSmallestDisplacement(10000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    public void callPlacesExecuteAsync(Call<Place> call){
        ((PlacesFragment) placesFragmentinstance).makeAsyncCall(call);
        ((PhotoFragment) photoFragmentinstance).executeCurrentphotos(call);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
                    Call<Place> call = service.getPlaces("Museums in New york", AppManager.getApiKey());
                    callPlacesExecuteAsync(call);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location.getLatitude() != lastLocation.getLatitude() || location.getLongitude() != lastLocation.getLongitude()) {
            lastLocation = location;
            String currlocation = location.getLatitude() + "," + location.getLongitude();
            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            Call<Place> call = service.getCurrentPlacesDetails(currlocation, "10000", "attractions", AppManager.getApiKey());
            callPlacesExecuteAsync(call);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (placesFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "places", placesFragmentinstance);
        if(photoFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "photos", photoFragmentinstance);
        if(favoriteFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState,"favorites",favoriteFragmentinstance);
        savedInstanceState.putBoolean("savedstatus",true);

    }


}
