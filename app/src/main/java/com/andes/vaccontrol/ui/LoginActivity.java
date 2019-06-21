package com.andes.vaccontrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vaccontrol.R;
import com.andes.vaccontrol.SessionManager;
import com.andes.vaccontrol.adapter.AdaptadorEstablos;
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
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {




    /* HERRAMIENTAS PARA EL LAYOUT */
    private EditText et_email, et_password;
    private Button btn_login;
    private ProgressBar loading;
    private TextView go_register;

    /* SERVICIOS WEB */
    private static String URL_LOGIN = AppServices.URL_LOGIN;
    private static String URL_CONFIRM = AppServices.URL_CONFIRM;

    String email, password;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        et_email = findViewById(R.id.input_l_email);
        et_password = findViewById(R.id.input_l_password);
        loading = findViewById(R.id.progressBar_l);
        btn_login = findViewById(R.id.btn_login);
        go_register = findViewById(R.id.link_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificar_campos()){
                    login();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Complete todos los campos requeridos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        go_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrar = new Intent(view.getContext(), RegisterActivity.class);
                startActivity(registrar);
            }
        });

    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private boolean verificar_campos(){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        email = et_email.getText().toString();
        password = et_password.getText().toString();

        if (email.isEmpty()){
            estado = false;
            //mensaje_error = mensaje_error + "* El ganado necesita tener nombre"+"\n";
            //Toast.makeText(CrearGanadoActivity.this, "El ganado necesita tener nombre", Toast.LENGTH_SHORT).show();
            String errorString = "Ingrese su email";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_email.setError(spannableStringBuilder);
        }else if(!validarEmail(email)){
            estado = false;
            String errorString = "Ingrese un correo válido";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_email.setError(spannableStringBuilder);
        }


        if (password.isEmpty()){
            estado = false;
            String errorString = "Ingrese su contraseña";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_password.setError(spannableStringBuilder);
        }

        return estado;
    }

    private void  login(){
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        final String email = this.et_email.getText().toString().trim();
        final String password = this.et_password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){

                                JSONArray message = jsonObject.getJSONArray("message");
                                if(message.length() == 0) {
                                    loading.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);
                                    String msm = jsonObject.getString("message");
                                    Toast.makeText(LoginActivity.this,""+msm, Toast.LENGTH_SHORT).show();
                                } else {
                                    loading.setVisibility(View.GONE);
                                    btn_login.setVisibility(View.VISIBLE);

                                    String usuario_id = message.getJSONObject(0).getString("usuario_id");
                                    String usuario = message.getJSONObject(0).getString("usuario");
                                    String contrasenia = message.getJSONObject(0).getString("contrasenia");
                                    String nombres = message.getJSONObject(0).getString("nombres");
                                    String apellidos = message.getJSONObject(0).getString("apellidos");
                                    String correo = message.getJSONObject(0).getString("correo");
                                    String perfil = message.getJSONObject(0).getString("perfil");
                                    String estado = message.getJSONObject(0).getString("estado");
                                    String cod_conf = message.getJSONObject(0).getString("cod_conf");

                                    if (estado.equals("t")){
                                        Toast.makeText(LoginActivity.this,"Ingreso Correcto", Toast.LENGTH_SHORT).show();
                                        String sesion = "iniciado";

                                        sessionManager.createSession(nombres,apellidos,usuario,usuario_id);

                                        Intent mi_usuario = new Intent(LoginActivity.this, HomeActivity.class);
                                        mi_usuario.putExtra("usuario",usuario);
                                        mi_usuario.putExtra("session",sesion);
                                        mi_usuario.putExtra("nombres",nombres);
                                        mi_usuario.putExtra("apellidos",apellidos);
                                        //setResult(RESULT_OK, mi_usuario);
                                        startActivity(mi_usuario);
                                        finish();

                                    } else {
                                        Toast.makeText(LoginActivity.this,"Correo sin confirmar", Toast.LENGTH_SHORT).show();

                                        // CONFIRMAR CORREO
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                                        View mView = getLayoutInflater().inflate(R.layout.confirm_email, null);
                                        final TextView mMensaje = (TextView) mView.findViewById(R.id.tv_confirm_email);
                                        mMensaje.setText("Ingresé el código de confirmación enviado a su email ó el código \"123456\" ");
                                        final EditText mCodigo = (EditText) mView.findViewById(R.id.tv_confirm_email_code);
                                        Button mConfirm = (Button) mView.findViewById(R.id.btn_confirm_email);

                                        mBuilder.setView(mView);
                                        final AlertDialog dialog = mBuilder.create();
                                        dialog.show();

                                        mConfirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // Confirmar codigo
                                                if (verificar_texto_vacio(mCodigo)){
                                                    String codigo = mCodigo.getText().toString();
                                                    ConfirmEmail(email, password, codigo, dialog);
                                                } else {
                                                    Toast.makeText( LoginActivity.this, "No email confirmado!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,"Ingreso fallido!" + e.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(RegisterActivity.this,"Registro fallido! aqui2" + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(RegisterActivity.this,"Conexión fallida!" + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void  ConfirmEmail(String email_conf,String pass_conf,String code_conf, AlertDialog dialogx){

        final String my_email = email_conf.trim();
        final String my_pass = pass_conf.trim();
        final String my_code = code_conf.trim();
        final AlertDialog dialog = dialogx;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CONFIRM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText( LoginActivity.this, "Email Confirmado, ahora puedes logearte!", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this,"Confirmación fallida!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",my_email);
                params.put("password",my_pass);
                params.put("code",my_code);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean verificar_texto_vacio(EditText campo){
        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        String text = campo.getText().toString();

        if (text.isEmpty()){
            estado = false;
            String errorString = "Complete el siguiente campo";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            campo.setError(spannableStringBuilder);
        } else {
            if (text.length() != 6){
                estado = false;
                String errorString = "El código debe tener 6 dígitos ";
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                campo.setError(spannableStringBuilder);
            }
        }
        return estado;
    }

}
