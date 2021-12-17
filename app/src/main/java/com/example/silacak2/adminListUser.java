package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
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
import com.example.silacak2.adapter.adapterUser;
import com.example.silacak2.model.dataUserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class adminListUser extends AppCompatActivity {
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    ArrayList<String> array_nama, array_email;
    ListView listView;
    SessionManager sessionManager;
    URLServer serv;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1000;

    private final static String STATUS = "Data User";
    public ArrayList<dataUserModel> dataUserModel;
    public RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_user);

        serv = new URLServer();

        getSupportActionBar().setTitle("Data User");

        recyclerView = findViewById(R.id.recy);
        recyclerView.setHasFixedSize(true);
        dataUserModel = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        layoutManager = new LinearLayoutManager(adminListUser.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new adapterUser(this, dataUserModel);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        bottomNavigationView.setSelectedItemId(R.id.listuser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.listuser:
                        return true;
//                    case R.id.profiles:
//                        startActivity(new Intent(adminListUser.this, profileAdmin.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        return true;
                    case R.id.homes:
                        startActivity(new Intent(adminListUser.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(adminListUser.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminListUser.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(adminListUser.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        sessionManager = new SessionManager(adminListUser.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

    }

    private void loadData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memuat Data...");
        progressDialog.show();

        AndroidNetworking.post(serv.getUserDataConfirm())
                .addBodyParameter("action", "tampil_user")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataUser");
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                dataUserModel item = new dataUserModel(
                                        data.getString("id_user"),
                                        data.getString("nama"),
                                        data.getString("email"),
                                        data.getString("status")
                                );

                                dataUserModel.add(item);
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
//        if(item.getItemId()==R.id.oListPerintah){
//            startActivity(new Intent(this,adminListPerintah.class));
//        }
        if(item.getItemId()==R.id.oLokasiUser){
            startActivity(new Intent(this,adminLokasiAll.class));
        }
        if(item.getItemId() == R.id.oScanQR){
            startActivity(new Intent(this, scan_qrcode.class));
        }
        if(item.getItemId() == R.id.oLaporan){
            startActivity(new Intent(this,adminListLaporan.class));
        }
        if(item.getItemId() == R.id.oAbsensi){
            startActivity(new Intent(this,AbsensiActivity.class));
        }
        if(item.getItemId() == R.id.oRekap){
            startActivity(new Intent(this,RekapAbsensi.class));
        }
        return true;
    }

    private void movetoLogin() {
        Intent i = new Intent(adminListUser.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

}