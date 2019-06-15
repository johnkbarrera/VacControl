package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class CrearEstabloActivity extends AppCompatActivity {

    ImageButton btn_atras;
    Button btn_add_establo;
    EditText et_nombre, et_detalle;
    Spinner spn_lista_paises, spn_lista_regiones;
    EditText et_ciudad, et_distrito;
    TextView tv_altitud, tv_latitud, tv_longitud;
    Button btn_coordenadas;

    private static String URL_ADD_ESTABLO = AppServices.URL_ADD_ESTABLO;

    // DATOS
    String sesion;
    String ganadero_usuario;
    String establo_nombre;
    String establo_detalle;
    String establo_pais;
    String establo_region;
    int establo_pais_pos, establo_region_pos;
    String establo_ciudad;
    String establo_distrito;
    String establo_altitud;
    String establo_latitud;
    String establo_longitud;

    ArrayAdapter<String> adapter_pais;
    ArrayAdapter<String> adapter_region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_establo);

        btn_atras = findViewById(R.id.btn_e_volver);
        btn_add_establo = findViewById(R.id.btn_e_registrar);
        et_nombre = findViewById(R.id.input_e_nombre);
        et_detalle = findViewById(R.id.input_e_detalle);
        spn_lista_paises = findViewById(R.id.spinner_e_pais);
        spn_lista_regiones = findViewById(R.id.spinner_e_region);
        et_ciudad = findViewById(R.id.input_e_ciudad);
        et_distrito = findViewById(R.id.input_e_comuna);
        btn_coordenadas = findViewById(R.id.btn_e_coordenadas);
        tv_altitud = findViewById(R.id.tag_e_altitud);
        tv_latitud = findViewById(R.id.tag_e_latitud);
        tv_longitud = findViewById(R.id.tag_e_longitud);


        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CrearEstabloActivity.this, "Retornando al Inicio", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        adapter_pais = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_paises));
        spn_lista_paises.setAdapter(adapter_pais);
        spn_lista_paises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                establo_pais_pos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                establo_pais_pos = 0;
            }
        });

        adapter_region = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_regiones));
        spn_lista_regiones.setAdapter(adapter_region);
        spn_lista_regiones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                establo_region_pos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                establo_region_pos = 0;
            }
        });


        // Intentamos al ganadero_usuario  y sesion

        Intent intent = getIntent();
        final Bundle ganadero_data = intent.getExtras();
        ganadero_usuario = ganadero_data.getString("usuario_ganadero");
        sesion = ganadero_data.getString("session");


        btn_add_establo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificar_campos()){
                    add_datos_establo(ganadero_usuario, sesion);
                    onBackPressed();
                } else {
                    Toast.makeText(CrearEstabloActivity.this, "Complete todos los campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // por defecto si escribes aquí el super el botón hará lo que debía hacer si lo quitas ya no hará lo que debía de hacer y puedes programar otros comportamientos.
        //Quita el super y has un finish() a la actividad o bien replanteate bien lo que quieres hacer cuando se presione hacia atrás.
    }

    private boolean verificar_campos(){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        establo_nombre = et_nombre.getText().toString();
        establo_detalle = et_detalle.getText().toString();
        establo_ciudad = et_ciudad.getText().toString();
        establo_distrito = et_distrito.getText().toString();

        if (establo_nombre.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "Debes ingresar un nombre";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_nombre.setError(spannableStringBuilder);

        } else if(establo_nombre.length()>30){
            estado = false;
            String errorString = "El nombre no debe exceder los 30 caracteres";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_nombre.setError(spannableStringBuilder);
        }

        if (establo_detalle.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "Debes ingresar detalles";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_detalle.setError(spannableStringBuilder);

        } else if(establo_detalle.length()>200){
            estado = false;
            String errorString = "No debes exceder los 200 caracteres";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_detalle.setError(spannableStringBuilder);
        }

        if (establo_ciudad.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "Debes ingresar una ciudad";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_ciudad.setError(spannableStringBuilder);

        } else if(establo_ciudad.length()>50){
            estado = false;
            String errorString = "El nombre de la ciudad no debe exceder los 50 caracteres";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_nombre.setError(spannableStringBuilder);
        }

        if (establo_distrito.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "Debes ingresar un distrito";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_distrito.setError(spannableStringBuilder);

        } else if(establo_distrito.length()>50){
            estado = false;
            String errorString = "El nombre del distrito no debe exceder los 50 caracteres";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_distrito.setError(spannableStringBuilder);
        }

        return estado;
    }

    private void add_datos_establo(String usuario_ganadero, String sesion){

        final String ganadero_identificador = usuario_ganadero.trim();
        final String estado = sesion.trim();

        final String establo_nombre = et_nombre.getText().toString().trim();
        final String establo_detalle = et_detalle.getText().toString().trim();
        final String establo_pais = adapter_pais.getItem(establo_pais_pos).trim();
        final String establo_region = adapter_region.getItem(establo_region_pos).trim();
        final String establo_ciudad = et_ciudad.getText().toString().trim();
        final String establo_distrito = et_distrito.getText().toString().trim();

        final String establo_latitud = "23.30";
        final String establo_longitud= "23.2456";
        final String establo_altitud = "32.232";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_ESTABLO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( CrearEstabloActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( CrearEstabloActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(CrearEstabloActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CrearEstabloActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganadero_usuario",ganadero_identificador);
                params.put("sesion",estado);
                params.put("nombre",establo_nombre);
                params.put("detalle",establo_detalle);
                params.put("pais",establo_pais);
                params.put("region",establo_region);
                params.put("ciudad",establo_ciudad);
                params.put("comuna",establo_distrito);
                params.put("latitud",establo_latitud);
                params.put("longitud",establo_longitud);
                params.put("altitud",establo_altitud);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }




}
