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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterTampilPesanAdmin;
import com.example.silacak2.adapter.adapterTampilPesanAnggota;
import com.example.silacak2.model.dataTampilPesanModel;
import com.example.silacak2.model.dataTampilPesanModelAnggota;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class anggotaPesanTampil extends AppCompatActivity {
    String idanggotaPesan, namaanggotaPesan, idPengirim;
    TextView txnamaKontak;
    ImageView imgBack;
    EditText edTulisPesan;
    LinearLayout lSend;
    URLServer serv;
    SessionManager sessionManager;
    private Activity activity;
    RecyclerView recyclerView;
    public ArrayList<dataTampilPesanModelAnggota> dataTampilPesanModelAnggotas;
    public RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota_pesan_tampil);

        sessionManager = new SessionManager(anggotaPesanTampil.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        serv = new URLServer();

        edTulisPesan = (EditText) findViewById(R.id.tulisPesan);
        txnamaKontak = (TextView) findViewById(R.id.namaKontak);
        //imgBack = (ImageView) findViewById(R.id.back);
        //lSend = (LinearLayout) findViewById(R.id.send);

//        Intent data = getIntent();
//        namaanggotaPesan = data.getStringExtra("nama");
//
//        txnamaKontak.setText(namaanggotaPesan);
//        imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(anggotaPesanTampil.this, anggotaPage.class);
//                startActivity(intent);
//            }
//        });

        recyclerView = findViewById(R.id.chat_recycler_viewAnggota);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataTampilPesanModelAnggotas = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        adapter = new adapterTampilPesanAnggota(this, dataTampilPesanModelAnggotas);
        recyclerView.setAdapter(adapter);

        loadData();

//        lSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                kirimPesan();
//                Intent intent = new Intent(anggotaPesanTampil.this, anggotaPesanTampil.class);
//                intent.putExtra("id_user", idanggotaPesan);
//                intent.putExtra("nama_user", namaanggotaPesan);
//                startActivity(intent);
//            }
//        });

        idPengirim = sessionManager.getUserDetail().get(SessionManager.ID_USER);

        BottomNavigationView bottomNavigationView = findViewById(R.id.userNav);
        bottomNavigationView.setSelectedItemId(R.id.pesanAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        startActivity(new Intent(anggotaPesanTampil.this, profileAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        startActivity(new Intent(anggotaPesanTampil.this, anggotaLokasi.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesAnggota:
                        startActivity(new Intent(anggotaPesanTampil.this, anggotaPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanAnggota:
                        return true;
                }
                return false;
            }
        });
    }

    private void movetoLogin() {
        Intent i = new Intent(anggotaPesanTampil.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        Intent data = getIntent();
        //String id_pengirim = Constants.getIdUser();
        String id_penerima = data.getStringExtra("id_user");
        AndroidNetworking.post(serv.getPesanTampilAnggota())
                .addBodyParameter("id_pengirim", "1")
                .addBodyParameter("id_penerima", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .addBodyParameter("action", "tampil")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataUser");
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                int id_pengirim = data.getInt("id_pengirim");
                                //String id = sessionManager.getUserDetail().get(SessionManager.ID_USER);
                                dataTampilPesanModelAnggota item = new dataTampilPesanModelAnggota(
                                        data.getString("id_pesan"),
                                        data.getString("id_pengirim"),
                                        data.getString("id_penerima"),
                                        data.getString("pesan"),
                                        data.getString("jam"),
                                        data.getString("tgl"),
                                        id_pengirim == Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.ID_USER)) ? adapterTampilPesanAnggota.MESSAGE_TYPE_OUT : adapterTampilPesanAnggota.MESSAGE_TYPE_IN
                                );
                                dataTampilPesanModelAnggotas.add(item);
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

//    private void kirimPesan(){
//        String pesan = edTulisPesan.getText().toString();
//
//        Intent data = getIntent();
//        String id_penerima = data.getStringExtra("id_user");
//        AndroidNetworking.post("https://silacak.pt-ckit.com/kirimPesan.php")
//                .addBodyParameter("id_pengirim", sessionManager.getUserDetail().get(SessionManager.ID_USER))
//                .addBodyParameter("id_penerima", id_penerima)
//                .addBodyParameter("pesan", pesan)
//                .addBodyParameter("action", "pesan")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            if (response.getString("pesan").equals("sukses")){
//                                Toast.makeText(getApplicationContext(), "Pesan Terkirim", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Pesan Gagal Terkirim", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(anggotaPesanTampil.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                        Toast.makeText(anggotaPesanTampil.this, "Error Time Out", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }



}