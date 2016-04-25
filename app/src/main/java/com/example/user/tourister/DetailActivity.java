package com.example.user.tourister;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.WindowInsets;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import android.support.v7.graphics.Palette;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



import java.util.ArrayList;
import java.util.HashSet;

import DataModel.Place;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class DetailActivity extends Activity {

    private DataModel.Result placeresult;

    private TextView titleView;
    private TextView placesaccesstimeView;
    private TextView placesaddressView;
    private TextView placeswebsiteView;
    private TextView placenumberView;
    private TextView placeratincountView;
    private TextView placetypeView;
    private RatingBar placeratingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        new DownloadPlaceDetails(getIntent().getStringExtra("placeid")).execute();


        titleView = (TextView) findViewById(R.id.title);
        placesaccesstimeView = (TextView) findViewById(R.id.placeacesstime);
        placesaddressView = (TextView) findViewById(R.id.placeaddress);
        placeswebsiteView = (TextView) findViewById(R.id.placewebsite);
        placenumberView = (TextView) findViewById(R.id.placephone);
        placeratincountView = (TextView) findViewById(R.id.placeratingcount);
        placetypeView = (TextView) findViewById(R.id.placetype);
        placeratingbar = (RatingBar) findViewById(R.id.placeratingbar);

        ImageButton starbutton = (ImageButton) findViewById(R.id.star);
        if(!getIntent().getBooleanExtra("favfragment",false)){

            starbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStar(v);
                }
            });
        }else{
            starbutton.setVisibility(View.GONE);
        }

        ImageButton infobutton = (ImageButton) findViewById(R.id.info);
        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformation(v);
            }
        });

        Bitmap photo = setupPhoto();

        colorize(photo);

        setupMap();

        setOutlines(R.id.star, R.id.info);
        applySystemWindowsBottomInset(R.id.container);

        getWindow().getEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                ImageView hero = (ImageView) findViewById(R.id.photo);
                ObjectAnimator color = ObjectAnimator.ofArgb(hero.getDrawable(), "tint", R.color.photo_tint, 0);
                color.start();

                findViewById(R.id.info).animate().alpha(1.0f);
                findViewById(R.id.star).animate().alpha(1.0f);

                getWindow().getEnterTransition().removeListener(this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        ImageView hero = (ImageView) findViewById(R.id.photo);
        ObjectAnimator color = ObjectAnimator.ofArgb(hero.getDrawable(), "tint",
                0, R.color.photo_tint);
        color.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishAfterTransition();
            }
        });
        color.start();

        findViewById(R.id.info).animate().alpha(0.0f);
        findViewById(R.id.star).animate().alpha(0.0f);
    }

    private void setupText() {
        titleView.setText(getIntent().getStringExtra("title"));
        placeratingbar.setRating(placeresult.getRating());
        placeratincountView.setText(placeresult.getUserRatingsTotal()+" Reviews");
        placetypeView.setText(placeresult.getTypes().get(0));
        placesaddressView.setText(placeresult.getFormattedAddress());
        placeswebsiteView.setText(placeresult.getWebsite());
        placenumberView.setText(placeresult.getInternationalPhoneNumber());
    }

    private void setupMap() {
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        double lat = getIntent().getDoubleExtra("lat", 37.6329946);
        double lng = getIntent().getDoubleExtra("lng", -122.4938344);
        float zoom = getIntent().getFloatExtra("zoom", 15.0f);

        LatLng position = new LatLng(lat, lng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
        map.addMarker(new MarkerOptions().position(position));
    }

    private void setOutlines(int star, int info) {
        final int size = getResources().getDimensionPixelSize(R.dimen.floating_button_size);

        final ViewOutlineProvider vop = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, size, size);
            }
        };

        findViewById(star).setOutlineProvider(vop);
        findViewById(info).setOutlineProvider(vop);
    }

    private void applySystemWindowsBottomInset(int container) {
        View containerView = findViewById(container);
        containerView.setFitsSystemWindows(true);
        containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                if (metrics.widthPixels < metrics.heightPixels) {
                    view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
                } else {
                    view.setPadding(0, 0, windowInsets.getSystemWindowInsetRight(), 0);
                }
                return windowInsets.consumeSystemWindowInsets();
            }
        });
    }

    private void colorize(Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(palette);
    }

    private void applyPalette(Palette palette) {
        getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor().getRgb()));

        titleView.setTextColor(palette.getVibrantColor().getRgb());
        placesaccesstimeView.setTextColor(palette.getLightMutedColor().getRgb());
        placesaddressView.setTextColor(palette.getLightMutedColor().getRgb());
        placeswebsiteView.setTextColor(palette.getLightMutedColor().getRgb());
        placenumberView.setTextColor(palette.getLightMutedColor().getRgb());
        placeratincountView.setTextColor(palette.getLightVibrantColor().getRgb());

        colorRipple(R.id.info, palette.getDarkMutedColor().getRgb(),
                palette.getDarkVibrantColor().getRgb());
        colorRipple(R.id.star, palette.getMutedColor().getRgb(),
                palette.getVibrantColor().getRgb());

        View infoView = findViewById(R.id.information_container);
        infoView.setBackgroundColor(palette.getLightMutedColor().getRgb());

        AnimatedPathView star = (AnimatedPathView) findViewById(R.id.star_container);
        star.setFillColor(palette.getVibrantColor().getRgb());
        star.setStrokeColor(palette.getLightVibrantColor().getRgb());
    }

    private void colorRipple(int id, int bgColor, int tintColor) {
        View buttonView = findViewById(id);

        RippleDrawable ripple = (RippleDrawable) buttonView.getBackground();
        GradientDrawable rippleBackground = (GradientDrawable) ripple.getDrawable(0);
        rippleBackground.setColor(bgColor);

        ripple.setColor(ColorStateList.valueOf(tintColor));
    }

    private Bitmap setupPhoto() {
        Bitmap bitmap = AppManager.getsPhotoCache();
        ((ImageView) findViewById(R.id.photo)).setImageBitmap(bitmap);
        return bitmap;
    }

    private class DownloadPlaceDetails extends AsyncTask<Void, Integer, DataModel.Result> {
        private String placeid;

        public DownloadPlaceDetails(String id){
            placeid = id;
        }

        protected DataModel.Result doInBackground(Void... values) {

            PlacesInterface service = AppManager.getRetrofit().create(PlacesInterface.class);
            Call<Place> call = service.getPlacesDetails(placeid,AppManager.getApiKey());
            try {
                Place res = call.execute().body();
                return res.getResult();
            }catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(DataModel.Result tresult) {
            placeresult = tresult;
            setupText();
        }
    }

    public void showStar(View view) {
        toggleStarView();
    }

    private void toggleStarView() {
        final AnimatedPathView starContainer = (AnimatedPathView) findViewById(R.id.star_container);

        if (starContainer.getVisibility() == View.INVISIBLE) {
            findViewById(R.id.photo).animate().alpha(0.2f);
            starContainer.setAlpha(1.0f);
            starContainer.setVisibility(View.VISIBLE);
            starContainer.reveal();
            addToFirebase();
        } else {
            findViewById(R.id.photo).animate().alpha(1.0f);
            starContainer.animate().alpha(0.0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    starContainer.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void addToFirebase(){
        final Firebase favref = AppManager.getRef().child(AppManager.getUseremail()).child("places");
        Query queryRef = favref.orderByKey();
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<1){
                    favref.child("0").setValue(getIntent().getStringExtra("placeid"));
                }else {
                    HashSet<String> existingplaceids = new HashSet<>((ArrayList<String>) dataSnapshot.getValue());
                    if(existingplaceids.add(getIntent().getStringExtra("placeid")))
                        favref.child(String.valueOf(existingplaceids.size()-1)).setValue(getIntent().getStringExtra("placeid"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void showInformation(View view) {
        toggleInformationView(view);
    }

    private void toggleInformationView(View view) {
        final View infoContainer = findViewById(R.id.information_container);

        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        float radius = Math.max(infoContainer.getWidth(), infoContainer.getHeight()) * 2.0f;

        Animator reveal;
        if (infoContainer.getVisibility() == View.INVISIBLE) {
            infoContainer.setVisibility(View.VISIBLE);
            reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, cx, cy, 0, radius);
            reveal.setInterpolator(new AccelerateInterpolator(2.0f));
        } else {
            reveal = ViewAnimationUtils.createCircularReveal(
                    infoContainer, cx, cy, radius, 0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    infoContainer.setVisibility(View.INVISIBLE);
                }
            });
            reveal.setInterpolator(new DecelerateInterpolator(2.0f));
        }
        reveal.setDuration(600);
        reveal.start();
    }

}