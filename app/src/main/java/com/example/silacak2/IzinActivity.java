package com.example.silacak2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterIzin;
import com.example.silacak2.model.IzinModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IzinActivity extends AppCompatActivity {

    MaterialButton btnIzin;
    SessionManager sessionManager;
    URLServer urlServer = new URLServer();
    String role, id_user;
    RecyclerView recyIzin;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<IzinModel> izinList;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izin);

        btnIzin = findViewById(R.id.btnIzin);
        recyIzin = findViewById(R.id.recyIzin);
        refreshLayout = findViewById(R.id.refreshLayout);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle("Manajemen Izin");

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        actionBar.setDisplayHomeAsUpEnabled(true);


        sessionManager = new SessionManager(getApplicationContext());
        role = sessionManager.getUserDetail().get(SessionManager.ROLE);
        id_user = sessionManager.getUserDetail().get(SessionManager.ID_USER);

        AndroidNetworking.initialize(getApplicationContext());

        izinList = new ArrayList<>();

        btnIzin.setOnClickListener((view) -> {
            startActivity(new Intent(IzinActivity.this, AddIzinActivity.class));
        });

        initialize(this);

        refreshLayout.setOnRefreshListener(()->{
            izinList = new ArrayList<>();
            initialize(this);
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (role.equalsIgnoreCase("anggota")) {
                startActivity(new Intent(this, profileAnggota.class));
                finish();
            } else {
                startActivity(new Intent(this, profileAdmin.class));
                finish();
            }
        }

        return true;
    }

    private void initialize(Activity activity) {
        AndroidNetworking.post(urlServer.server + "/absensi/getIzin.php")
                .addBodyParameter("id_user", id_user)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyIzin.setLayoutManager(layoutManager);

                                recyIzin.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

                                adapter = new adapterIzin(activity, izinList);

                                recyIzin.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    izinList.add(new IzinModel(
                                            jo.getString("id_izin"),
                                            jo.getString("perihal"),
                                            jo.getString("status"),
                                            jo.getString("date_permit")
                                    ));

                                    adapter.notifyDataSetChanged();

                                }


                            } else {
                                Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
        refreshLayout.setRefreshing(false);
    }
}