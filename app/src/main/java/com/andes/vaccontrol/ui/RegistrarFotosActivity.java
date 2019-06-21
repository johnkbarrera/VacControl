package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vaccontrol.R;
import com.andes.vaccontrol.configuration.AppServices;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrarFotosActivity extends AppCompatActivity {

    ImageButton btn_atras;
    Button btn_guardar_fotos;
    Button btn_foto_frontal,btn_foto_posterior;
    ImageView iv_foto_frontal, iv_foto_posterior;
    TextView text_fecha;

    /* SERVICIOS WEB */
    private static String URL_SUBIR_FOTO = AppServices.URL_SUBIR_FOTO;
    private static String URL_ADD_FOTOS = AppServices.URL_ADD_FOTOS;


    String fecha,timestamp;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int posicion_de_imagen;
    String ruta_frontal,ruta_posterior;
    Bitmap bitmap_frontal, bitmap_posterior;

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
                Toast.makeText(RegistrarFotosActivity.this, "Retornando a la información del ganado", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(RegistrarFotosActivity.this, "foto frontal", Toast.LENGTH_SHORT).show();
                tomarfoto();
            }
        });


        btn_foto_posterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicion_de_imagen = 2;
                Toast.makeText(RegistrarFotosActivity.this, "foto posterior", Toast.LENGTH_SHORT).show();
                tomarfoto();
            }
        });


        btn_guardar_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verificar_campos()){
                    SimpleDateFormat stampFormat = new SimpleDateFormat("yyyyMMDDhhmmss", Locale.getDefault());
                    Date date = new Date();
                    timestamp = stampFormat.format(date);

                    // Guardando imagen 1
                    ruta_frontal = timestamp+"_GANADO"+ganado_id+"_FRONTAL";
                    GuardarUnaImagen(ruta_frontal,sesion, bitmap_frontal);

                    // Guardando imagen 2
                    ruta_posterior = timestamp+"_GANADO"+ganado_id+"_POSTERIOR";
                    GuardarUnaImagen(ruta_posterior,sesion, bitmap_posterior);

                    AddSesion(ganado_id,sesion);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();

                } else {
                    Toast.makeText(RegistrarFotosActivity.this, "Debes tomar las 2 fotos!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void tomarfoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            switch (posicion_de_imagen){
                case 1:
                    bitmap_frontal = (Bitmap) extras.get("data");
                    iv_foto_frontal.setImageBitmap(bitmap_frontal);
                    break;

                case 2:
                    bitmap_posterior = (Bitmap) extras.get("data");
                    iv_foto_posterior.setImageBitmap(bitmap_posterior);
                    break;
            }

        }
    }

    private boolean verificar_campos(){
        boolean status = true;
        if (bitmap_frontal == null){
            status =  false;
        }
        if (bitmap_posterior == null){
            status = false;
        }

        return status;
    }

    private void GuardarUnaImagen(final String nombre, String sesion, Bitmap data){
        final String estado = sesion.trim();
        final String nombre_foto = nombre.trim();
        final Bitmap data_foto = data;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBIR_FOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                // Toast.makeText(RegistrarFotosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                // Toast.makeText( RegistrarFotosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegistrarFotosActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrarFotosActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",estado);
                params.put("nombre",nombre_foto);
                params.put("data",convertirIngString(data_foto));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String convertirIngString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);

        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
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
                                Toast.makeText( RegistrarFotosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( RegistrarFotosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegistrarFotosActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrarFotosActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",estado);
                params.put("ganado_id",ganado_identificador);
                params.put("date",fecha);
                params.put("foto_frontal",ruta_frontal);
                params.put("foto_posterior",ruta_posterior);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
