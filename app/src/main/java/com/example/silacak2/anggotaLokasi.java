package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class anggotaLokasi extends AppCompatActivity implements OnMapReadyCallback{
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    SessionManager sessionManager;
    LocationManager locationManager;
    URLServer serv;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 7500;
    private MapView mapView;
    private MapboxMap map;
    private Marker markers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota_lokasi);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.mapAnggota);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        serv = new URLServer();

        BottomNavigationView bottomNavigationView = findViewById(R.id.userNav);
        bottomNavigationView.setSelectedItemId(R.id.lokasiAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        startActivity(new Intent(anggotaLokasi.this, profileAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesAnggota:
                        startActivity(new Intent(anggotaLokasi.this, anggotaPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                    case R.id.pesanAnggota:
                        startActivity(new Intent(anggotaLokasi.this, anggotaPesanTampil.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(anggotaLokasi.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        Constants.setIdGps(sessionManager.getUserDetail().get(SessionManager.ID_GPS));
        startService(new Intent(getApplicationContext(),LocationServer.class));

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
    }

    private void movetoLogin() {
        Intent i = new Intent(anggotaLokasi.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void getAllData() {
        AndroidNetworking.post(serv.getAllAnggotaLokasi())
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
                                    String tugas = jo.getString("tugas");
                                    String detail = jo.getString("detail_perintah");

                                    addMarkers(latLng, name, role,tugas,detail);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }
    private void addMarkers(LatLng point, String name, String role,String tugas,String detail) {
        IconFactory iconFactory = IconFactory.getInstance(anggotaLokasi.this);
        Icon icon;
        if (role.equalsIgnoreCase("anggota")) {
            icon = iconFactory.fromResource(R.drawable.policeman);
        } else {
            icon = iconFactory.fromResource(R.drawable.policeman);
        }
        //icon = iconFactory.fromResource(R.drawable.man);

        markers = map.addMarker(new MarkerOptions().position(point));
        markers.setIcon(icon);
        markers.setTitle("Nama : "+name);
        String tugs;

        if(!tugas.equals("null")){
            tugs = tugas;
        }else{
            tugs = "Tidak Ada Tugas";
        }


        if(!detail.equals("null")){
            markers.setSnippet("Tugas Sekarang:\n"+detail+"\n\nTugas Harian: \n"+tugs);
        }else{
            markers.setSnippet("Tugas Harian:\n"+tugs);
        }
    }
    private void clearAllMarkers() {
        if (markers != null) {
            map.removeMarker(markers);
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
                handler.postDelayed(runnable,delay);
                clearAllMarkers();
                getAllData();
            }
        },delay);
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