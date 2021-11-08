package com.example.silacak2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class anggotaPage extends AppCompatActivity implements OnMapReadyCallback {
    final String api_keys = "pv2A0M0C6NbfoQNF0lQ0QRyNRuTnWVQK";
    SessionManager sessionManager;
    LocationManager locationManager;
    URLServer serv;
    Handler handler = new Handler();
    Handler handlers = new Handler();
    Runnable runnable, runnables;
    int delay = 3000;
    FloatingActionButton fabTugas;
    EditText textTugas;
    private MapView mapView;
    private MapboxMap map;
    private Marker markers, perintahMarkers;

    private PendingIntent pendingIntent;
    private static final int ALARM_REQUEST_CODE = 134;
    //set interval notifikasi 10 detik
    private int interval_seconds = 1;
    private int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota_page);

        sessionManager = new SessionManager(anggotaPage.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        Mapbox.getInstance(this, getString(R.string.access_token));
        mapView = findViewById(R.id.mapUser);

        fabTugas = findViewById(R.id.btnTugas);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        serv = new URLServer();

        BottomNavigationView bottomNavigationView = findViewById(R.id.userNav);
        bottomNavigationView.setSelectedItemId(R.id.homesAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        startActivity(new Intent(anggotaPage.this, profileAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        startActivity(new Intent(anggotaPage.this, anggotaLokasi.class));
                        overridePendingTransition(0, 0);
                        finish();
                    case R.id.pesanAnggota:
                        startActivity(new Intent(anggotaPage.this, anggotaPesanTampil.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.homesAnggota:
                        return true;
                }
                return false;
            }
        });

        Constants.setIdGps(sessionManager.getUserDetail().get(SessionManager.ID_GPS));
        startService(new Intent(getApplicationContext(), LocationServer.class));
        //startService(new Intent(getApplicationContext(), NotificationPesan.class));

        mapView.getMapAsync(this);

        //Set Tugas Button
        fabTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        //namaPengirim();



    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        getAllData();
        getPerintah();
    }

    private void movetoLogin() {
        Intent i = new Intent(anggotaPage.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(anggotaPage.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(anggotaPage.this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.set_tugas, null);
        textTugas = view.findViewById(R.id.txtTugas);

        builder.setView(view)
                .setTitle("Menyetel Tugas")
                .setIcon(R.drawable.ic_baseline_assignment_24)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tugas = textTugas.getText().toString();
                        String id_lokasi = sessionManager.getUserDetail().get(SessionManager.ID_GPS);
                        setTugasAction(id_lokasi, tugas);
                    }
                });

        AlertDialog ald = builder.create();
        ald.show();
        //namaPengirim();
    }

    private void getAllData() {
        String id = sessionManager.getUserDetail().get(SessionManager.ID_USER);
        AndroidNetworking.post(serv.getAnggotaLokasi())
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

    private void setTugasAction(String ids, String tugas) {
        AndroidNetworking.post(serv.setAnggotaTugas())
                .addBodyParameter("id_lokasi", ids)
                .addBodyParameter("tugas", tugas)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(anggotaPage.this, "Menyetel Tugas Berhasil", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(anggotaPage.this, "Menyetel Tugas Gagal", Toast.LENGTH_SHORT).show();
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

    private void getPerintah() {
        AndroidNetworking.post(serv.getAnggotaPerintah())
                .addBodyParameter("purpose", "get")
                .addBodyParameter("id_lokasi", sessionManager.getUserDetail().get(SessionManager.ID_GPS))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    String detail = jo.getString("detail_perintah");
                                    Double lat = Double.parseDouble(jo.getString("lat"));
                                    Double lng = Double.parseDouble(jo.getString("lng"));
                                    String latt = jo.getString("lat").toString() + "," + jo.getString("lng").toString();
                                    LatLng latLng = new LatLng(lat, lng);

                                    addPosition(latLng, detail, latt);
                                    //notifyServer("notify");
                                }
                            } else {
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();

                    }
                });
    }

    private void addPosition(LatLng point, String detail, String latt) {
        IconFactory iconFactory = IconFactory.getInstance(anggotaPage.this);
        Icon icon;
        icon = iconFactory.fromResource(R.drawable.warning);
        perintahMarkers = map.addMarker(new MarkerOptions().position(point));
        perintahMarkers.setPosition(point);
        perintahMarkers.setIcon(icon);
        perintahMarkers.setTitle("Perintah Baru");
        perintahMarkers.setSnippet("Location : " + latt + "\nDetail Tugas:\n" + detail);
    }

    private void addMarkers(LatLng point, String name, String role, String tugas, String detail) {
        IconFactory iconFactory = IconFactory.getInstance(anggotaPage.this);
        Icon icon;
//        if (role.equalsIgnoreCase("admin")) {
//            icon = iconFactory.fromResource(R.drawable.man);
//        } else {
//            icon = iconFactory.fromResource(R.drawable.motorcycle);
//        }
        icon = iconFactory.fromResource(R.drawable.policeman);
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

//    private void showNotificationPerintah(String title, String contents) {
//        String channel_id = "my-perintah-notification";
//
//        // Create Custom Notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(anggotaPage.this, channel_id);
//        builder.setContentTitle(title);
//        builder.setContentText(contents);
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setSmallIcon(R.mipmap.ic_presisi);
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        builder.setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channel_id, "my-perintah-request", NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(anggotaPage.this);
//        managerCompat.notify(22, builder.build());
//
//        notifyServer("done");
//
//    }

//    private void notifyServer(String purpose) {
//        AndroidNetworking.post(serv.getAnggotaPerintah())
//                .addBodyParameter("purpose", purpose)
//                .addBodyParameter("id_lokasi", sessionManager.getUserDetail().get(SessionManager.ID_GPS))
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            boolean status = response.getBoolean("status");
//                            if (status) {
//                                if (purpose.equalsIgnoreCase("notify")) {
//                                    showNotificationPerintah(getString(R.string.app_name), "Ada Perintah Baru");
//                                } else {
//                                    return;
//                                }
//                            } else {
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                    }
//                });
//    }

//    private void showNotificationPesan(String title, String contents) {
//        String channel_id = "my-pesan-notification";
//        String nama = sessionManager.getUserDetail().get(SessionManager.NAMA);
//        Intent resultIntent = new Intent(getApplicationContext(),anggotaPesanTampil.class);
//        resultIntent.putExtra("nama", nama);
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                getApplicationContext(),
//                0,
//                resultIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//
//        // Create Custom Notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(anggotaPage.this, channel_id);
//        builder.setContentTitle(title);
//        builder.setContentText(contents);
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setSmallIcon(R.mipmap.ic_presisi);
//        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channel_id, "my-pesan-request", NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(anggotaPage.this);
//        managerCompat.notify(22, builder.build());
//
//        notifyServer2("done", "kosong");
//    }
//
//    private void namaPengirim() {
//        String id = sessionManager.getUserDetail().get(SessionManager.ID_USER);
//        AndroidNetworking.post("https://silacak.pt-ckit.com/getNama.php")
//                .addBodyParameter("api_key", api_keys)
//                .addBodyParameter("id_user", id)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Boolean status = response.getBoolean("status");
//                            String first;
//                            if (status) {
//                                JSONArray ja = response.getJSONArray("result");
//                                for (int i = 0; i < ja.length(); i++) {
//                                    JSONObject jo = ja.getJSONObject(i);
//                                    String pesan = "Ada Pesan Baru Dari " + jo.getString("nama");
//                                    notifyServer2("notify", pesan);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                    }
//                });
//    }
//
//    private void notifyServer2(String purpose, String pesan) {
//        AndroidNetworking.post("https://silacak.pt-ckit.com/getNotifikasi.php")
//                .addBodyParameter("purpose", purpose)
//                .addBodyParameter("id_user", sessionManager.getUserDetail().get(SessionManager.ID_USER))
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            boolean status = response.getBoolean("status");
//                            if (status) {
//                                if (purpose.equalsIgnoreCase("notify")) {
//                                    showNotificationPesan(getString(R.string.app_name), pesan);
//                                    //startAlarmManager();
//                                } else {
//                                    return;
//                                }
//                            } else {
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        anError.printStackTrace();
//                    }
//                });
//    }

    private void clearAllMarkers() {
        if (markers != null) {
            map.removeMarker(markers);
        }
    }

    private void clearPerintahMarkers() {
        if (perintahMarkers != null) {
            map.removeMarker(perintahMarkers);
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
                startService(new Intent(getApplicationContext(),AppReceiver.class));
                //namaPengirim();
            }
        }, delay);
        handlers.postDelayed(runnables = new Runnable() {
            @Override
            public void run() {
                handlers.postDelayed(runnables, 10000);
                clearPerintahMarkers();
                getPerintah();
            }
        }, 10000);
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        handler.removeCallbacks(runnables);
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

//    public void startAlarmManager() {
//        //set alarm manager dengan memasukkan waktu yang telah dikonversi menjadi milliseconds
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.SECOND, interval_seconds);
//        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        Toast.makeText(this, "AlarmManager Start.", Toast.LENGTH_SHORT).show();
//    }
}