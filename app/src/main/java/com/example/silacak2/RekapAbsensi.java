package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.silacak2.adapter.adapterAbsensi;
import com.example.silacak2.adapter.adapterYesterday;
import com.example.silacak2.model.AbsensiModel;
import com.example.silacak2.model.YesterdayModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RekapAbsensi extends AppCompatActivity {

    TextView availYesterday,titleYesterday;
    RecyclerView recyYesterday,recyAbsensi;
    public RecyclerView.Adapter adapter,adapters;
    public LinearLayoutManager layoutManager,layoutManagers;
    ArrayList<YesterdayModel> yesterdayList;
    ArrayList<AbsensiModel> absensiList;
    URLServer server = new URLServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_absensi);

        availYesterday = findViewById(R.id.availYesterday);
        titleYesterday = findViewById(R.id.titleYesterday);

        recyYesterday = findViewById(R.id.recyYesterday);
        recyAbsensi = findViewById(R.id.recyAbsensi);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setTitle("Rekap Absensi");

        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        actionBar.setDisplayHomeAsUpEnabled(true);


        AndroidNetworking.initialize(RekapAbsensi.this);

        yesterdayList = new ArrayList<YesterdayModel>();

        initializeYesterday();

        absensiList = new ArrayList<AbsensiModel>();

        initializeAbsensi();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_admin, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            startActivity(new Intent(this,adminPageNew.class));
        }

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

    private void initializeYesterday(){
        AndroidNetworking.post(server.server+"/absensi/getYesterday.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            String counts = response.getString("count");
                            titleYesterday.setText("Tidak Hadir Kemarin ("+counts+")");
                            if(status){
                                JSONArray ja = response.getJSONArray("data");
                                availYesterday.setVisibility(View.GONE);
                                recyYesterday.setVisibility(View.VISIBLE);

                                layoutManager = new LinearLayoutManager(RekapAbsensi.this, RecyclerView.HORIZONTAL, false);
                                recyYesterday.setLayoutManager(layoutManager);

                                recyYesterday.addItemDecoration(new DividerItemDecoration(RekapAbsensi.this, DividerItemDecoration.HORIZONTAL));

                                adapter = new adapterYesterday(RekapAbsensi.this,yesterdayList);
                                recyYesterday.setAdapter(adapter);
                                for(int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);

                                    yesterdayList.add(
                                            new YesterdayModel(
                                                    jo.getString("id_anggota"),
                                                    jo.getString("foto"),
                                                    jo.getString("nama"),
                                                    jo.getString("nrp")
                                            )

                                    );

                                    adapter.notifyDataSetChanged();

                                }

                                LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
                                linearSnapHelper.attachToRecyclerView(recyYesterday);
                                Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if(layoutManager.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount() - 1)){
                                            layoutManager.smoothScrollToPosition(recyYesterday,new RecyclerView.State(),layoutManager.findLastCompletelyVisibleItemPosition() + 1);
                                        }else{
                                            layoutManager.smoothScrollToPosition(recyYesterday,new RecyclerView.State(), 0);
                                        }
                                    }
                                },0,3000);



                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void initializeAbsensi(){
        ProgressDialog progressDialog = new ProgressDialog(RekapAbsensi.this);
        progressDialog.setTitle("Rekap Absensi");
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.post(server.server+"/absensi/getAbsensi.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            progressDialog.dismiss();
                            JSONArray ja = response.getJSONArray("data");
                            layoutManagers = new LinearLayoutManager(RekapAbsensi.this, RecyclerView.VERTICAL, false);
                            recyAbsensi.setLayoutManager(layoutManagers);

                            recyAbsensi.addItemDecoration(new DividerItemDecoration(RekapAbsensi.this, DividerItemDecoration.VERTICAL));

                            adapters = new adapterAbsensi(RekapAbsensi.this,absensiList);
                            recyAbsensi.setAdapter(adapters);
                            for(int i=0;i<ja.length();i++){
                                JSONObject jo = ja.getJSONObject(i);

                                absensiList.add(new AbsensiModel(
                                        jo.getString("id_anggota"),
                                        jo.getString("nama"),
                                        jo.getString("foto"),
                                        jo.getString("hadir"),
                                        jo.getString("tidak_hadir")
                                ));
                                adapters.notifyDataSetChanged();

                            }




                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(RekapAbsensi.this, "ERROR RESPONSES", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(RekapAbsensi.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}