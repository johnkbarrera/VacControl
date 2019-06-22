package com.andes.vaccontrol.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.andes.vaccontrol.configuration.AppServices;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.andes.vaccontrol.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CrearGanadoActivity extends AppCompatActivity {

    private static final String TAG = "CrearGanadoActivity";

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    ImageButton btn_atras;
    Button btn_add_ganado;
    EditText et_nombre, et_registro;
    Spinner spn_lista_razas, spn_lista_procedencias;
    ImageView btn_get_fecha;
    TextView et_fecha;
    EditText et_peso_dob;
    EditText et_rgm;
    EditText et_rgp;
    EditText et_v_madre;
    EditText et_v_padre;

    private static String URL_ADD_GANADO = AppServices.URL_ADD_GANADO;

    // DATOS
    String sesion;
    String establo_id;
    String ganado_nombre;
    String ganado_registro;
    String ganado_raza;
    String ganado_procedencia;
    int ganado_raza_pos, ganado_procedencia_pos;
    String ganado_dob;
    String ganado_peso;
    String ganado_rgm = "";
    String ganado_rgp = "";
    String ganado_v_madre = "";
    String ganado_v_padre = "";

    ArrayAdapter<String> adapter_raza;
    ArrayAdapter<String> adapter_procedencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ganado);

        btn_atras = findViewById(R.id.btn_g_volver);
        btn_add_ganado = findViewById(R.id.btn_g_registrar);
        et_nombre = findViewById(R.id.input_g_nombre);
        et_registro = findViewById(R.id.input_g_registro);
        spn_lista_razas = findViewById(R.id.spinner_g_raza);
        spn_lista_procedencias = findViewById(R.id.spinner_g_procedencia);
        btn_get_fecha = findViewById(R.id.btn_g_dob);
        et_fecha = findViewById(R.id.text_dob);
        et_peso_dob = findViewById(R.id.input_g_peso);
        et_rgm = findViewById(R.id.input_g_rgm);
        et_rgp = findViewById(R.id.input_g_rgp);
        et_v_madre = findViewById(R.id.input_g_v_madre);
        et_v_padre = findViewById(R.id.input_g_v_padre);


        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CrearGanadoActivity.this, "Retornando a mi establo", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });


        adapter_raza = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_razas));
        spn_lista_razas.setAdapter(adapter_raza);
        spn_lista_razas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ganado_raza_pos = position;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ganado_raza_pos = 0;
            }
        });

        adapter_procedencias = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_g_procedencias));
        spn_lista_procedencias.setAdapter(adapter_procedencias);
        spn_lista_procedencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ganado_procedencia_pos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ganado_procedencia_pos = 0;
            }
        });

        btn_get_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CrearGanadoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = year + "/" + month + "/" + day;
                et_fecha.setText(date);
            }
        };



        // Intentamos establo_1d y sesion

        Intent intent = getIntent();
        final Bundle establo_data = intent.getExtras();
        establo_id = establo_data.getString("identificador_establo");
        sesion = establo_data.getString("session");


        btn_add_ganado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificar_campos()){
                    add_datos_ganado(establo_id, sesion);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onBackPressed();
                } else {
                    Toast.makeText(CrearGanadoActivity.this, "Complete todos los campos requeridos!", Toast.LENGTH_SHORT).show();
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

        ganado_nombre = et_nombre.getText().toString();
        ganado_registro = et_registro.getText().toString();
        ganado_dob = et_fecha.getText().toString();
        ganado_peso = et_peso_dob.getText().toString();

        ganado_rgm = et_rgm.getText().toString();
        ganado_rgp = et_rgp.getText().toString();
        ganado_v_madre = et_v_madre.getText().toString();
        ganado_v_padre = et_v_padre.getText().toString();

        if (ganado_registro.isEmpty()){ganado_registro = "";}
        if (ganado_rgm.isEmpty()){ganado_rgm = "No Registrado";}
        if (ganado_rgp.isEmpty()){ganado_rgp = "No Registrado";}
        if (ganado_v_madre.isEmpty()){ganado_v_madre = "No Registrado";}
        if (ganado_v_padre.isEmpty()){ganado_v_padre = "No Registrado";}

        if (ganado_nombre.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "El ganado necesita tener nombre";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_nombre.setError(spannableStringBuilder);

        }else if(ganado_nombre.length()>50){
            estado = false;
            String errorString = "El nombre no debe exceder los 20 caracteres";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_nombre.setError(spannableStringBuilder);
        }

        if (ganado_dob.isEmpty()){
            estado = false;
            String errorString = "Registre la fecha de nacimiento del ganado";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_fecha.setError(spannableStringBuilder);
        }

        if (ganado_peso.isEmpty()){
            estado = false;
            String errorString = "Registre el peso del ganado (aprox.)";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_peso_dob.setError(spannableStringBuilder);
        }

        return estado;
    }

    private void add_datos_ganado(String id_establo, String sesion){

        final String establo_identificador = id_establo.trim();
        final String estado = sesion.trim();
        final String ganado_nombre = et_nombre.getText().toString().trim();
        final String ganado_registro_o = ganado_registro.trim();
        final String ganado_raza = adapter_raza.getItem(ganado_raza_pos).trim();
        final String ganado_procedencia = adapter_procedencias.getItem(ganado_procedencia_pos).trim();
        final String ganado_dob = et_fecha.getText().toString().trim();
        final String ganado_peso = et_peso_dob.getText().toString().trim();
        final String ganado_rgm_o = ganado_rgm.trim();
        final String ganado_rgp_o = ganado_rgp.trim();
        final String ganado_v_madre_o = ganado_v_madre.trim();
        final String ganado_v_padre_o = ganado_v_padre.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_GANADO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( CrearGanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( CrearGanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(CrearGanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CrearGanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("establo_id",establo_identificador);
                params.put("sesion",estado);
                params.put("nombre",ganado_nombre);
                params.put("registro",ganado_registro_o);
                params.put("raza",ganado_raza);
                params.put("procedencia",ganado_procedencia);
                params.put("dob",ganado_dob);
                params.put("peso_dob",ganado_peso);
                params.put("rgm",ganado_rgm_o);
                params.put("rgp",ganado_rgp_o);
                params.put("v_madre",ganado_v_madre_o);
                params.put("v_padre",ganado_v_padre_o);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}
