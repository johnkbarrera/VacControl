package com.andes.vaccontrol.configuration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppConfig {

    public static String BASE_URL = "http://172.22.3.169/vacbservices/";

    public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
