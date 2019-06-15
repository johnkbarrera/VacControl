package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vaccontrol.R;
import com.andes.vaccontrol.adapter.AdaptadorEstablos;
import com.andes.vaccontrol.configuration.AppServices;

import java.util.ArrayList;

public class MiGanadoActivity extends AppCompatActivity {

    /* HERRAMIENTAS PARA EL LAYOUT */
    TextView ganado_title, ganado_nom, ganado_registro, ganado_raza, ganado_procedencia;

    private FloatingActionButton fab_main, fab1_mail, fab2_share, fab3_share;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_add_1, textview_add_2, textview_add_3;

    Boolean isOpen = false;

    /* SERVICIOS WEB */
    private static String URL_VER_GANADOS = AppServices.URL_VER_GANADO;

    /* VARIABLES */
    ArrayList<ArrayList<String>> lista_produccion;
    ArrayList<ArrayList<String>> lista_reproduccion;
    ArrayList<ArrayList<String>> lista_monitoreo;

    String ganado_id;
    String sesion;



    /*

    mi_ganado.putExtra("posicion","Ganado "+(position+1));
                mi_ganado.putExtra("ganado_id",lista_de_ganados.get(position).get(0));
                mi_ganado.putExtra("nombre",lista_de_ganados.get(position).get(1));


    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_ganado);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ganado_title = findViewById(R.id.text_g_numero);
        ganado_nom = findViewById(R.id.text_g_nombre);
        ganado_raza = findViewById(R.id.text_g_raza);
        ganado_procedencia = findViewById(R.id.text_g_procedencia);
        ganado_registro = findViewById(R.id.text_g_registro);

        Intent intent = getIntent();
        final Bundle ganado_data = intent.getExtras();

        ganado_id = ganado_data.getString("ganado_id");
        sesion = ganado_data.getString("session");

        ganado_title.setText(ganado_data.getString("posicion"));
        ganado_nom.setText(ganado_data.getString("nombre"));

        /* cargamos datos de la lista*/















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

                Toast.makeText(getApplicationContext(), "fab3 ", Toast.LENGTH_SHORT).show();

            }
        });

        fab2_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();

            }
        });

        fab1_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Email", Toast.LENGTH_SHORT).show();

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vacas, menu);
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
            Toast.makeText(MiGanadoActivity.this,"Refrescando Vista!", Toast.LENGTH_SHORT).show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_production) {

            Toast.makeText(MiGanadoActivity.this,"Salir!", Toast.LENGTH_SHORT).show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_session) {

            Toast.makeText(MiGanadoActivity.this,"Salir!", Toast.LENGTH_SHORT).show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_reproduction) {

            Toast.makeText(MiGanadoActivity.this,"Salir!", Toast.LENGTH_SHORT).show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_saca) {

            Toast.makeText(MiGanadoActivity.this,"Salir!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
