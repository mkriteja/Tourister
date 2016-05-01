package com.krishna.user.tourister;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class TourDetailActivity extends Activity {

    private TextView tourtitle;
    private TextView tourduration;
    private TextView tourdesc;
    private TextView tourlocation;
    private TextView tourcode;
    private TextView tourprice;
    private RatingBar tourrating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        //initilize
        tourtitle = (TextView) findViewById(R.id.title_tour);
        tourduration = (TextView) findViewById(R.id.duration_tour);
        tourdesc = (TextView) findViewById(R.id.desc_tour);
        tourlocation = (TextView) findViewById(R.id.placeaddress_tour);
        tourcode = (TextView) findViewById(R.id.tourcode_tour);
        tourprice = (TextView) findViewById(R.id.price_tour);
        tourrating = (RatingBar) findViewById(R.id.placeratingbar_tour);

        Bitmap photo = setupPhoto();

        colorize(photo);

        applySystemWindowsBottomInset(R.id.container_tour);
        getWindow().getEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                ImageView hero = (ImageView) findViewById(R.id.photo_tourd);
                ObjectAnimator color = ObjectAnimator.ofArgb(hero.getDrawable(), "tint", 0);
                color.start();
                getWindow().getEnterTransition().removeListener(this);
            }
        });

        setupText();

    }


    private Bitmap setupPhoto() {
        Bitmap bitmap = AppManager.getTourPhotoCache();
        ((ImageView) findViewById(R.id.photo_tourd)).setImageBitmap(bitmap);
        return bitmap;
    }

    private void colorize(Bitmap photo) {
        Palette palette = Palette.from(photo).generate();;
        applyPalette(palette);
    }

    private void applyPalette(Palette palette) {
        int DEFAULT_VAL = getResources().getColor(R.color.blue, null);
        getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor(DEFAULT_VAL)));
        tourtitle.setTextColor(palette.getVibrantColor(DEFAULT_VAL));
        tourduration.setTextColor(palette.getLightMutedColor(DEFAULT_VAL));
        tourcode.setTextColor(palette.getLightMutedColor(DEFAULT_VAL));
        tourdesc.setTextColor(palette.getLightMutedColor(DEFAULT_VAL));
        tourlocation.setTextColor(palette.getLightMutedColor(DEFAULT_VAL));
        tourprice.setTextColor(palette.getLightMutedColor(DEFAULT_VAL));
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

    @Override
    public void onBackPressed() {
        ImageView hero = (ImageView) findViewById(R.id.photo_tourd);
        ObjectAnimator color = ObjectAnimator.ofArgb(hero.getDrawable(), "tint",
                0);
        color.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finishAfterTransition();
            }
        });
        color.start();
    }

    private void setupText() {
        tourtitle.setText(getIntent().getStringExtra("title"));
        tourrating.setRating(Float.valueOf(getIntent().getStringExtra("rating")));
        tourdesc.setText(getIntent().getStringExtra("Description"));
        tourcode.setText(getIntent().getStringExtra("tcode"));
        tourlocation.setText(getIntent().getStringExtra("loc"));
        tourprice.setText(getIntent().getStringExtra("price") + " " + "$");
        tourduration.setText(getIntent().getStringExtra("Duration") + " " + "Hrs");
    }
}

