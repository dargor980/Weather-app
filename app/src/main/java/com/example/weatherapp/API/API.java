package com.example.weatherapp.API;

import com.example.weatherapp.API.Deserializers.MyDeserializer;
import com.example.weatherapp.Models.City;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    public static final String BASE_ICONS = "https://openweathermap.org/img/w/";
    public static final String EXTENSION_ICONS = ".png";

    private static Retrofit retrofit = null;

    public static final String APPKEY = "6a563288c35a76fde94e0a9eb5ff1076";

    public static Retrofit getApi(){
        if(retrofit == null){

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(City.class, new MyDeserializer());

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder.create()))
                    .build();
        }
        return retrofit;
    }
}
