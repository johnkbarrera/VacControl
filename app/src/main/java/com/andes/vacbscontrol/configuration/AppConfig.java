package com.andes.vacbscontrol.configuration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andes.vacbscontrol.configuration.AppServices.URL_PROYECTO;


public class AppConfig {


    //public static String BASE_URL = "http://165.22.210.70/vacbservices/";
    public static String BASE_URL = URL_PROYECTO;

    public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
