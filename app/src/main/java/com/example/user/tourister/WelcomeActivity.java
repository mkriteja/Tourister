package com.example.user.tourister;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

public class WelcomeActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, WelcomeFragment.newInstance())
                .commit();

    }
}
