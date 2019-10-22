package com.andes.vacbscontrol.ui;

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

import com.andes.vacbscontrol.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.andes.vacbscontrol.R;
import com.andes.vacbscontrol.adapter.*;
import com.andes.vacbscontrol.configuration.AppServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    /* HERRAMIENTAS PARA EL LAYOUT */
    ListView lista;
    TextView tvusuario,tvnombre,tvemail;
    LinearLayout lista_vacia;

    /* SERVICIOS WEB */
    private static String URL_GANADERO = AppServices.URL_GANADERO;
    private static String URL_ESTABLOS = AppServices.URL_ESTABLOS;


    // EXTRAEMOS DATOS DE ANDROID INTENT QUE NOS TRAE DATOS DEL USUARIO
    String sesion = "";
    String usuario;
    String nombres;
    String apellidos;

    ArrayList<ArrayList<String>> lista_de_establos;
    private AccessController view;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String,String> user = sessionManager.getUserDetail();
        usuario = user.get(sessionManager.EMAIL);
        sesion = "iniciado";
        nombres = user.get(sessionManager.NAME);
        apellidos = user.get(sessionManager.LASTNAME);;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mi_nuevo_establo = new Intent(view.getContext(), CrearEstabloActivity.class);
                mi_nuevo_establo.putExtra("usuario_ganadero",usuario);
                mi_nuevo_establo.putExtra("session",sesion);
                startActivity(mi_nuevo_establo);

            }
        });

        // DEFINIMOS EL LAYOUT
        tvusuario = findViewById(R.id.text_e_numero);
        tvnombre = findViewById(R.id.text_e_nombre);
        tvemail = findViewById(R.id.text_e_ganados);
        lista_vacia = findViewById(R.id.empty_list_e);
        lista = (ListView) findViewById(R.id.lista_establos);


        tvnombre.setText(nombres +" "+apellidos);
        tvemail.setText(usuario);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent mi_establo = new Intent(view.getContext(), EstabloActivity.class);
                mi_establo.putExtra("establo_id",lista_de_establos.get(position).get(0));
                mi_establo.putExtra("nombre",lista_de_establos.get(position).get(1));
                mi_establo.putExtra("detalle",lista_de_establos.get(position).get(2));
                mi_establo.putExtra("ciudad",lista_de_establos.get(position).get(4));
                mi_establo.putExtra("posicion","Establo "+(position+1));
                mi_establo.putExtra("session",sesion);
                startActivity(mi_establo);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
            Toast.makeText(HomeActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();

            lista_de_establos = new ArrayList<ArrayList<String>>();
            lista.setAdapter(new AdaptadorEstablos(HomeActivity.this, "Establo", lista_de_establos));
            LeerGanadero(usuario,sesion);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(HomeActivity.this,"Cerrando Sesión!", Toast.LENGTH_SHORT).show();
            sessionManager.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LeerEstablos(String id_ganadero,String sesion){

        final String ganadero_identificador = id_ganadero.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ESTABLOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");
                                if(message.length() == 0) {
                                    lista_vacia.setVisibility(View.VISIBLE);
                                    Toast.makeText(HomeActivity.this,"No registras ningun Establo!", Toast.LENGTH_SHORT).show();
                                } else {
                                    lista_vacia.setVisibility(View.GONE);

                                    //Toast.makeText(HomeActivity.this,""+message.length(), Toast.LENGTH_SHORT).show();

                                    for (int i = 0; i < message.length(); i++) {
                                        String establo_id = message.getJSONObject(i).getString("establo_id");
                                        String nombre = message.getJSONObject(i).getString("nombre");
                                        String detalle = message.getJSONObject(i).getString("detalle");
                                        String pais = message.getJSONObject(i).getString("pais");
                                        String region = message.getJSONObject(i).getString("region");
                                        String ciudad = message.getJSONObject(i).getString("ciudad");
                                        String comuna = message.getJSONObject(i).getString("comuna");
                                        String latitud = message.getJSONObject(i).getString("latitud");
                                        String longitud = message.getJSONObject(i).getString("longitud");
                                        String altitud = message.getJSONObject(i).getString("altitud");

                                        ArrayList<String> ar = new ArrayList<String>();
                                        ar.add(establo_id);
                                        ar.add(nombre);
                                        ar.add(detalle);
                                        ar.add(pais);
                                        ar.add(region);
                                        ar.add(ciudad);
                                        ar.add(comuna);
                                        ar.add(latitud);
                                        ar.add(longitud);
                                        ar.add(altitud);
                                        lista_de_establos.add(ar);
                                    }
                                    lista.setAdapter(new AdaptadorEstablos(HomeActivity.this, "Establo", lista_de_establos));
                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganadero_id",ganadero_identificador);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void LeerGanadero(String usuario, String sesion){
        final String ganadero_usuario = usuario.trim();
        final String estado = sesion.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GANADERO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONArray message = jsonObject.getJSONArray("message");

                                String ganadero_id = message.getJSONObject(0).getString("index");

                                tvusuario.setText("Ganadero ID: "+ganadero_id);

                                LeerEstablos(ganadero_id,estado);
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ganadero_usuario",ganadero_usuario);
                params.put("sesion",estado);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override protected void onResume() {
        super.onResume();

        // CARGAMOS Y VISUALIZAMOS DATOS DEL GANADERO
        if (sesion == "iniciado"){
            lista_de_establos = new ArrayList<ArrayList<String>>();
            lista.setAdapter(new AdaptadorEstablos(HomeActivity.this, "Establo", lista_de_establos));
            LeerGanadero(usuario,sesion);
        }
    }


    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

}

