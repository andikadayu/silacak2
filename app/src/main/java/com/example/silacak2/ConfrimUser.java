package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfrimUser extends AppCompatActivity {
    String iduserBaru, namauserBaru, emailuserBaru;
    EditText txtidBaru, txtnamaBaru, txtemailBaru;
    Button btnUpdate, btnBlokir;
    URLServer serv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confrim_user);

        serv = new URLServer();

        txtidBaru = findViewById(R.id.cfrm_idBaru);
        txtnamaBaru = findViewById(R.id.cfrm_namaBaru);
        txtemailBaru = findViewById(R.id.cfrm_emailBaru);

        Intent data = getIntent();
        iduserBaru = data.getStringExtra("id_user");
        namauserBaru = data.getStringExtra("nama_user");
        emailuserBaru = data.getStringExtra("email_user");

        txtidBaru.setText(iduserBaru);
        txtnamaBaru.setText(namauserBaru);
        txtemailBaru.setText(emailuserBaru);

        txtidBaru.setEnabled(false);

        btnUpdate = (Button) findViewById(R.id.buttonConfirmBaru);
        btnBlokir = (Button) findViewById(R.id.buttonBukaBlokir);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        btnBlokir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blokir();
            }
        });
    }

    public void confirm() {
        if (txtidBaru.getText().toString().equals("") || txtnamaBaru.getText().toString().equals("") || txtemailBaru.getText().toString().equals("")) {
            Toast.makeText(this, "Data Tidak Boleh Kosong", Toast.LENGTH_LONG).show();
        } else {
            AndroidNetworking.post(serv.getKonfirmUser())
                    .addBodyParameter("id_user", txtidBaru.getText().toString())
                    .addBodyParameter("action", "konfirmasi")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("pesan").equals("sukses")) {
                                    Toast.makeText(getApplicationContext(), "Akun Sudah Di Setujui", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), adminUserNew.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Akun Gagal Disetujui", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Galat" + e, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(getApplicationContext(), "Failed " + anError, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public void blokir() {
        AndroidNetworking.post(serv.getBlokirUserBaru())
                .addBodyParameter("id_user", txtidBaru.getText().toString())
                .addBodyParameter("action", "konfirmasi")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("pesan").equals("sukses")) {
                                Toast.makeText(getApplicationContext(), "Akun Sudah Di Buka Blokirnya", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), adminUserNew.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Akun Gagal Dibuka Blokirnya", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Galat" + e, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Failed " + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }
}