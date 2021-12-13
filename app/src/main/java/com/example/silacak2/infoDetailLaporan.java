package com.example.silacak2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONObject;

public class infoDetailLaporan extends AppCompatActivity implements OnMapReadyCallback {

    TextView lblNama,lblWaktu,lblStatus,lblDetail;
    Button btnBack;
    MapView mapView;
    MapboxMap map;
    URLServer serv;
    String id_laporan,detail_laporan,lat_laporan,lng_laporan,nama,tgl_laporan,status;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail_laporan);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.map_laporan);

        lblNama = findViewById(R.id.lblNamaLaporan);
        lblWaktu = findViewById(R.id.lblWaktuLaporan);
        lblStatus = findViewById(R.id.lblStatusLaporan);
        lblDetail = findViewById(R.id.lblDetailLaporan);

        btnBack = findViewById(R.id.btnBackLaporan);

        serv = new URLServer();

        Intent data = getIntent();
        id_laporan = data.getStringExtra("id_laporan");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(infoDetailLaporan.this,adminListLaporan.class));
                finish();
            }
        });
        mapView.getMapAsync(this);

    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        getDataLaporan();
    }

    private void getDataLaporan(){
        ProgressDialog pgr = new ProgressDialog(infoDetailLaporan.this);
        pgr.setCancelable(false);
        pgr.setMessage("Memuat Data...");
        pgr.show();
        AndroidNetworking.post(serv.server + "/laporan/getOnce.php")
                .addBodyParameter("id_laporan",id_laporan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getString("is_active").equals("1")){
                                status = "Belum Selesai";
                            }else{
                                status = "Selesai";
                            }

                            nama = response.getString("nama");
                            detail_laporan = response.getString("detail_laporan");
                            lat_laporan = response.getString("lat_laporan");
                            lng_laporan = response.getString("lng_laporan");
                            tgl_laporan = response.getString("tgl_laporan");
                            initializeData();
                            initializeMarker();
                            pgr.dismiss();

                        }catch (Exception e){
                            e.printStackTrace();
                            pgr.dismiss();
                            Toast.makeText(infoDetailLaporan.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(infoDetailLaporan.this,adminListLaporan.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void initializeData(){
        lblNama.setText(nama);
        lblStatus.setText(status);
        lblWaktu.setText(tgl_laporan);
        lblDetail.setText(detail_laporan);
    }

    private void initializeMarker(){
        double lat = Double.parseDouble(lat_laporan);
        double lng = Double.parseDouble(lng_laporan);
        LatLng point = new LatLng(lat,lng);
        IconFactory iconFactory = IconFactory.getInstance(infoDetailLaporan.this);
        Icon icon;
        icon = iconFactory.fromResource(R.drawable.warning);
        marker = map.addMarker(new MarkerOptions().position(point));
        marker.setPosition(point);
        marker.setIcon(icon);
        marker.setTitle("Danger");

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point,13));
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
