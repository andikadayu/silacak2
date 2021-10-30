package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterAnggota;
import com.example.silacak2.adapter.adapterUser;
import com.example.silacak2.model.dataAnggotaModel;
import com.example.silacak2.model.dataUserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class adminListAnggota extends AppCompatActivity {
    SessionManager sessionManager;
    URLServer serv;
    private final static String STATUS = "Data User";
    public ArrayList<dataAnggotaModel> dataAnggotaModels;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_anggota);

        serv = new URLServer();

        getSupportActionBar().setTitle("Data Anggota");

        recyclerView = findViewById(R.id.recyAnggota);
        recyclerView.setHasFixedSize(true);
        dataAnggotaModels = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        layoutManager = new LinearLayoutManager(adminListAnggota.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new adapterAnggota(this, dataAnggotaModels);
        recyclerView.setAdapter(adapter);

        //Nav Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        bottomNavigationView.setSelectedItemId(R.id.listanggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.listanggota:
                        return true;
//                    case R.id.profiles:
//                        startActivity(new Intent(adminListAnggota.this, profileAdmin.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(adminListAnggota.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homes:
                        startActivity(new Intent(adminListAnggota.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminListAnggota.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(adminListAnggota.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(adminListAnggota.this);
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
        if (item.getItemId()==R.id.oProfileAdmin){
            startActivity(new Intent(this, profileAdmin.class));
        }
        if(item.getItemId()==R.id.oListPerintah){
            startActivity(new Intent(this,adminListPerintah.class));
        }
        if(item.getItemId()==R.id.oLokasiUser){
            startActivity(new Intent(this,adminLokasiAll.class));
        }
        return true;
    }

    private void movetoLogin() {
        Intent i = new Intent(adminListAnggota.this, MainActivity.class);
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
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                dataAnggotaModel item = new dataAnggotaModel(
                                        data.getString("id_anggota"),
                                        data.getString("id_user"),
                                        data.getString("nama"),
                                        data.getString("email"),
                                        data.getString("status")
                                );

                                dataAnggotaModels.add(item);
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