package com.krishna.user.tourister;

import android.graphics.Bitmap;

import com.firebase.client.Firebase;

import DataModel.Place;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by user on 4/19/2016.
 */
public class AppManager {

    private static Bitmap sPhotoCache;

    private static Bitmap tourPhotoCache;

    private static Bitmap selectedPhoto;

    private static String transitionName;

    private static String useremail;

    private static Firebase ref = new Firebase("https://tourister.firebaseio.com/");

    private static Call<Place> apicall;

    private static AppManager instance = new AppManager();

    private AppManager(){

    }

    public static AppManager getInstance( ) {
        return instance;
    }

    public static Bitmap getSelectedPhoto() {
        return selectedPhoto;
    }

    public static void setSelectedPhoto(Bitmap selectedPhoto) {
        AppManager.selectedPhoto = selectedPhoto;
    }

    public static String getTransitionName() {
        return transitionName;
    }

    public static void setTransitionName(String transitionName) {
        AppManager.transitionName = transitionName;
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


    public static Call<Place> getApicall() {
        return AppManager.apicall;
    }

    public static void setApicall(Call<Place> call) {
        AppManager.apicall = call;
    }

    public static Bitmap getTourPhotoCache() {
        return tourPhotoCache;
    }

    public static void setTourPhotoCache(Bitmap tourPhotoCache) {
        AppManager.tourPhotoCache = tourPhotoCache;
    }

    public static void reset(){
        setUseremail(null);
        setApicall(null);
        setTourPhotoCache(null);
        setSelectedPhoto(null);
        setsPhotoCache(null);
        setTransitionName(null);
    }

}
