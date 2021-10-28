package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfrimAnggota extends AppCompatActivity {
    String iduser, namauser, emailuser;
    EditText txtid, txtnama, txtemail;
    Button btnUpdate, btnBlokirUser;
    URLServer serv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrim_anggota);

        serv = new URLServer();

        txtid = findViewById(R.id.cfrm_id);
        txtnama = findViewById(R.id.cfrm_nama);
        txtemail = findViewById(R.id.cfrm_email);

        Intent data = getIntent();
        iduser = data.getStringExtra("id_user");
        namauser = data.getStringExtra("nama_user");
        emailuser = data.getStringExtra("email_user");

        txtid.setText(iduser);
        txtnama.setText(namauser);
        txtemail.setText(emailuser);

        txtid.setEnabled(false);

        btnUpdate = (Button) findViewById(R.id.buttonConfirm);
        btnBlokirUser = (Button) findViewById(R.id.buttonBukaBlokirUser);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        btnBlokirUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bukaBlokir();
            }
        });
    }

    public void confirm(){
        if (txtid.getText().toString().equals("") || txtnama.getText().toString().equals("") || txtemail.getText().toString().equals("")) {
            Toast.makeText(this, "Data Tidak Boleh Kosong", Toast.LENGTH_LONG).show();
        } else {
            AndroidNetworking.post(serv.getKonfirmAnggota())
                    .addBodyParameter("id_user", txtid.getText().toString())
                    .addBodyParameter("nama", txtnama.getText().toString())
                    .addBodyParameter("email", txtemail.getText().toString())
                    .addBodyParameter("action", "konfirmasi")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("pesan").equals("sukses")){
                                    Toast.makeText(getApplicationContext(), "Data Berhasil Diubah", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), adminListUser.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Data Gagal Diubah", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e){
                                Toast.makeText(getApplicationContext(), "Data Gagal Diubah" + e, Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Failed " + anError, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public void bukaBlokir(){
        AndroidNetworking.post(serv.getBlokirUserBaru())
                .addBodyParameter("id_user", txtid.getText().toString())
                .addBodyParameter("action", "konfirmasi")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("pesan").equals("sukses")){
                                Toast.makeText(getApplicationContext(), "Data Berhasil Diubah", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), adminListUser.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Data Gagal Diubah", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
                            Toast.makeText(getApplicationContext(), "Data Gagal Diubah" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Failed " + anError, Toast.LENGTH_LONG).show();
                    }
                });

    }
}