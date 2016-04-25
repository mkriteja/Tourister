package com.example.user.tourister;

import android.graphics.Bitmap;

import com.firebase.client.Firebase;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by user on 4/19/2016.
 */
public class AppManager {

    private static Bitmap sPhotoCache;

    private static String useremail;

    private static Firebase ref = new Firebase("https://tourister.firebaseio.com/");

    private static AppManager instance = new AppManager();

    private AppManager(){

    }

    public static AppManager getInstance( ) {
        return instance;
    }

    public static Firebase getRef() {
        return ref;
    }

    public static String getApiKey() {
        return "AIzaSyA7oAvJwhU-bgedvLqyS1TWdzlBS83nO5Q";
    }

    public static String getGooglePhotoUrl() {
        return "https://maps.googleapis.com/maps/api/place/photo?";
    }

    public static Bitmap getsPhotoCache() {
        return sPhotoCache;
    }

    public static void setsPhotoCache(Bitmap sPhotoCache) {
        AppManager.sPhotoCache = sPhotoCache;
    }

    public static String getUseremail() {
        return useremail;
    }

    public static void setUseremail(String useremail) {
        AppManager.useremail = useremail;
    }

    public static Retrofit getRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return  retrofit;
    }

}
