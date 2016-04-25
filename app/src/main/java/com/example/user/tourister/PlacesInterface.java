package com.example.user.tourister;

import DataModel.Place;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by user on 4/18/2016.
 */
public interface PlacesInterface {

    @GET("maps/api/place/textsearch/json")
    Call<Place> getPlaces(@Query("query") String query, @Query("key") String key);

    @GET("maps/api/place/details/json")
    Call<Place> getPlacesDetails(@Query("placeid") String query,@Query("key") String key);

}
