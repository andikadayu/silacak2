package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterPesanAdmin;
import com.example.silacak2.model.dataPesanModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class adminPesan extends AppCompatActivity {
    private final static String STATUS = "Data User";
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    public ArrayList<dataPesanModel> dataPesanModels;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    SessionManager sessionManager;
    URLServer serv;
    Handler handler = new Handler();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pesan);

        serv = new URLServer();

        getSupportActionBar().setTitle("Pesan");

        recyclerView = findViewById(R.id.recyPesan);
        recyclerView.setHasFixedSize(true);
        dataPesanModels = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        layoutManager = new LinearLayoutManager(adminPesan.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new adapterPesanAdmin(this, dataPesanModels);
        recyclerView.setAdapter(adapter);

        //Nav Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        bottomNavigationView.setSelectedItemId(R.id.pesan);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pesan:
                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(adminPesan.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(adminPesan.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminPesan.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homes:
                        startActivity(new Intent(adminPesan.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        BottomNavigationView bottomNavigationView2 = findViewById(R.id.adminNav2);
        bottomNavigationView2.setSelectedItemId(R.id.person);
        bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.person:
                        return true;
                    case R.id.groub:
                        startActivity(new Intent(adminPesan.this, adminPesanGroub.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(adminPesan.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_admin, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.oProfileAdmin) {
            startActivity(new Intent(this, profileAdmin.class));
        }
//        if (item.getItemId() == R.id.oListPerintah) {
//            startActivity(new Intent(this, adminListPerintah.class));
//        }
        if (item.getItemId() == R.id.oLokasiUser) {
            startActivity(new Intent(this, adminLokasiAll.class));
        }
        if(item.getItemId() == R.id.oScanQR){
            startActivity(new Intent(this, scan_qrcode.class));
        }
        if(item.getItemId() == R.id.oLaporan){
            startActivity(new Intent(this,adminListLaporan.class));
        }
        return true;
    }

    private void movetoLogin() {
        Intent i = new Intent(adminPesan.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memuat Data...");
        progressDialog.show();

        AndroidNetworking.post(serv.getAnggotaData())
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
                                dataPesanModel item = new dataPesanModel(
                                        data.getString("id_anggota"),
                                        data.getString("id_user"),
                                        data.getString("nama"),
                                        data.getString("email"),
                                        data.getString("foto")
                                );
                                dataPesanModels.add(item);
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
//                        anError.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Terjadi Kesalahan" + anError, Toast.LENGTH_LONG).show();
                    }
                });
    }
}