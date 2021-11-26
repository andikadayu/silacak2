package com.example.silacak2;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class userPage extends AppCompatActivity implements OnMapReadyCallback {
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    SessionManager sessionManager;
    LocationManager locationManager;
    URLServer serv;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 3000;
    private MapView mapView;
    private MapboxMap map;
    private Marker markers;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.mapPengguna);
        fab = findViewById(R.id.btnLapor);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        serv = new URLServer();

        BottomNavigationView bottomNavigationView = findViewById(R.id.penggunaNav);
        bottomNavigationView.setSelectedItemId(R.id.homesUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesUser:
                        startActivity(new Intent(userPage.this, profilUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanUser:
                        startActivity(new Intent(userPage.this, userPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesUser:
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(userPage.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        Constants.setIdGps(sessionManager.getUserDetail().get(SessionManager.ID_GPS));
        startService(new Intent(getApplicationContext(), LocationServer.class));

        mapView.getMapAsync(this);

        namaPengirim();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
    }

    private void movetoLogin() {
        Intent i = new Intent(userPage.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void getAllData() {
        String id = sessionManager.getUserDetail().get(SessionManager.ID_USER);
        AndroidNetworking.post(serv.getUserLokasi())
                .addBodyParameter("api_key", api_keys)
                .addBodyParameter("id_user", id)
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
                                    String laporan = jo.getString("detail_laporan");

                                    addMarkers(latLng, name, role,laporan);
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

    private void showNotificationPesan(String title, String contents) {
        String channel_id = "my-pesan-notification";

        // Create Custom Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(userPage.this, channel_id);
        builder.setContentTitle(title);
        builder.setContentText(contents);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_presisi);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, "my-pesan-request", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(userPage.this);
        managerCompat.notify(22, builder.build());

        notifyServer2("done", "kosong");
    }

    private void namaPengirim() {
        String id = sessionManager.getUserDetail().get(SessionManager.ID_USER);
        AndroidNetworking.post("https://silacak.pt-ckit.com/getNama.php")
                .addBodyParameter("api_key", api_keys)
                .addBodyParameter("id_user", id)
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
                                    String pesan = "Ada Pesan Baru Dari " + jo.getString("nama");
                                    notifyServer2("notify", pesan);
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

    private void addMarkers(LatLng point, String name, String role,String laporan) {
        IconFactory iconFactory = IconFactory.getInstance(userPage.this);
        Icon icon;

        icon = iconFactory.fromResource(R.drawable.man);

        markers = map.addMarker(new MarkerOptions().position(point));
        markers.setIcon(icon);
        markers.setTitle(name);
        if(!laporan.equals("null")){
            markers.setSnippet("Mengirim Laporan:\n"+laporan);
        }
    }

    private void clearAllMarkers() {
        if (markers != null) {
            map.removeMarker(markers);
        }
    }

    private void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(userPage.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(userPage.this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_lapor,null);
        final EditText txtLapor = view.findViewById(R.id.txtLapor);

        builder.setView(view)
                .setTitle("Menyetel Laporan Masalah yang Terjadi")
                .setIcon(R.drawable.ic_baseline_report_problem_24)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Laporkan Masalah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String laporan  = txtLapor.getText().toString();
                        String id_masyarakat = sessionManager.getUserDetail().get(SessionManager.ID_USER);
                        if(laporan.equals("") || id_masyarakat.equals("")){
                            Toast.makeText(userPage.this, "Complete the Form", Toast.LENGTH_SHORT).show();
                        }else{
                            // TODO Post to server
                            setLaporan(laporan,id_masyarakat);
                        }
                    }
                });

        AlertDialog ald = builder.create();
        ald.show();
    }

    private void setLaporan(String laporan,String id_masyarakat){
        AndroidNetworking.post(serv.server + "/laporan/setLaporan.php")
                .addBodyParameter("id_masyarakat",id_masyarakat)
                .addBodyParameter("detail_laporan",laporan)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(userPage.this, "Berhasil Melaporankan Masalah", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(userPage.this, "Tidak Bisa Melaporankan Masalah", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(userPage.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
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