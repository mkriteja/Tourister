package com.krishna.user.tourister;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import DataModel.Place;
import retrofit2.Call;

public class SliderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PhotoFragment.OnPhotoInteractionListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final String PREFS_LAST_IMG = "prefs_last_img";
    private static double DEFAULT_LATITUDE = 37.6329946;
    private static double DEFAULT_LONGITUDE = -122.4938344;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private MaterialViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    private android.location.Location lastLocation;
    private FloatingActionButton floatingActionButton;

    private Fragment placesFragmentinstance;
    private Fragment photoFragmentinstance;
    private Fragment favoriteFragmentinstance;
    private Fragment tourFragmentInstance;

    private de.hdodenhof.circleimageview.CircleImageView circleImageView;
    private SharedPreferences mPreferences;
    private TextView camera;
    private TextView gallery;
    private boolean iscamera = false;


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
            favoriteFragmentinstance = getSupportFragmentManager().getFragment(savedInstanceState, "favorites");
            tourFragmentInstance = getSupportFragmentManager().getFragment(savedInstanceState, "tours");
            lastLocation = (Location) savedInstanceState.getParcelable("lastlocation");
            if (placesFragmentinstance == null) {
                placesFragmentinstance = PlacesFragment.newInstance();
            }
            if (photoFragmentinstance == null) {
                photoFragmentinstance = PhotoFragment.newInstance();
            }
            if (favoriteFragmentinstance == null) {
                favoriteFragmentinstance = FavFragment.newInstance();
            }
            if (tourFragmentInstance == null) {
                tourFragmentInstance = TourFragment.newInstance();
            }
        } else {
            placesFragmentinstance = PlacesFragment.newInstance();
            photoFragmentinstance = PhotoFragment.newInstance();
            favoriteFragmentinstance = FavFragment.newInstance();
            tourFragmentInstance = TourFragment.newInstance();
        }
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) {
                    String currlocation = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
                    PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
                    Call<Place> call = service.getCurrentPlacesDetails(currlocation, "500", "attractions", AppManager.getApiKey());
                    callPlacesExecuteAsync(call);
                }
            }
        });
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

        final View headerview = navigationView.getHeaderView(0);
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
        circleImageView = (de.hdodenhof.circleimageview.CircleImageView) headerview.findViewById(R.id.circle);
        ((TextView) headerview.findViewById(R.id.useremailheader)).setText(AppManager.getUseremail().replaceAll("@@", "."));
        final Firebase favref = AppManager.getRef().child(AppManager.getUseremail()).child("Name");
        favref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String username = (String) snapshot.getValue();
                if (username != null && !username.isEmpty()) {
                    ((TextView) headerview.findViewById(R.id.usernameheader)).setText(username);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mPreferences = getSharedPreferences(AppManager.getUseremail() + PREFS_LAST_IMG, Context.MODE_PRIVATE);
        String tempimage = mPreferences.getString("image", "");
        if (!tempimage.isEmpty()) {
            byte[] decodedString = Base64.decode(tempimage, Base64.DEFAULT);
            circleImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View DialogView = getLayoutInflater().inflate(R.layout.camera, null);

                camera = (TextView) DialogView.findViewById(R.id.camera);
                gallery = (TextView) DialogView.findViewById(R.id.gall);

                final AlertDialog dialog = new AlertDialog.Builder(SliderActivity.this)
                        .setTitle("Select an Option").create();
                dialog.setCancelable(true);
                dialog.setView(DialogView);
                dialog.show();
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iscamera = true;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                        dialog.dismiss();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iscamera = false;
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, 0);
                        dialog.dismiss();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (iscamera) {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                storeImage(bitmap);
                circleImageView.setImageBitmap(bitmap);
            }
        } else {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    storeImage(bitmap);
                    circleImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    //Log.d("gallery", "Error accessing file: " + e.getMessage());
                }
            }
        }
    }

    private void storeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        SharedPreferences sharedRef = getSharedPreferences(AppManager.getUseremail() + PREFS_LAST_IMG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedRef.edit();
        edit.putString("image", encodedImage);
        edit.apply();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
        String currlocation;
        if (lastLocation != null) {
            currlocation = lastLocation.getLatitude() + "," + lastLocation.getLongitude();
        } else {
            currlocation = DEFAULT_LATITUDE + "," + DEFAULT_LONGITUDE;
        }
        Call<Place> call = null;
        switch (id) {
            case R.id.task1:
                call = service.getCurrentPlacesDetails(currlocation, "500", "Architecture", AppManager.getApiKey());
                break;
            case R.id.task2:
                call = service.getCurrentPlacesDetails(currlocation, "500", "Historic Places", AppManager.getApiKey());
                break;
            case R.id.task3:
                call = service.getCurrentPlacesDetails(currlocation, "500", "Life Style", AppManager.getApiKey());
                break;
            case R.id.task4:
                call = service.getCurrentPlacesDetails(currlocation, "500", "Food", AppManager.getApiKey());
                break;
            case R.id.task6:
                call = service.getCurrentPlacesDetails(currlocation, "500", "Art and Museums", AppManager.getApiKey());
                break;
            case R.id.task7:
                finish();
                break;
            default:
                break;

        }
        if (call != null)
            callPlacesExecuteAsync(call);
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
            if (position == 0) {
                return placesFragmentinstance;
            } else if (position == 1) {
                return photoFragmentinstance;
            } else if (position == 2) {
                return favoriteFragmentinstance;
            } else if (position == 3) {
                return tourFragmentInstance;
            } else {
                return null;
            }
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
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
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
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

        } else {
            LocationRequest locationRequest = LocationRequest.create()
                    .setSmallestDisplacement(10000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    public void callPlacesExecuteAsync(Call<Place> call) {
        AppManager.setApicall(call);
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
                    startLocationUpdates();
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
        if (location != null && lastLocation == null || (lastLocation.getLatitude()!=location.getLatitude() && lastLocation.getLongitude()!=location.getLongitude())) {
            lastLocation = location;
            String currlocation = location.getLatitude() + "," + location.getLongitude();
            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            Call<Place> call = service.getCurrentPlacesDetails(currlocation, "500", "attractions", AppManager.getApiKey());
            callPlacesExecuteAsync(call);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (placesFragmentinstance != null && placesFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "places", placesFragmentinstance);
        if (photoFragmentinstance != null && photoFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "photos", photoFragmentinstance);
        if (favoriteFragmentinstance != null && favoriteFragmentinstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "favorites", favoriteFragmentinstance);
        if (tourFragmentInstance != null && tourFragmentInstance.isAdded()) {
            getSupportFragmentManager().putFragment(savedInstanceState, "tours", tourFragmentInstance);
        }
        savedInstanceState.putParcelable("lastlocation",lastLocation);
    }
}
