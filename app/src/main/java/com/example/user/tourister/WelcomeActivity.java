package com.example.user.tourister;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.firebase.client.Firebase;

public class WelcomeActivity extends FragmentActivity{

    private Fragment welcomeFragmentInstance;
    private static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);

        if (savedInstanceState != null) {
            welcomeFragmentInstance = getSupportFragmentManager().getFragment(savedInstanceState, "welcome");
            if (welcomeFragmentInstance == null) {
                welcomeFragmentInstance = WelcomeFragment.newInstance();
            }
        } else {
            welcomeFragmentInstance = WelcomeFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, welcomeFragmentInstance)
                .commit();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (welcomeFragmentInstance != null && welcomeFragmentInstance.isAdded())
            getSupportFragmentManager().putFragment(savedInstanceState, "welcome", welcomeFragmentInstance);
    }
}
