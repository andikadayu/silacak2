package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterPesanGroub;
import com.example.silacak2.adapter.adapterTampilPesanAdmin;
import com.example.silacak2.model.dataPesanGroubModelAdmin;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class anggotaPesanGroub extends AppCompatActivity {
    String idgroub, pesangroub;
    EditText edTulisPesan;
    LinearLayout lSend;
    URLServer serv;
    SessionManager sessionManager;
    private Activity activity;
    RecyclerView recyclerView;
    public ArrayList<dataPesanGroubModelAdmin> dataPesanGroubModelAdmins;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota_pesan_groub);

        sessionManager = new SessionManager(anggotaPesanGroub.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        serv = new URLServer();

        edTulisPesan = (EditText) findViewById(R.id.tulisPesan);
        lSend = (LinearLayout) findViewById(R.id.send);

        lSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesan();
                Intent intent = new Intent(anggotaPesanGroub.this, anggotaPesanGroub.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyPesanGroub);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataPesanGroubModelAdmins = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        adapter = new adapterPesanGroub(this, dataPesanGroubModelAdmins);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.userNav);
        bottomNavigationView.setSelectedItemId(R.id.pesanAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        startActivity(new Intent(anggotaPesanGroub.this, profileAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        startActivity(new Intent(anggotaPesanGroub.this, anggotaLokasi.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesAnggota:
                        startActivity(new Intent(anggotaPesanGroub.this, anggotaPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanAnggota:
                        return true;
                }
                return false;
            }
        });

        BottomNavigationView bottomNavigationView2 = findViewById(R.id.userNav2);
        bottomNavigationView2.setSelectedItemId(R.id.groubAnggota);
        bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.groubAnggota:
                        return true;
                    case R.id.personAnggota:
                        startActivity(new Intent(anggotaPesanGroub.this, anggotaPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void movetoLogin() {
        Intent i = new Intent(anggotaPesanGroub.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        AndroidNetworking.post("https://silacak.pt-ckit.com/getPesanGroub.php")
                .addBodyParameter("action", "tampil")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataPesan");
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                int id_pengirim = data.getInt("id_send");
                                dataPesanGroubModelAdmin item = new dataPesanGroubModelAdmin(
                                        data.getString("id_pesan_groub"),
                                        data.getString("nama"),
                                        data.getString("id_send"),
                                        data.getString("pesan"),
                                        data.getString("tanggal"),
                                        data.getString("jam"),
                                        id_pengirim == Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.ID_USER)) ? adapterTampilPesanAdmin.MESSAGE_TYPE_OUT : adapterTampilPesanAdmin.MESSAGE_TYPE_IN
                                );
                                dataPesanGroubModelAdmins.add(item);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan" + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void kirimPesan(){
        String pesan = edTulisPesan.getText().toString();
        AndroidNetworking.post("https://silacak.pt-ckit.com/kirimPesanGroub.php")
                .addBodyParameter("id_pengirim", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .addBodyParameter("pesan", pesan)
                .addBodyParameter("action", "pesan")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("pesan").equals("sukses")){
                                Toast.makeText(getApplicationContext(), "Pesan Terkirim", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Pesan Gagal Terkirim", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(anggotaPesanGroub.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(anggotaPesanGroub.this, "Error Time Out", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}