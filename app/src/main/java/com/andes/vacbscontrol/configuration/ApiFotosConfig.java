package com.andes.vacbscontrol.configuration;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface ApiFotosConfig {

    @Multipart
    @POST("services/subir_foto_hd.php")

    Call<ServerResponse> upload(
        @Header("Authorization") String authorization,
        @PartMap Map<String, RequestBody> map
    );
}