package com.example.silacak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Point;
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

public class adminPageNew extends AppCompatActivity implements OnMapReadyCallback {

    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    SessionManager sessionManager;
    LocationManager locationManager;
    URLServer serv;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    FloatingActionButton fab;
    EditText ePerintah;
    boolean isRequest = true;
    private MapView mapView;
    private MapboxMap map;
    private Marker markers;
    private Marker perintahMark;
    private com.mapbox.mapboxsdk.annotations.Marker destinationMarker;
    private Point originPosition, destinationPosition, waypointPosition;
    private Location originLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page_new);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.map_adminnew);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        serv = new URLServer();
        //Nav Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        bottomNavigationView.setSelectedItemId(R.id.homes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homes:
                        return true;
//                    case R.id.profiles:
//                        startActivity(new Intent(adminPage.this, profileAdmin.class));
//                        overridePendingTransition(0, 0);
//                        finish();
//                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(adminPageNew.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(adminPageNew.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(adminPageNew.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(adminPageNew.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(adminPageNew.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        Constants.setIdGps(sessionManager.getUserDetail().get(SessionManager.ID_GPS));
        startService(new Intent(getApplicationContext(), LocationServer.class));
        startService(new Intent(getApplicationContext(), NotificationBerkumpul.class));

        mapView.getMapAsync(this);

        getAllData();

        Toast.makeText(adminPageNew.this, "Ini New Version", Toast.LENGTH_SHORT).show();

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
        if (item.getItemId() == R.id.oListPerintah) {
            startActivity(new Intent(this, adminListPerintah.class));
        }
        if (item.getItemId() == R.id.oLokasiUser) {
            startActivity(new Intent(this, adminLokasiAll.class));
        }
        return true;
    }

    private void movetoLogin() {
        Intent i = new Intent(adminPageNew.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void getAllData() {
        AndroidNetworking.post(serv.getAdmTracking())
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

                                    addMarkers(latLng, name, role, tugas, detail);

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


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        if(getIntent().hasExtra("latitude")){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getIntent().getDoubleExtra("latitude",-7.983908), getIntent().getDoubleExtra("longitude",112.621391)),13));
        }else{
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-7.983908, 112.621391),13));
        }
    }

    private void addMarkers(LatLng point, String name, String role, String tugas, String detail) {
        IconFactory iconFactory = IconFactory.getInstance(adminPageNew.this);
        Icon icon;
        if (role.equalsIgnoreCase("admin")) {
            icon = iconFactory.fromResource(R.drawable.soldier);
        } else {
            icon = iconFactory.fromResource(R.drawable.policeman);
        }

        markers = map.addMarker(new MarkerOptions().position(point));
        markers.setPosition(point);
        markers.setIcon(icon);
        markers.setTitle("Nama : " + name);
        String tugs;

        if (!tugas.equals("null")) {
            tugs = tugas;
        } else {
            tugs = "Tidak Ada Tugas";
        }


        if (!detail.equals("null")) {
            markers.setSnippet("Tugas Sekarang:\n" + detail + "\n\nTugas Harian: \n" + tugs);
        } else {
            markers.setSnippet("Tugas Harian:\n" + tugs);
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


    private void showNotificationPesan(String title, String contents) {
        String channel_id = "my-pesan-notification";

        // Create Custom Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(adminPageNew.this, channel_id);
        builder.setContentTitle(title);
        builder.setContentText(contents);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_presisi);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, "my-pesan-request", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(adminPageNew.this);
        managerCompat.notify(22, builder.build());

        notifyServer2("done", "kosong");
    }


    private void notifyServer2(String purpose, String pesan) {
        AndroidNetworking.post("https://silacak.pt-ckit.com/getNotifikasi.php")
                .addBodyParameter("purpose", purpose)
                .addBodyParameter("id_user", sessionManager.getUserDetail().get(SessionManager.ID_USER))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                if (purpose.equalsIgnoreCase("notify")) {
                                    showNotificationPesan(getString(R.string.app_name), pesan);
                                } else {
                                    return;
                                }
                            } else {
                                return;
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


}