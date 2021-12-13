package com.example.silacak2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    EditText txtUsername, txtPassword, txtOTP;
    Button buttonLogin, buttonOTP;
    TextView tvLink, tvLinks, buttonRegister;
    ProgressBar progBar;
    URLServer serv;
    String api_keys = "wco5wkEiVpZumrSru50vZ1imk6knrgMh";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUsername = (EditText) findViewById(R.id.textUsername);
        txtPassword = (EditText) findViewById(R.id.textPassword);
        txtOTP = (EditText) findViewById(R.id.textOTP);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (TextView) findViewById(R.id.buttonRegister);
        buttonOTP = (Button) findViewById(R.id.btnSendOTP);
        progBar = (ProgressBar) findViewById(R.id.progBar);
        serv = new URLServer();
        progBar.setVisibility(View.GONE);
        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserDetail().get(SessionManager.ROLE);
            if (role.equalsIgnoreCase("admin")) {
                Intent intent = new Intent(MainActivity.this, adminPageNew.class);
                startActivity(intent);
                finish();
            } else if (role.equalsIgnoreCase("anggota")) {
                Intent intent = new Intent(MainActivity.this, anggotaPage.class);
                startActivity(intent);
                finish();
            }else if (role.equalsIgnoreCase("user")) {
                Intent intent = new Intent(MainActivity.this, userPage.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, notConfirmPage.class);
                startActivity(intent);
                finish();
            }
        }
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, registerPage.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = txtUsername.getText().toString();
                String pass = txtPassword.getText().toString();
                String otp = txtOTP.getText().toString();

                if (!user.equals("") && !pass.equals("") && !otp.equals("")) {
                    progBar.setVisibility(View.VISIBLE);
                    loginAction(user, pass,otp);
                } else {
                    Toast.makeText(MainActivity.this, "Complete the Form", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user = txtUsername.getText().toString();
                if (!user.equals("")){
                    sendtoOTP(user);
                }else{
                    Toast.makeText(MainActivity.this, "Masukkan Username Dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Intent in = new Intent(getApplicationContext(),detail_feature.class);
            startActivity(in);
            finish();
        } else {
            return;
        }
    }
    private void loginAction(String user, String pass,String otp) {
        AndroidNetworking.post(serv.getLogin())
                .addBodyParameter("username", user)
                .addBodyParameter("password", pass)
                .addBodyParameter("otp",otp)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //boolean status = response.getBoolean("status");

                            if (response.getString("pesan").equals("sukses")) {
                                JSONArray ja = response.getJSONArray("data");
                                LoginData log = new LoginData();
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    log.setId_user(jo.getString("id_user"));
                                    log.setRole(jo.getString("role"));
                                    log.setNama(jo.getString("nama"));
                                    log.setId_gps(jo.getString("id_lokasi"));
                                }
                                sessionManager.createLoginSession(log);

                                String role = sessionManager.getUserDetail().get(SessionManager.ROLE);
                                Constants.setIdUser(sessionManager.getUserDetail().get(SessionManager.ID_USER));
                                Constants.setNamaUser(sessionManager.getUserDetail().get(SessionManager.NAMA));
                                Constants.setRoleUser(sessionManager.getUserDetail().get(SessionManager.ROLE));
                                if (role.equalsIgnoreCase("admin")) {
//                                    Constants.setIdGps("0");
                                    Intent intent = new Intent(MainActivity.this, adminPageNew.class);
                                    startActivity(intent);
                                    finish();
                                } else if (role.equalsIgnoreCase("anggota")) {
                                    Intent intent = new Intent(MainActivity.this, anggotaPage.class);
                                    startActivity(intent);
                                    finish();
                                } else if (role.equalsIgnoreCase("user")) {
                                    Intent intent = new Intent(MainActivity.this, userPage.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(MainActivity.this, notConfirmPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else if(response.getString("pesan").equals("salah")) {
                                Toast.makeText(MainActivity.this, "Username/Password Salah", Toast.LENGTH_SHORT).show();
                                progBar.setVisibility(View.GONE);
                            } else if(response.getString("pesan").equals("blokir")){
                                Toast.makeText(MainActivity.this, "Maaf, Akun Anda Terblokir, Silahkan Hubungi Admin", Toast.LENGTH_SHORT).show();
                                progBar.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
                            progBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error Time Out", Toast.LENGTH_SHORT).show();
                        progBar.setVisibility(View.GONE);
                    }
                });
    }

    private void sendtoOTP(String user){
        AndroidNetworking.post(serv.sendOTP())
                .addBodyParameter("username",user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String msg = response.getString("msg");

                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error Time Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}