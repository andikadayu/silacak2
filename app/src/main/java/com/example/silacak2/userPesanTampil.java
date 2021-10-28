package com.example.silacak2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.silacak2.adapter.adapterTampilPesanAnggota;
import com.example.silacak2.adapter.adapterTampilPesanUser;
import com.example.silacak2.model.dataTampilPesanModelAnggota;
import com.example.silacak2.model.dataTampilPesanModelUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class userPesanTampil extends AppCompatActivity {
    URLServer serv;
    EditText edTulisPesan;
    TextView txnamaKontak;
    ImageView imgBack;
    LinearLayout lSend;
    String iduserPesan, namauserPesan;
    SessionManager sessionManager;
    RecyclerView recyclerView;
    public ArrayList<dataTampilPesanModelUser> dataTampilPesanModelUsers;
    public RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pesan_tampil);

        sessionManager = new SessionManager(userPesanTampil.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        serv = new URLServer();

        edTulisPesan = (EditText) findViewById(R.id.tulisPesan);
        txnamaKontak = (TextView) findViewById(R.id.namaKontak);
        imgBack = (ImageView) findViewById(R.id.back);
        lSend = (LinearLayout) findViewById(R.id.send);

        Intent data = getIntent();
        namauserPesan = data.getStringExtra("nama_user");
        iduserPesan = data.getStringExtra("id_user");

        txnamaKontak.setText(namauserPesan);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userPesanTampil.this, userPesan.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.chat_recycler_viewUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        dataTampilPesanModelUsers = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        adapter = new adapterTampilPesanUser(this, dataTampilPesanModelUsers);
        recyclerView.setAdapter(adapter);

        lSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesan();
                Intent intent = new Intent(userPesanTampil.this, userPesanTampil.class);
                intent.putExtra("id_user", iduserPesan);
                intent.putExtra("nama_user", namauserPesan);
                startActivity(intent);
            }
        });

    }

    private void movetoLogin() {
        Intent i = new Intent(userPesanTampil.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        Intent data = getIntent();
        //String id_pengirim = Constants.getIdUser();
        String id_penerima = data.getStringExtra("id_user");
        AndroidNetworking.post(serv.getPesanTampilAnggota())
                .addBodyParameter("id_pengirim", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .addBodyParameter("id_penerima", id_penerima)
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
                                dataTampilPesanModelUser item = new dataTampilPesanModelUser(
                                        data.getString("id_pesan"),
                                        data.getString("id_pengirim"),
                                        data.getString("id_penerima"),
                                        data.getString("pesan"),
                                        data.getString("jam"),
                                        data.getString("tgl"),
                                        id_pengirim == Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.ID_USER)) ? adapterTampilPesanAnggota.MESSAGE_TYPE_OUT : adapterTampilPesanAnggota.MESSAGE_TYPE_IN
                                );
                                dataTampilPesanModelUsers.add(item);
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

    private void kirimPesan(){
        String pesan = edTulisPesan.getText().toString();

        Intent data = getIntent();
        String id_penerima = data.getStringExtra("id_user");
        AndroidNetworking.post("https://silacak.pt-ckit.com/kirimPesan.php")
                .addBodyParameter("id_pengirim", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .addBodyParameter("id_penerima", id_penerima)
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
                            Toast.makeText(userPesanTampil.this, "Gagal, Permintaan Waktu Habis", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(userPesanTampil.this, "Error Time Out", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}