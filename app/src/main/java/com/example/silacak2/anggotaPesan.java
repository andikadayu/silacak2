package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterPesanAdmin;
import com.example.silacak2.adapter.adapterPesanAnggota;
import com.example.silacak2.adapter.adapterPesanAnggota2;
import com.example.silacak2.model.dataPesanModel;
import com.example.silacak2.model.dataPesanModelAnggota;
import com.example.silacak2.model.dataPesanModelAnggota2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class anggotaPesan extends AppCompatActivity {
    URLServer serv;
    public RecyclerView recyclerView;
    public RecyclerView recyclerView2;
    public ArrayList<dataPesanModelAnggota> dataPesanAnggotaModels;
    public ArrayList<dataPesanModelAnggota2> dataPesanAnggotaModels2;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager layoutManager2;
    public RecyclerView.Adapter adapter;
    public RecyclerView.Adapter adapter2;
    SessionManager sessionManager;
    LocationManager locationManager;
    private ProgressDialog progressDialog;
    String nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota_pesan);

        serv = new URLServer();

        getSupportActionBar().setTitle("Pesan");

        sessionManager = new SessionManager(anggotaPesan.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        recyclerView = findViewById(R.id.recyPesanAnggota);
        recyclerView2 = findViewById(R.id.recyPesanAnggota2);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        dataPesanAnggotaModels = new ArrayList<>();
        dataPesanAnggotaModels2 = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();
        loadData2();

        layoutManager = new LinearLayoutManager(anggotaPesan.this, RecyclerView.VERTICAL, false);
        layoutManager2 = new LinearLayoutManager(anggotaPesan.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView2.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new adapterPesanAnggota(this, dataPesanAnggotaModels);
        adapter2 = new adapterPesanAnggota2(this, dataPesanAnggotaModels2);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);


        BottomNavigationView bottomNavigationView = findViewById(R.id.userNav);
        bottomNavigationView.setSelectedItemId(R.id.pesanAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        startActivity(new Intent(anggotaPesan.this, profileAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        startActivity(new Intent(anggotaPesan.this, anggotaLokasi.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesAnggota:
                        startActivity(new Intent(anggotaPesan.this, anggotaPage.class));
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
        bottomNavigationView2.setSelectedItemId(R.id.personAnggota);
        bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.personAnggota:
                        return true;
                    case R.id.groubAnggota:
                        startActivity(new Intent(anggotaPesan.this, anggotaPesanGroub.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void movetoLogin() {
        Intent i = new Intent(anggotaPesan.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        AndroidNetworking.post(serv.getDataAnggota())
                .addBodyParameter("action", "tampil_anggota")
                .addBodyParameter("id_user", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataAnggota");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                dataPesanModelAnggota item = new dataPesanModelAnggota(
                                        data.getString("id_user"),
                                        data.getString("id_anggota"),
                                        data.getString("nama"),
                                        data.getString("email"),
                                        data.getString("foto")
                                );
                                dataPesanAnggotaModels.add(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan" + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadData2() {
        AndroidNetworking.post(serv.getDataAnggota2())
                .addBodyParameter("action", "tampil_anggota")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataAnggota");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                dataPesanModelAnggota2 item = new dataPesanModelAnggota2(
                                        data.getString("id_user"),
                                        data.getString("nama"),
                                        data.getString("foto")
                                );
                                dataPesanAnggotaModels2.add(item);
                                //notifyServer("notify");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        adapter2.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
//                        anError.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan" + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }
}