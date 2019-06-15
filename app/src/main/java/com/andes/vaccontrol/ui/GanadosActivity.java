package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.andes.vaccontrol.adapter.AdaptadorEstablos;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.andes.vaccontrol.R;
import com.andes.vaccontrol.adapter.AdaptadorGanados;
import com.andes.vaccontrol.configuration.AppServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GanadosActivity extends AppCompatActivity {


    /* HERRAMIENTAS PARA EL LAYOUT */
    TextView establo_title, establo_nom, establo_detalle, establo_num_ganados;
    ListView ganados_lista;
    LinearLayout lista_vacia;

    /* SERVICIOS WEB */
    private static String URL_GANADOS = AppServices.URL_GANADOS;
    ArrayList<ArrayList<String>> lista_de_ganados;


    String sesion;
    String establo_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganados);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Bundle establo_data = intent.getExtras();

        establo_id = establo_data.getString("establo_id");
        sesion = establo_data.getString("session");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent mi_nuevo_ganado = new Intent(view.getContext(), CrearGanadoActivity.class);
                mi_nuevo_ganado.putExtra("identificador_establo",establo_data.getString("establo_id"));
                mi_nuevo_ganado.putExtra("session",sesion);
                startActivity(mi_nuevo_ganado);
            }
        });

        establo_title = findViewById(R.id.text_e_numero);
        establo_nom = findViewById(R.id.text_e_nombre);
        establo_num_ganados = findViewById(R.id.text_e_ganados);
        establo_detalle = findViewById(R.id.text_e_detalle);

        establo_title.setText(establo_data.getString("posicion"));
        establo_nom.setText(establo_data.getString("nombre"));
        establo_detalle.setText(establo_data.getString("detalle")+", esta en "+establo_data.getString("ciudad")+" y su código es "+establo_data.getString("establo_id"));

        lista_vacia = findViewById(R.id.empty_list_g);
        ganados_lista = (ListView) findViewById(R.id.lista_vacas);

        /*
        lista_de_ganados = new ArrayList<ArrayList<String>>();
        ganados_lista.setAdapter(new AdaptadorEstablos(GanadosActivity.this, "Establo", lista_de_ganados));
        LeerGanados(establo_id, sesion);*/

        ganados_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent mi_ganado = new Intent(view.getContext(), MiGanadoActivity.class);
                mi_ganado.putExtra("posicion","Ganado "+(position+1));
                mi_ganado.putExtra("ganado_id",lista_de_ganados.get(position).get(0));
                mi_ganado.putExtra("nombre",lista_de_ganados.get(position).get(1));
                mi_ganado.putExtra("session",sesion);
                startActivity(mi_ganado);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Toast.makeText(GanadosActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();

            lista_de_ganados = new ArrayList<ArrayList<String>>();
            ganados_lista.setAdapter(new AdaptadorEstablos(GanadosActivity.this, "Establo", lista_de_ganados));
            LeerGanados(establo_id,sesion);

            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Toast.makeText(GanadosActivity.this,"Salir!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void LeerGanados(String id_establo,String sesion){

        final String establo_identificador = id_establo.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GANADOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");
                                establo_num_ganados.setText("Número de cabezas: "+message.length());
                                if(message.length() == 0) {
                                    // Lista Vacia
                                    lista_vacia.setVisibility(View.VISIBLE);
                                    Toast.makeText(GanadosActivity.this,"No registras ningun Ganado!", Toast.LENGTH_SHORT).show();
                                } else {
                                    lista_vacia.setVisibility(View.GONE);
                                    Toast.makeText(GanadosActivity.this,""+message.length(), Toast.LENGTH_SHORT).show();
                                   for (int i = 0; i < message.length(); i++) {
                                        String ganado_id = message.getJSONObject(i).getString("ganado_id");
                                        String nombre = message.getJSONObject(i).getString("nombre");
                                        String raza = message.getJSONObject(i).getString("raza");/*
                                        String registro = message.getJSONObject(i).getString("registro");
                                        String procedencia = message.getJSONObject(i).getString("procedencia");
                                        String dob = message.getJSONObject(i).getString("dob");
                                        String pesodob = message.getJSONObject(i).getString("pesodob");
                                        String rgm = message.getJSONObject(i).getString("rgm");
                                        String rgp = message.getJSONObject(i).getString("rgp");
                                        String v_madre = message.getJSONObject(i).getString("v_madre");
                                        String v_padre = message.getJSONObject(i).getString("v_padre");*/

                                        ArrayList<String> ar = new ArrayList<String>();
                                        ar.add(ganado_id);
                                        ar.add(nombre);
                                        ar.add(raza);/*
                                        ar.add(registro);
                                        ar.add(procedencia);
                                        ar.add(dob);
                                        ar.add(pesodob);
                                        ar.add(rgm);
                                        ar.add(rgp);
                                        ar.add(v_madre);
                                        ar.add(v_padre);*/
                                        lista_de_ganados.add(ar);
                                    }

                                    ganados_lista.setAdapter(new AdaptadorGanados(GanadosActivity.this, "Ganado", lista_de_ganados));

                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadosActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadosActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("establo_id",establo_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        final Bundle establo_data = intent.getExtras();
        establo_id = establo_data.getString("establo_id");
        sesion = establo_data.getString("session");
        lista_de_ganados = new ArrayList<ArrayList<String>>();
        ganados_lista.setAdapter(new AdaptadorEstablos(GanadosActivity.this, "Establo", lista_de_ganados));
        LeerGanados(establo_id,sesion);
    }

}
