package com.pal.mapd.api;

import com.pal.mapd.apiModels.nearby_models.NearbyList;
import com.pal.mapd.apiModels.places_models.PlacesList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PlacesAPI {

    private static final String baseUrl = "https://maps.googleapis.com/maps/";



    public static PlacesService placesService = null ;


    //Using singleton pattern for creating an instance of object only once
    public static PlacesService getPlacesService(){

        if(placesService == null){

            Retrofit retrofit= new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            placesService = retrofit.create(PlacesService.class);

        }
        return placesService;
    }



    public interface  PlacesService{
        @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyBwejucEA4uHmcE1JKEPbtXR1YG8vnFWqQ")
        Call<NearbyList> getResult(@Query(value = "type") String type  , @Query(value = "radius")String radius ,
                                   @Query(value = "location" ,encoded = true)String location);

        @GET("api/place/nearbysearch/json?key=AIzaSyBwejucEA4uHmcE1JKEPbtXR1YG8vnFWqQ&fields=formatted_address")
        Call<PlacesList> getPlace(@Query(value = "placeid" , encoded = true) String placeid);






    }



}
