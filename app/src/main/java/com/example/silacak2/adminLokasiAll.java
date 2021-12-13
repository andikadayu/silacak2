package com.example.silacak2;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class adminLokasiAll extends AppCompatActivity implements OnMapReadyCallback {
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    LocationManager locationManager;
    SessionManager sessionManager;
    URLServer serv;
    int delay = 5000;
    Handler handler = new Handler();
    Runnable runnable;
    private MapView mapView;
    private MapboxMap map;
    private Marker markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lokasi_all);

        sessionManager = new SessionManager(adminLokasiAll.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        Constants.setIdGps(sessionManager.getUserDetail().get(SessionManager.ID_GPS));
        startService(new Intent(getApplicationContext(), LocationServer.class));

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.map_admin);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        serv = new URLServer();

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homes:
                        startActivity(new Intent(adminLokasiAll.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
//                    case R.id.profiles:
//                        startActivity(new Intent(adminPage.this, profileAdmin.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(adminLokasiAll.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(adminLokasiAll.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminLokasiAll.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(adminLokasiAll.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        mapView.getMapAsync(this);
        getAllData();
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
        if(item.getItemId() == R.id.oAbsensi){
            startActivity(new Intent(this,AbsensiActivity.class));
        }
        return true;
    }

    private void movetoLogin() {
        Intent i = new Intent(adminLokasiAll.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void getAllData() {
        AndroidNetworking.post(serv.getAllLokasi())
                .addBodyParameter("api_key", api_keys)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean status = response.getBoolean("status");
                            String first;
                            if (status) {
                                JSONArray ja = response.getJSONArray("result");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    String name = jo.getString("nama");
                                    String role = jo.getString("role");
                                    Double lat = Double.parseDouble(jo.getString("latitude"));
                                    Double lng = Double.parseDouble(jo.getString("longitude"));
                                    LatLng latLng = new LatLng(lat, lng);

                                    addMarkers(latLng, name, role);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
//                            Toast.makeText(adminLokasiAll.this,e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
//                        Toast.makeText(adminLokasiAll.this,anError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.983908, 112.621391), 13));
    }

    private void addMarkers(LatLng point, String name, String role) {
        IconFactory iconFactory = IconFactory.getInstance(adminLokasiAll.this);
        Icon icon;
        if(role.equalsIgnoreCase("admin")){
            icon = iconFactory.fromResource(R.drawable.soldier);
        }else if(role.equalsIgnoreCase("anggota")){
            icon = iconFactory.fromResource(R.drawable.policeman);

        }else{
            icon = iconFactory.fromResource(R.drawable.man);

        }
        markers = map.addMarker(new MarkerOptions().position(point));

        markers.setPosition(point);
        markers.setIcon(icon);

        markers.setTitle("Nama : " + name);

    }

    private void clearAllMarkers() {
        if (markers != null) {
            map.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                clearAllMarkers();
                getAllData();
            }
        }, delay);
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
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