package com.andes.vaccontrol.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vaccontrol.R;
import com.andes.vaccontrol.adapter.AdaptadorProduccion;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GanadoRetiradoActivity extends AppCompatActivity {

    /* HERRAMIENTAS PARA EL LAYOUT */
    TextView ganado_title, ganado_nom, ganado_registro, ganado_raza, ganado_procedencia;
    TextView ganado_dob, ganado_peso_dob, ganado_rpm, ganado_vmadre, ganado_rpg, ganado_vpadre;

    TextView ganado_prof_ubre, ganado_prof_corporal, ganado_corp_bsc,  ganado_fecha_monitoreo, ganado_fecha_monitoreo_2;

    TextView ganado_reproduccion, ganado_estado_actual, ganado_peso_actual, ganado_fecha_celo;

    ImageButton fab_detalle, fab_estado_nutricional,fab_reproduccion, fab_potencial_genetico;
    LinearLayout lay_detalle, lay_estado_nutricional,lay_reproduccion, lay_potencial_genetico;

    Boolean isOpenDetalle = true;
    Boolean isOpenEstadoNutricional = true;
    Boolean isOpenPotencialGenetico = true;
    Boolean isOpenReproduccion = true;

    /* SERVICIOS WEB */
    private static String URL_VER_GANADOS = AppServices.URL_VER_GANADO;
    private static String URL_VER_GANADO_REPRODUCCION = AppServices.URL_VER_GANADO_REPRODUCCION;
    private static String URL_VER_GANADO_MONITORERO = AppServices.URL_VER_GANADO_MONITORERO;
    private static String URL_VER_GANADO_PRODUCCION = AppServices.URL_VER_GANADO_PRODUCCION;


    /* VARIABLES */
    String ganado_id;
    String sesion;

    ListView producciones_lista;
    LinearLayout lista_vacia;
    ArrayList<ArrayList<String>> lista_de_produccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganado_retirado);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ganado_title = findViewById(R.id.text_g_numero);
        ganado_nom = findViewById(R.id.text_g_nombre);
        ganado_raza = findViewById(R.id.text_g_raza);
        ganado_procedencia = findViewById(R.id.text_g_procedencia);
        ganado_registro = findViewById(R.id.text_g_registro);


        ganado_dob = findViewById(R.id.tv_gd_dob);
        ganado_peso_dob =findViewById(R.id.tv_gd_pesodob);
        ganado_rpm = findViewById(R.id.tv_gd_rgm);
        ganado_vmadre = findViewById(R.id.tv_gd_vmadre);
        ganado_rpg = findViewById(R.id.tv_gd_rgp);
        ganado_vpadre = findViewById(R.id.tv_gd_vpadre);

        ganado_prof_ubre = findViewById(R.id.tv_gd_profubre);
        ganado_prof_corporal = findViewById(R.id.tv_gd_profcorp);
        ganado_corp_bsc = findViewById(R.id.tv_gd_bsc);
        ganado_fecha_monitoreo = findViewById(R.id.tv_gd_monitoreo_fecha);
        ganado_fecha_monitoreo_2 = findViewById(R.id.tv_gd_monitoreo_fecha_2);

        ganado_reproduccion = findViewById(R.id.tv_gd_preñada);
        ganado_estado_actual = findViewById(R.id.tv_gd_estado_vaca);
        ganado_peso_actual = findViewById(R.id.tv_gd_peso);
        ganado_fecha_celo = findViewById(R.id.tv_gd_fecha_celo);

        lista_vacia = findViewById(R.id.empty_list_gd_prod);
        producciones_lista = (ListView) findViewById(R.id.lista_vaca_produccion);

        producciones_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent mi_produccion = new Intent(view.getContext(), GanadoRetiradoActivity.class);
                mi_produccion.putExtra("posicion","Ganado "+(position+1));
                mi_produccion.putExtra("ganado_id",lista_de_produccion.get(position).get(0));
                mi_produccion.putExtra("nombre",lista_de_produccion.get(position).get(1));
                mi_produccion.putExtra("session",sesion);
                //startActivity(mi_produccion);
            }
        });


        Intent intent = getIntent();
        final Bundle ganado_data = intent.getExtras();

        ganado_id = ganado_data.getString("ganado_id");
        sesion = ganado_data.getString("session");

        ganado_title.setText(ganado_data.getString("posicion"));
        ganado_nom.setText(ganado_data.getString("nombre"));

        /* cargamos datos de la generales*/

        LeerGanado(ganado_id, sesion);

        fab_detalle = findViewById(R.id.fab_detalles_ganado);
        lay_detalle = findViewById(R.id.lay_detalles_ganado);

        fab_detalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenDetalle) {
                    lay_detalle.setVisibility(View.VISIBLE);
                    //lay_detalle.startAnimation(fab_open);
                    //fab_detalle.startAnimation(fab_anticlock);
                    fab_detalle.setRotation(0);
                    isOpenDetalle = true;
                } else {
                    lay_detalle.setVisibility(View.GONE);
                    //lay_detalle.startAnimation(fab_close);
                    //fab_detalle.startAnimation(fab_clock);
                    fab_detalle.setRotation(180);
                    isOpenDetalle = false;
                }
            }
        });

        fab_estado_nutricional = findViewById(R.id.fab_estado_nutricional_ganado);
        lay_estado_nutricional = findViewById(R.id.lay_estado_nutricional_ganado);

        fab_estado_nutricional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenEstadoNutricional) {
                    lay_estado_nutricional.setVisibility(View.VISIBLE);
                    //lay_detalle.startAnimation(fab_open);
                    //fab_detalle.startAnimation(fab_anticlock);
                    fab_estado_nutricional.setRotation(0);
                    isOpenEstadoNutricional = true;
                } else {
                    lay_estado_nutricional.setVisibility(View.GONE);
                    //lay_detalle.startAnimation(fab_close);
                    //fab_detalle.startAnimation(fab_clock);
                    fab_estado_nutricional.setRotation(180);
                    isOpenEstadoNutricional = false;
                }
            }
        });

        fab_potencial_genetico = findViewById(R.id.fab_potencial_genetico_ganado);
        lay_potencial_genetico = findViewById(R.id.lay_potencial_genetico_ganado);
        fab_potencial_genetico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenPotencialGenetico) {
                    lay_potencial_genetico.setVisibility(View.VISIBLE);
                    //lay_detalle.startAnimation(fab_open);
                    //fab_detalle.startAnimation(fab_anticlock);
                    fab_potencial_genetico.setRotation(0);
                    isOpenPotencialGenetico = true;
                } else {
                    lay_potencial_genetico.setVisibility(View.GONE);
                    //lay_detalle.startAnimation(fab_close);
                    //fab_detalle.startAnimation(fab_clock);
                    fab_potencial_genetico.setRotation(180);
                    isOpenPotencialGenetico = false;
                }
            }
        });

        fab_reproduccion = findViewById(R.id.fab_reproduccion_ganado);
        lay_reproduccion = findViewById(R.id.lay_reproduccion_ganado);

        fab_reproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenReproduccion) {
                    lay_reproduccion.setVisibility(View.VISIBLE);
                    //lay_detalle.startAnimation(fab_open);
                    //fab_detalle.startAnimation(fab_anticlock);
                    fab_reproduccion.setRotation(0);
                    isOpenReproduccion = true;
                } else {
                    lay_reproduccion.setVisibility(View.GONE);
                    //lay_detalle.startAnimation(fab_close);
                    //fab_detalle.startAnimation(fab_clock);
                    fab_reproduccion.setRotation(180);
                    isOpenReproduccion = false;
                }
            }
        });


    }

    @Override protected void onResume() {
        super.onResume();

        // CARGAMOS Y VISUALIZAMOS DATOS DEL GANADERO
        ver_reproduccion();
        ver_monitoreo();

        lista_de_produccion = new ArrayList<ArrayList<String>>();
        producciones_lista.setAdapter(new AdaptadorProduccion(GanadoRetiradoActivity.this, "Establo", lista_de_produccion));
        LeerProduccionGanado(ganado_id,sesion);
    }

    private void ver_monitoreo()  {
        final String ganado_identificador = ganado_id.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VER_GANADO_MONITORERO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");

                                if (message.length()==0){}
                                else {
                                    String ubre_prof = message.getJSONObject(0).getString("prof_ubre");
                                    String corp_prof = message.getJSONObject(0).getString("prof_corp");
                                    String corp_bsc = message.getJSONObject(0).getString("bsc");
                                    String fecha_examen = message.getJSONObject(0).getString("fecha");


                                    ganado_prof_ubre.setText(ubre_prof);
                                    ganado_prof_corporal.setText(corp_prof);
                                    ganado_corp_bsc.setText(corp_bsc);
                                    ganado_fecha_monitoreo.setText(fecha_examen);
                                    ganado_fecha_monitoreo_2.setText(fecha_examen);
                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( GanadoRetiradoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoRetiradoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoRetiradoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganado_id",ganado_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void ver_reproduccion() {
        final String ganado_identificador = ganado_id.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VER_GANADO_REPRODUCCION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");

                                if (message.length()==0){}
                                else {
                                    String estado_rep = message.getJSONObject(0).getString("estado");
                                    String estado_vaca = message.getJSONObject(0).getString("estado_vaca");
                                    String peso_actual = message.getJSONObject(0).getString("peso");
                                    String ultimo_celo = message.getJSONObject(0).getString("ultimo_celo");
                                    if (estado_rep.equals("t")){ estado_rep = "Si";}
                                    if (estado_rep.equals("f")){ estado_rep = "No";}

                                    ganado_reproduccion.setText(estado_rep);
                                    ganado_estado_actual.setText(estado_vaca);
                                    ganado_peso_actual.setText(peso_actual);
                                    ganado_fecha_celo.setText(ultimo_celo);
                                }

                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( GanadoRetiradoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoRetiradoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoRetiradoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganado_id",ganado_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eliminado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(GanadoRetiradoActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();
            lista_de_produccion = new ArrayList<ArrayList<String>>();
            producciones_lista.setAdapter(new AdaptadorProduccion(GanadoRetiradoActivity.this, "Establo", lista_de_produccion));
            LeerProduccionGanado(ganado_id,sesion);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_atras) {

            Toast.makeText(GanadoRetiradoActivity.this,"Retornando a mi Establo!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LeerGanado(String ganado_id, String sesion){
        final String ganado_identificador = ganado_id.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VER_GANADOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");

                                String raza = message.getJSONObject(0).getString("raza");
                                String procedencia = message.getJSONObject(0).getString("procedencia");
                                String registro = message.getJSONObject(0).getString("registro");

                                ganado_raza.setText(raza);
                                ganado_procedencia.setText(procedencia);
                                ganado_registro.setText(registro);

                                String dob = message.getJSONObject(0).getString("dob");
                                String peso_dob = message.getJSONObject(0).getString("pesodob");
                                String rgm = message.getJSONObject(0).getString("rgm");
                                String vmadre = message.getJSONObject(0).getString("v_madre");
                                String rgp = message.getJSONObject(0).getString("rgp");
                                String vpadre = message.getJSONObject(0).getString("v_padre");

                                ganado_dob.setText(dob);
                                ganado_peso_dob.setText(peso_dob);
                                ganado_rpm.setText(rgm);
                                ganado_vmadre.setText(vmadre);
                                ganado_rpg.setText(rgp);
                                ganado_vpadre.setText(vpadre);

                                String estado_saca = message.getJSONObject(0).getString("saca_estado");
                                String motivo_saca = message.getJSONObject(0).getString("saca_motivo");
                                String fecha_saca = message.getJSONObject(0).getString("saca_fecha");

                                if (estado_saca.equals("t")){
                                    final LinearLayout layout_saca = (LinearLayout) findViewById(R.id.layout_saca);
                                    final TextView motivo_de_saca = (TextView) findViewById(R.id.tv_motivo_saca);
                                    final TextView fecha_de_saca = (TextView) findViewById(R.id.tv_fecha_saca);

                                    layout_saca.setVisibility(View.VISIBLE);
                                    motivo_de_saca.setText("El motivo de Saca es: "+motivo_saca);
                                    fecha_de_saca.setText("La fecha de Saca es: "+fecha_saca);

                                }

                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( GanadoRetiradoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoRetiradoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoRetiradoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganado_id",ganado_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void LeerProduccionGanado(String id_ganado,String sesion){

        final String ganado_identificador = id_ganado.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VER_GANADO_PRODUCCION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");
                                if(message.length() == 0) {
                                    // Lista Vacia
                                    lista_vacia.setVisibility(View.VISIBLE);
                                    Toast.makeText(GanadoRetiradoActivity.this,"No registras producción!", Toast.LENGTH_SHORT).show();
                                } else {
                                    lista_vacia.setVisibility(View.GONE);

                                    //Toast.makeText(GanadoActivity.this,""+message.length(), Toast.LENGTH_SHORT).show();

                                    for (int i = 0; i < message.length(); i++) {
                                        String produccion_id = message.getJSONObject(i).getString("produccion_id");
                                        String litro_en_prod = message.getJSONObject(i).getString("litros_leche");
                                        String solidos_en_prod = message.getJSONObject(i).getString("solidos");
                                        String csomaticas_en_prod = message.getJSONObject(i).getString("c_somaticas");
                                        String estado_en_prod = message.getJSONObject(i).getString("estado_prod");
                                        String fecha_de_prod = message.getJSONObject(i).getString("fecha");
                                        String hora_en_prod = message.getJSONObject(i).getString("hora");

                                        if (solidos_en_prod.equals("-1.00")) {solidos_en_prod = "No Registra";}
                                        if (csomaticas_en_prod.equals("-1.00")) {csomaticas_en_prod = "No Registra";}

                                        ArrayList<String> ar = new ArrayList<String>();
                                        ar.add(produccion_id);
                                        ar.add(litro_en_prod);
                                        ar.add(solidos_en_prod);
                                        ar.add(csomaticas_en_prod);
                                        ar.add(estado_en_prod);
                                        ar.add(fecha_de_prod);
                                        ar.add(hora_en_prod);
                                        lista_de_produccion.add(ar);
                                    }

                                    producciones_lista.setAdapter(new AdaptadorProduccion(GanadoRetiradoActivity.this, "Ordeño", lista_de_produccion));

                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoRetiradoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoRetiradoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoRetiradoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganado_id",ganado_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
