package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class registerPage extends AppCompatActivity {
    EditText txtnama_daftar, txtemail_daftar, txtusername_daftar, txtpassword_daftar;
    Button buttonDaftar;
    private ProgressDialog progressDialog;
    URLServer serv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        txtnama_daftar = (EditText) findViewById(R.id.rg_nama);
        txtemail_daftar = (EditText) findViewById(R.id.rg_email);
        txtusername_daftar = (EditText) findViewById(R.id.rg_username);
        txtpassword_daftar = (EditText) findViewById(R.id.rg_password);
        buttonDaftar = (Button) findViewById(R.id.buttonDaftar);

        buttonDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = txtnama_daftar.getText().toString();
                String e = txtemail_daftar.getText().toString();
                String u = txtusername_daftar.getText().toString();
                String p = txtpassword_daftar.getText().toString();

                if (!n.equals("") && !e.equals("") && !u.equals("") && !p.equals("")) {
                    daftar(n, e, u, p);
                } else {
                    Toast.makeText(registerPage.this, "Lengkapi Data Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void daftar(String n, String e, String u, String p) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        AndroidNetworking.post("https://silacak.pt-ckit.com/daftar.php")
                .addBodyParameter("nama", n)
                .addBodyParameter("email", e)
                .addBodyParameter("username", u)
                .addBodyParameter("password", p)
                .addBodyParameter("action", "daftar")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("pesan").equals("sukses")){
                                Toast.makeText(getApplicationContext(), "Berhasil Melakukan Pendaftaran", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Melakukan Pendaftaran (Email/Username sudah dipakai)", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(registerPage.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(registerPage.this, "Error Time Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}