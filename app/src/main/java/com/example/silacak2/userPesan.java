package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.silacak2.adapter.adapterPesanUser;
import com.example.silacak2.model.dataPesanModelUser;
import com.example.silacak2.model.dataPesanModelUser2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class userPesan extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView recyclerView2;
    public ArrayList<dataPesanModelUser> dataPesanModelUsers;
    public ArrayList<dataPesanModelUser2> dataPesanModelUser2s;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.LayoutManager layoutManager2;
    public RecyclerView.Adapter adapter;
    public RecyclerView.Adapter adapter2;
    URLServer serv;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pesan);

        serv = new URLServer();

        getSupportActionBar().setTitle("Pesan");

        sessionManager = new SessionManager(userPesan.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        recyclerView = findViewById(R.id.recyPesanUser);
        recyclerView.setHasFixedSize(true);
        dataPesanModelUsers = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());

        loadData();

        layoutManager = new LinearLayoutManager(userPesan.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new adapterPesanUser(this, dataPesanModelUsers);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.penggunaNav);
        bottomNavigationView.setSelectedItemId(R.id.pesanUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homesUser:
                        startActivity(new Intent(userPesan.this, userPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.profilesUser:
                        startActivity(new Intent(userPesan.this, profilUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanUser:
                        return true;
                }
                return false;
            }
        });
    }

    private void movetoLogin() {
        Intent i = new Intent(userPesan.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void loadData() {
        AndroidNetworking.post(serv.getUserPesan())
                .addBodyParameter("action", "tampil_user")
                .addBodyParameter("id_user", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("DataUser");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                dataPesanModelUser item = new dataPesanModelUser(
                                        data.getString("id_user"),
                                        data.getString("nama"),
                                        data.getString("jml")
                                );
                                dataPesanModelUsers.add(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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