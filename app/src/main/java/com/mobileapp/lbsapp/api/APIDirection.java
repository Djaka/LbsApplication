package com.mobileapp.lbsapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIDirection {

    private static String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    public static Retrofit setInit(){
        return new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static APIDirection getInstance(){
        return setInit().create(APIDirection.class);
    }

}
