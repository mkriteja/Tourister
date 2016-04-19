package com.example.user.tourister;

import com.firebase.client.core.Repo;

import java.util.List;

import DataModel.com.example.user.tourister.Place;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by user on 4/18/2016.
 */
public interface PlacesInterface {

    @GET("maps/api/place/textsearch/json")
    Call<Place> getPlaces(@Query("query") String query,@Query("key") String key);
}
