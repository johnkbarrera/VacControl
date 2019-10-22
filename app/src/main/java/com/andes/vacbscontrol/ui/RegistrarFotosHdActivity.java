package com.andes.vacbscontrol.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vacbscontrol.BuildConfig;
import com.andes.vacbscontrol.R;
import com.andes.vacbscontrol.configuration.AppConfig;
import com.andes.vacbscontrol.configuration.AppServices;
import com.andes.vacbscontrol.configuration.ApiFotosConfig;
import com.andes.vacbscontrol.configuration.ServerResponse;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegistrarFotosHdActivity extends AppCompatActivity {

    ImageButton btn_atras;
    Button btn_guardar_fotos;
    Button btn_foto_frontal,btn_foto_posterior;
    ImageView iv_foto_frontal, iv_foto_posterior;
    TextView text_fecha;

    /* SERVICIOS WEB */
    private static String URL_ADD_FOTOS = AppServices.URL_ADD_FOTOS;





    private String mFrontalImageFileLocation = "";
    private String mPosteriorImageFileLocation = "";

    String fecha,timestamp;

    private static final int CAMERA_PIC_REQUEST = 1111;
    private int posicion_de_imagen;

    private static final String TAG = RegistrarFotosHdActivity.class.getSimpleName();

    String nombre_frontal, nombre_posterior;
    File photoFile_frontal = null;
    File photoFile_posterior = null;
    private boolean frontal_existe = false;
    private boolean posterior_existe = false;

    String ganado_id;
    String sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitrar_fotos);

        btn_atras = findViewById(R.id.btn_foto_volver);
        btn_guardar_fotos = findViewById(R.id.btn_foto_registrar);
        text_fecha = findViewById(R.id.tv_fotos_date);
        btn_foto_frontal = findViewById(R.id.btn_foto_frontal);
        btn_foto_posterior = findViewById(R.id.btn_foto_posterior);
        iv_foto_frontal = findViewById(R.id.image_frontal);
        iv_foto_posterior = findViewById(R.id.image_posterior);

        Intent intent = getIntent();
        final Bundle ganado_data = intent.getExtras();

        ganado_id = ganado_data.getString("ganado_id");
        sesion = ganado_data.getString("session");


        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegistrarFotosHdActivity.this, "Retornando a la información del ganado", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

        text_fecha.setText(fecha);


        btn_foto_frontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion_de_imagen = 1;
                Toast.makeText(RegistrarFotosHdActivity.this, "foto frontal", Toast.LENGTH_SHORT).show();
                captureImage();
            }
        });


        btn_foto_posterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion_de_imagen = 2;
                Toast.makeText(RegistrarFotosHdActivity.this, "foto posterior", Toast.LENGTH_SHORT).show();
                captureImage();
            }
        });


        btn_guardar_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verificar_campos()){


                    //Toast.makeText(RegistrarFotosHdActivity.this, "todo ok", Toast.LENGTH_SHORT).show();

                    uploadFile(photoFile_frontal);
                    uploadFile(photoFile_posterior);

                    AddSesion(ganado_id,sesion);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();

                } else {
                    Toast.makeText(RegistrarFotosHdActivity.this, "Debes tomar las 2 fotos!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK & requestCode == CAMERA_PIC_REQUEST){
            switch (posicion_de_imagen){
                case 1:
                    Glide.with(this).load(mFrontalImageFileLocation).into(iv_foto_frontal);
                    frontal_existe = true;
                    break;

                case 2:
                    Glide.with(this).load(mPosteriorImageFileLocation).into(iv_foto_posterior);
                    posterior_existe = true;
                    break;
            }
        }
    }

    private void captureImage() {
            Intent callCameraApplicationIntent = new Intent();
            callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

            File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app_vacas");
            // Then we create the storage directory if does not exists
            if (!storageDirectory.exists()) storageDirectory.mkdir();

            SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault());
            Date date = new Date();

            switch (posicion_de_imagen){

                case 1:
                    timestamp = stampFormat.format(date);
                    nombre_frontal = timestamp+"_GANADO"+ganado_id+"_FRONTAL";

                    // Here we create the file using a prefix, a suffix and a directory
                    photoFile_frontal = new File(storageDirectory, nombre_frontal + ".jpg");
                    mFrontalImageFileLocation = photoFile_frontal.getAbsolutePath();

                    // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
                    Uri outputUri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile_frontal);
                    callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                    break;

                case 2:
                    timestamp = stampFormat.format(date);
                    nombre_posterior = timestamp+"_GANADO"+ganado_id+"_POSTERIOR";
                    // Here we create the file using a prefix, a suffix and a directory
                    photoFile_posterior = new File(storageDirectory, nombre_posterior + ".jpg");
                    mPosteriorImageFileLocation = photoFile_posterior.getAbsolutePath();

                    // Here we add an extra file to the intent to put the address on to. For this purpose we use the FileProvider, declared in the AndroidManifest.
                    Uri outputUri2 = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile_posterior);
                    callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri2);
                    break;
            }

            // The following is a new line with a trying attempt
            callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // The following strings calls the camera app and wait for his file in return.
            startActivityForResult(callCameraApplicationIntent, CAMERA_PIC_REQUEST);
    }

    private boolean verificar_campos(){
        boolean status = true;
        if (!frontal_existe){
            status =  false;
        }
        if (!posterior_existe){
            status = false;
        }
        return status;
    }

    private void uploadFile(File postPath) {

            // Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> map = new HashMap<>();
            File file = postPath;

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
            ApiFotosConfig getResponse = AppConfig.getRetrofit().create(ApiFotosConfig.class);
            Call<ServerResponse> call = getResponse.upload("token", map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ServerResponse serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "problemas subiendo las imagenes", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    Log.v("Error: ", t.getMessage());
                }
            });
    }

    private void AddSesion(String ganado_id, String sesion){
        final String ganado_identificador = ganado_id.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_FOTOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( RegistrarFotosHdActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( RegistrarFotosHdActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegistrarFotosHdActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrarFotosHdActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",estado);
                params.put("ganado_id",ganado_identificador);
                params.put("date",fecha);
                params.put("foto_frontal",nombre_frontal);
                params.put("foto_posterior",nombre_posterior);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
