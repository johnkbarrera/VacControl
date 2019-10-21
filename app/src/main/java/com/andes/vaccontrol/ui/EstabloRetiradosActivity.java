package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.andes.vaccontrol.R;
import com.andes.vaccontrol.SessionManager;
import com.andes.vaccontrol.adapter.AdaptadorEstablos;
import com.andes.vaccontrol.adapter.AdaptadorGanado;
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

public class EstabloRetiradosActivity extends AppCompatActivity {


    /* HERRAMIENTAS PARA EL LAYOUT */
    ListView ganados_lista;
    LinearLayout lista_vacia;

    /* SERVICIOS WEB */
    private static String URL_LISTA_GANADO_ELIMINADO = AppServices.URL_LISTA_GANADO_ELIMINADO;
    ArrayList<ArrayList<String>> lista_de_ganados;


    String sesion;
    String establo_id;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establo_retirados);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Bundle establo_data = intent.getExtras();

        establo_id = establo_data.getString("establo_id");
        sesion = establo_data.getString("session");

        lista_vacia = findViewById(R.id.empty_list_g);
        ganados_lista = (ListView) findViewById(R.id.lista_vacas);

        ganados_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent mi_ganado_eliminado = new Intent(view.getContext(), GanadoRetiradoActivity.class);
                mi_ganado_eliminado.putExtra("posicion","Ganado "+(position+1));
                mi_ganado_eliminado.putExtra("ganado_id",lista_de_ganados.get(position).get(0));
                mi_ganado_eliminado.putExtra("nombre",lista_de_ganados.get(position).get(1));
                mi_ganado_eliminado.putExtra("session",sesion);
                startActivity(mi_ganado_eliminado);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eliminado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Toast.makeText(EstabloRetiradosActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();

            lista_de_ganados = new ArrayList<ArrayList<String>>();
            ganados_lista.setAdapter(new AdaptadorEstablos(EstabloRetiradosActivity.this, "Establo", lista_de_ganados));
            LeerGanados(establo_id,sesion);

            return true;
        }

        if (id == R.id.action_atras) {

            Toast.makeText(EstabloRetiradosActivity.this,"Retornando al mi Establo!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void LeerGanados(String id_establo,String sesion){

        final String establo_identificador = id_establo.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LISTA_GANADO_ELIMINADO,
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
                                    Toast.makeText(EstabloRetiradosActivity.this,"No registras ningun Ganado!", Toast.LENGTH_SHORT).show();
                                } else {
                                    lista_vacia.setVisibility(View.GONE);

                                    Toast.makeText(EstabloRetiradosActivity.this,"N° de cabezas de ganado eliminado: "+message.length(), Toast.LENGTH_SHORT).show();

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

                                    ganados_lista.setAdapter(new AdaptadorGanado(EstabloRetiradosActivity.this, "Ganado", lista_de_ganados));

                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( EstabloRetiradosActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(EstabloRetiradosActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EstabloRetiradosActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
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
        ganados_lista.setAdapter(new AdaptadorEstablos(EstabloRetiradosActivity.this, "Establo", lista_de_ganados));
        LeerGanados(establo_id,sesion);
    }

}
