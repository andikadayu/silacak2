package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.silacak2.adapter.adapterLaporan;
import com.example.silacak2.model.dataLaporanModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class adminListLaporan extends AppCompatActivity {

    URLServer serv;
    SessionManager sessionManager;

    public ArrayList<dataLaporanModel> dataLaporanModels;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_laporan);


        serv = new URLServer();

        getSupportActionBar().setTitle("List Laporan Masyakat");

        recyclerView = findViewById(R.id.recyLaporan);
        recyclerView.setHasFixedSize(true);
        dataLaporanModels = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        adapter = new adapterLaporan(dataLaporanModels,this);
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
//                        startActivity(new Intent(adminListPerintah.this, profileAdmin.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        return true;
                    case R.id.homes:
                        startActivity(new Intent(adminListLaporan.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(adminListLaporan.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminListLaporan.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(adminListLaporan.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        sessionManager = new SessionManager(adminListLaporan.this);
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
        return true;
    }

    private  void loadData(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Memuat Data...");
        progressDialog.show();
        AndroidNetworking.post(serv.server+"/laporan/getAll.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray ja = response.getJSONArray("result");
                            for(int i=0;i<ja.length();i++){
                                JSONObject jo = ja.getJSONObject(i);
                                String status;
                                if(jo.getString("is_active").equals("1")){
                                    status = "Belum Selesai";
                                }else{
                                    status = "Selesai";
                                }
                                dataLaporanModel data = new dataLaporanModel(
                                        jo.getString("id_laporan"),
                                        jo.getString("detail_laporan"),
                                        status,
                                        jo.getString("nama"),
                                        jo.getString("tgl_laporan")
                                        );
                                dataLaporanModels.add(data);
                            }
                            progressDialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(adminListLaporan.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(adminListLaporan.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

}