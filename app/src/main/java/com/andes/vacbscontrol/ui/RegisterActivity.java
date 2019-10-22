package com.andes.vacbscontrol.ui;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andes.vacbscontrol.R;
import com.andes.vacbscontrol.configuration.AppServices;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;

import com.android.volley.RequestQueue;
//import com.android.volley.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    /* HERRAMIENTAS PARA EL LAYOUT */

    private EditText et_nombre, et_apellidos;
    private EditText et_email, et_password, et_c_passwword;
    private Button btn_regist;
    private ProgressBar loading;

    /* SERVICIOS WEB */
    private static String URL_REGISTER = AppServices.URL_REGISTER;
    private static String URL_CONFIRM = AppServices.URL_CONFIRM;

    String email,password,cpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_nombre = findViewById(R.id.input_u_nombre);
        et_apellidos = findViewById(R.id.input_u_apellidos);
        et_email = findViewById(R.id.input_email);
        et_password = findViewById(R.id.input_password);
        et_c_passwword = findViewById(R.id.input_confirm_password);
        loading = findViewById(R.id.progressBar);

        btn_regist = findViewById(R.id.btn_regist);

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validaciones de formulario
                if (verificar_campos()){
                    Regist();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Complete todos los campos requeridos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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

    private boolean verificar_campos(){

        boolean estado = true;
        int errorColor = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        email = et_email.getText().toString();
        password = et_password.getText().toString();
        cpassword = et_c_passwword.getText().toString();

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


        if (cpassword.isEmpty()){
            estado = false;
            String errorString = "Ingrese su contraseña";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_c_passwword.setError(spannableStringBuilder);
        }

        if (!password.equals(cpassword)){
            estado = false;
            String errorString = "Sus contraseñas no coinciden";
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            et_c_passwword.setError(spannableStringBuilder);
        }

        return estado;
    }

    private void  Regist(){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);

        final String nombres = this.et_nombre.getText().toString().trim();
        final String apellidos = this.et_apellidos.getText().toString().trim();
        final String email = this.et_email.getText().toString().trim();
        final String password = this.et_password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText( RegisterActivity.this, "Email registrado!", Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);
                                /*Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);*/

                                // CONFIRMAR CORREO
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
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
                                            Toast.makeText( RegisterActivity.this, "No email confirmado!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,"Registro fallido!" + e.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(RegisterActivity.this,"Registro fallido! aqui2" + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(RegisterActivity.this,"Conexión fallida!" + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("nombres",nombres);
                params.put("apellidos",apellidos);
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
                                Toast.makeText( RegisterActivity.this, "Email Confirmado!", Toast.LENGTH_SHORT).show();
                                /*Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);*/
                                dialog.cancel();
                                onBackPressed();
                                finish();
                            }
                            if (success.equals("0")){
                                String message = jsonObject.getString("message");
                                Toast.makeText( RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,"Confirmación fallida!" + e.toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(RegisterActivity.this,"Registro fallido! aqui2" + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,"Conexión fallida!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(RegisterActivity.this,"Conexión fallida!" + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);

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
}