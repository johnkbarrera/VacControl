package com.andes.vaccontrol.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GanadoActivity extends AppCompatActivity {

    /* HERRAMIENTAS PARA EL LAYOUT */
    TextView ganado_title, ganado_nom, ganado_registro, ganado_raza, ganado_procedencia;
    TextView ganado_dob, ganado_peso_dob, ganado_rpm, ganado_vmadre, ganado_rpg, ganado_vpadre;

    TextView ganado_c_somaticas, ganado_prof_ubre, ganado_prof_corporal, ganado_fecha_monitoreo;

    TextView ganado_reproduccion, ganado_estado_actual, ganado_peso_actual, ganado_fecha_celo;

    ImageButton fab_detalle,fab_monitoreo,fab_reproduccion;
    LinearLayout lay_detalle,lay_monitoreo,lay_reproduccion;

    private FloatingActionButton fab_main, fab1_mail, fab2_share, fab3_share;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_add_1, textview_add_2, textview_add_3;

    Boolean isOpen = false;
    Boolean isOpenDetalle = true;
    Boolean isOpenMonitoreo = true;
    Boolean isOpenReproduccion = true;

    /* LAYOUt REPRODUCCION*/
    private static final String TAG = "GanadoActivity";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static String URL_ADD_ESTADO_REPRODUCCION = AppServices.URL_ADD_ESTADO_REPRODUCCION;

    int ganado_estadovaca_pos, ganado_estado_pos;

    private static String URL_ADD_PRODUCCION = AppServices.URL_ADD_PRODUCCION;

    /* SERVICIOS WEB */
    private static String URL_VER_GANADOS = AppServices.URL_VER_GANADO;
    private static String URL_VER_GANADO_REPRODUCCION = AppServices.URL_VER_GANADO_REPRODUCCION;
    private static String URL_VER_GANADO_MONITORERO = AppServices.URL_VER_GANADO_MONITORERO;
    private static String URL_VER_GANADO_PRODUCCION = AppServices.URL_VER_GANADO_PRODUCCION;
    private static String URL_GANADO_SACA = AppServices.URL_GANADO_SACA;



    /* VARIABLES */
    String ganado_id;
    String sesion;

    ListView producciones_lista;
    LinearLayout lista_vacia;
    ArrayList<ArrayList<String>> lista_de_produccion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganado);
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

        ganado_c_somaticas = findViewById(R.id.tv_gd_csomatica);
        ganado_prof_ubre = findViewById(R.id.tv_gd_profubre);
        ganado_prof_corporal = findViewById(R.id.tv_gd_profcorp);
        ganado_fecha_monitoreo = findViewById(R.id.tv_gd_monitoreo_fecha);

        ganado_reproduccion = findViewById(R.id.tv_gd_preñada);
        ganado_estado_actual = findViewById(R.id.tv_gd_estado_vaca);
        ganado_peso_actual = findViewById(R.id.tv_gd_peso);
        ganado_fecha_celo = findViewById(R.id.tv_gd_fecha_celo);

        lista_vacia = findViewById(R.id.empty_list_gd_prod);
        producciones_lista = (ListView) findViewById(R.id.lista_vaca_produccion);

        producciones_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent mi_produccion = new Intent(view.getContext(), GanadoActivity.class);
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

        fab_monitoreo = findViewById(R.id.fab_monitoreo_ganado);
        lay_monitoreo = findViewById(R.id.lay_monitoreo_ganado);

        fab_monitoreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenMonitoreo) {
                    lay_monitoreo.setVisibility(View.VISIBLE);
                    //lay_detalle.startAnimation(fab_open);
                    //fab_detalle.startAnimation(fab_anticlock);
                    fab_monitoreo.setRotation(0);
                    isOpenMonitoreo = true;
                } else {
                    lay_monitoreo.setVisibility(View.GONE);
                    //lay_detalle.startAnimation(fab_close);
                    //fab_detalle.startAnimation(fab_clock);
                    fab_monitoreo.setRotation(180);
                    isOpenMonitoreo = false;
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


        fab_main = findViewById(R.id.fab);
        fab1_mail = findViewById(R.id.fab1);
        fab2_share = findViewById(R.id.fab2);
        fab3_share = findViewById(R.id.fab3);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_add_1 = (TextView) findViewById(R.id.tv_g_add_1);
        textview_add_2 = (TextView) findViewById(R.id.tv_g_add_2);
        textview_add_3 = (TextView) findViewById(R.id.tv_g_add_3);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {
                    textview_add_1.setVisibility(View.INVISIBLE);
                    textview_add_2.setVisibility(View.INVISIBLE);
                    textview_add_3.setVisibility(View.INVISIBLE);
                    fab3_share.startAnimation(fab_close);
                    fab2_share.startAnimation(fab_close);
                    fab1_mail.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    fab3_share.setClickable(false);
                    fab2_share.setClickable(false);
                    fab1_mail.setClickable(false);
                    isOpen = false;
                } else {
                    textview_add_1.setVisibility(View.VISIBLE);
                    textview_add_2.setVisibility(View.VISIBLE);
                    textview_add_3.setVisibility(View.VISIBLE);
                    fab3_share.startAnimation(fab_open);
                    fab2_share.startAnimation(fab_open);
                    fab1_mail.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab3_share.setClickable(true);
                    fab2_share.setClickable(true);
                    fab1_mail.setClickable(true);
                    isOpen = true;
                }

            }
        });


        fab3_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textview_add_1.setVisibility(View.INVISIBLE);
                textview_add_2.setVisibility(View.INVISIBLE);
                textview_add_3.setVisibility(View.INVISIBLE);
                fab3_share.startAnimation(fab_close);
                fab2_share.startAnimation(fab_close);
                fab1_mail.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab3_share.setClickable(false);
                fab2_share.setClickable(false);
                fab1_mail.setClickable(false);
                isOpen = false;
                RegistrarProduccion();
            }
        });

        fab2_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {textview_add_1.setVisibility(View.INVISIBLE);
                textview_add_2.setVisibility(View.INVISIBLE);
                textview_add_3.setVisibility(View.INVISIBLE);
                fab3_share.startAnimation(fab_close);
                fab2_share.startAnimation(fab_close);
                fab1_mail.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab3_share.setClickable(false);
                fab2_share.setClickable(false);
                fab1_mail.setClickable(false);
                isOpen = false;
                Intent nueva_sesion_fotos = new Intent(view.getContext(), RegistrarFotosActivity.class);
                nueva_sesion_fotos.putExtra("ganado_id",ganado_id);
                nueva_sesion_fotos.putExtra("session",sesion);
                startActivity(nueva_sesion_fotos);

            }
        });

        fab1_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview_add_1.setVisibility(View.INVISIBLE);
                textview_add_2.setVisibility(View.INVISIBLE);
                textview_add_3.setVisibility(View.INVISIBLE);
                fab3_share.startAnimation(fab_close);
                fab2_share.startAnimation(fab_close);
                fab1_mail.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab3_share.setClickable(false);
                fab2_share.setClickable(false);
                fab1_mail.setClickable(false);
                isOpen = false;
                RegistrarEstadoReproduccion();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();

        // CARGAMOS Y VISUALIZAMOS DATOS DEL GANADERO
        ver_reproduccion();
        ver_monitoreo();

        lista_de_produccion = new ArrayList<ArrayList<String>>();
        producciones_lista.setAdapter(new AdaptadorProduccion(GanadoActivity.this, "Establo", lista_de_produccion));
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
                                    String celuas_somaticas = message.getJSONObject(0).getString("c_somaticas");
                                    String ubre_prof = message.getJSONObject(0).getString("prof_ubre");
                                    String corp_prof = message.getJSONObject(0).getString("prof_corp");
                                    String fecha_examen = message.getJSONObject(0).getString("fecha");

                                    //if (ceulas_somaticas.isEmpty()){}

                                    ganado_c_somaticas.setText(celuas_somaticas);
                                    ganado_prof_ubre.setText(ubre_prof);
                                    ganado_prof_corporal.setText(corp_prof);
                                    ganado_fecha_monitoreo.setText(fecha_examen);
                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
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

                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_ganado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh_vaca) {
            Toast.makeText(GanadoActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();
            lista_de_produccion = new ArrayList<ArrayList<String>>();
            producciones_lista.setAdapter(new AdaptadorProduccion(GanadoActivity.this, "Establo", lista_de_produccion));
            LeerProduccionGanado(ganado_id,sesion);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gd_atras) {

            Toast.makeText(GanadoActivity.this,"Retornando a mi Establo!", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saca) {

            RegistrarSaca();
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

                                    fab_main.setVisibility(View.GONE);
                                }

                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");

                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(GanadoActivity.this,"No registras producción!", Toast.LENGTH_SHORT).show();
                                } else {
                                    lista_vacia.setVisibility(View.GONE);

                                    //Toast.makeText(GanadoActivity.this,""+message.length(), Toast.LENGTH_SHORT).show();

                                    for (int i = 0; i < message.length(); i++) {
                                        String produccion_id = message.getJSONObject(i).getString("produccion_id");
                                        String litro_en_prod = message.getJSONObject(i).getString("litros_leche");
                                        String solidos_en_prod = message.getJSONObject(i).getString("solidos");
                                        String estado_en_prod = message.getJSONObject(i).getString("estado_prod");
                                        String fecha_de_prod = message.getJSONObject(i).getString("fecha");
                                        String peso_en_prod = message.getJSONObject(i).getString("peso");

                                        ArrayList<String> ar = new ArrayList<String>();
                                        ar.add(produccion_id);
                                        ar.add(litro_en_prod);
                                        ar.add(solidos_en_prod);
                                        ar.add(estado_en_prod);
                                        ar.add(fecha_de_prod);
                                        ar.add(peso_en_prod);
                                        lista_de_produccion.add(ar);
                                    }

                                    producciones_lista.setAdapter(new AdaptadorProduccion(GanadoActivity.this, "Extracción", lista_de_produccion));

                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
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


    /* REPRODUCCION*/

    private  void RegistrarEstadoReproduccion(){

        // LAYOUT
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GanadoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_registrar_reproduccion, null);



        final ArrayAdapter<String> adapter_estado;
        final ArrayAdapter<String> adapter_estadovaca;

        final Spinner spn_lista_estado = (Spinner) mView.findViewById(R.id.spinner_gp_estado);
        final Spinner spn_lista_estadovaca = (Spinner) mView.findViewById(R.id.spinner_gp_estadovaca);
        final TextView et_fecha = (TextView) mView.findViewById(R.id.text_gd_datecelo);
        final ImageView btn_get_fechaparto = (ImageView) mView.findViewById(R.id.btn_gd_date);
        final EditText et_peso = (EditText) mView.findViewById(R.id.input_gd_peso);
        final Button btn_add_parto = (Button) mView.findViewById(R.id.btn_confirm_parto);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        adapter_estado = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_estado_en_produccion));
        spn_lista_estado.setAdapter(adapter_estado);
        spn_lista_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ganado_estado_pos = position;

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ganado_estado_pos = 0;
            }
        });

        adapter_estadovaca = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.array_estado_vacas));
        spn_lista_estadovaca.setAdapter(adapter_estadovaca);
        spn_lista_estadovaca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ganado_estadovaca_pos = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ganado_estadovaca_pos = 0;
            }
        });


        btn_get_fechaparto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        GanadoActivity.this,
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

        btn_add_parto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar codigo
                if (verificar_campos(et_fecha,et_peso)){

                    String gr_estado = adapter_estado.getItem(ganado_estado_pos).trim();
                    String gr_estado_vaca = adapter_estadovaca.getItem(ganado_estado_pos).trim();
                    final String gr_fecha_celo = et_fecha.getText().toString().trim();
                    final String gr_peso = et_peso.getText().toString().trim();

                    add_estado_reproduccion(sesion, ganado_id, gr_estado_vaca, gr_estado, gr_fecha_celo,gr_peso, dialog);
                } else {
                    Toast.makeText( GanadoActivity.this, "Complete los campos requeridos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void add_estado_reproduccion(String sesion_est, String ganado_id, String estadovaca, String estado_b, String fecha, String peso, AlertDialog dialogx){

        final String sesion = sesion_est.trim();
        final String ganado_identificador = ganado_id.trim();
        final String estado_vaca = estadovaca.trim();
        final String estado_bool = estado_b.trim();
        final String ganado_fecha = fecha.trim();
        final String ganado_peso = peso.trim();

        final AlertDialog dialog = dialogx;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_ESTADO_REPRODUCCION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                ver_reproduccion();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",sesion);
                params.put("ganado_id",ganado_identificador);
                params.put("gr_estado_vaca",estado_vaca);
                params.put("gr_estado",estado_bool);
                params.put("gr_fecha_celo",ganado_fecha);
                params.put("gr_peso",ganado_peso);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private boolean verificar_campos(TextView fecha, EditText peso){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        String fecha_txt = fecha.getText().toString();
        String peso_txt = peso.getText().toString();

        if (fecha_txt.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "El ganado necesita tener nombre";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            fecha.setError(spannableStringBuilder);

        }
        if (peso_txt.isEmpty()){
            estado = false;
            String errorString = "Registre la fecha de ultimo celo";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            peso.setError(spannableStringBuilder);
        }

        return estado;
    }

    /* PRODUCCION*/

    private  void RegistrarProduccion(){

        // LAYOUT
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GanadoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_registrar_produccion, null);


        final EditText et_litros = (EditText) mView.findViewById(R.id.input_gpr_litros);
        final EditText et_peso = (EditText) mView.findViewById(R.id.input_gpr_peso);
        final ImageView btn_get_fechaproducion = (ImageView) mView.findViewById(R.id.btn_grp_date);
        final EditText et_fecha = (EditText) mView.findViewById(R.id.input_gpr_fecha);
        final Button btn_add_produccion = (Button) mView.findViewById(R.id.btn_confirm_produccion);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();



        btn_get_fechaproducion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        GanadoActivity.this,
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

        btn_add_produccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar codigo
                if (verificar_campos_produccion(et_fecha,et_peso, et_litros)){

                    final String gpr_fecha_prod = et_fecha.getText().toString().trim();
                    final String gpr_peso = et_peso.getText().toString().trim();
                    final String gpr_litro = et_litros.getText().toString().trim();

                    add_estado_produccion(sesion, ganado_id, gpr_litro, gpr_peso, gpr_fecha_prod, dialog);
                } else {
                    Toast.makeText( GanadoActivity.this, "Complete los campos requeridos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verificar_campos_produccion(TextView fecha, EditText peso, EditText litros){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        String fecha_txt = fecha.getText().toString();
        String peso_txt = peso.getText().toString();
        String litros_txt = litros.getText().toString();

        if (fecha_txt.isEmpty()){
            estado = false;
            String errorString = "Ingrese una fecha";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            fecha.setError(spannableStringBuilder);

        }
        if (peso_txt.isEmpty()){
            estado = false;
            String errorString = "Ingrese el peso";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            peso.setError(spannableStringBuilder);
        }
        if (litros_txt.isEmpty()){
            estado = false;
            String errorString = "Ingrese la producción en litro";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            litros.setError(spannableStringBuilder);
        }

        return estado;
    }

    private void add_estado_produccion(String sesion_est, String ganado_id, String litros_prod, String peso_prod, String fecha_prod, AlertDialog dialogx){

        final String sesion = sesion_est.trim();
        final String ganado_identificador = ganado_id.trim();
        final String produccion_litros = litros_prod.trim();
        final String produccion_peso = peso_prod.trim();
        final String produccion_fecha = fecha_prod.trim();

        final AlertDialog dialog = dialogx;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_PRODUCCION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                                dialog.cancel();


                                lista_de_produccion = new ArrayList<ArrayList<String>>();
                                producciones_lista.setAdapter(new AdaptadorProduccion(GanadoActivity.this, "Establo", lista_de_produccion));
                                LeerProduccionGanado(ganado_identificador,sesion);
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",sesion);
                params.put("ganado_id",ganado_identificador);
                params.put("gpr_litros",produccion_litros);
                params.put("gpr_fecha",produccion_fecha);
                params.put("gpr_peso",produccion_peso);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }



    /* SACA */

    private  void RegistrarSaca(){

        // LAYOUT
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GanadoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_saca_ganado, null);


        final EditText et_motivo = (EditText) mView.findViewById(R.id.input_gp_saca_motivo);
        final Button btn_saca_ganado = (Button) mView.findViewById(R.id.btn_confirm_saca);

        mBuilder.setView(mView);
        final AlertDialog dialog_delete = mBuilder.create();
        dialog_delete.show();

        btn_saca_ganado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Confirmar codigo
                if (verificar_campos_saca(et_motivo)){

                    final String gpr_motivo_saca = et_motivo.getText().toString().trim();
                    final String gpr_saca_fecha  = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    AlertDialog alertDialog = new AlertDialog.Builder(GanadoActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cuidado!")
                            .setMessage("Está seguro que desea eliminar el registro?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Eliminar_Ganado(sesion, ganado_id, gpr_motivo_saca, gpr_saca_fecha, dialog_delete);
                                    //finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog_delete.cancel();
                                    Toast.makeText(getApplicationContext(),"Proceso cancelado",Toast.LENGTH_LONG).show();
                                }
                            })
                            .show();

                } else {
                    Toast.makeText( GanadoActivity.this, "Complete los campos requeridos!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verificar_campos_saca(TextView motivo){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        String motivo_txt = motivo.getText().toString();

        if (motivo_txt.isEmpty()){
            estado = false;
            String errorString = "Ingrese el motivo de saca";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            motivo.setError(spannableStringBuilder);

        }
        return estado;
    }

    private void Eliminar_Ganado(String sesion_est, String ganado_id, String motivo, String fecha, AlertDialog dialogx){

        final String sesion = sesion_est.trim();
        final String ganado_identificador = ganado_id.trim();
        final String saca_motivo = motivo.trim();
        final String saca_fecha = fecha.trim();

        final AlertDialog dialog = dialogx;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GANADO_SACA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                LeerGanado(ganado_identificador, sesion);
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( GanadoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(GanadoActivity.this,"Error obteniendo los Datos!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GanadoActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("sesion",sesion);
                params.put("ganado_id",ganado_identificador);
                params.put("saca_motivo",saca_motivo);
                params.put("saca_fecha",saca_fecha);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}
