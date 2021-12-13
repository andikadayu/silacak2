package com.example.silacak2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class anggotaLokasi extends AppCompatActivity implements OnMapReadyCallback,MapboxMap.OnMarkerClickListener{
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
    private String nrps,nama,foto,pangkat,tugass;

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
                        startActivity(new Intent(anggotaLokasi.this, anggotaPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        return true;

                    case R.id.absenAnggota:
                        startActivity(new Intent(anggotaLokasi.this, AbsensiActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
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
        map.setOnMarkerClickListener(this);
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
                                    String pangkat = jo.getString("pangkat");
                                    String nrp = jo.getString("nrp");

                                    addMarkers(latLng, name, role,tugas,detail,pangkat,nrp);
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
    private void addMarkers(LatLng point, String name, String role,String tugas,String detail,String pangkat,String nrp) {
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


        markers.setTitle(nrp+" | "+pangkat + " " + name);


        String tugs;

        if(!tugas.equals("null")){
            tugs = tugas;
        }else{
            tugs = "Tidak Ada Tugas";
        }


        if (!detail.equals("null")) {
            markers.setSnippet("Tugas Sekarang:" + detail + "\n\nTugas Harian:" + tugs);
        } else {
            markers.setSnippet("Tugas Harian:" + tugs);
        }
    }
    private void clearAllMarkers() {
        if (markers != null) {
            //map.removeMarker(markers);
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

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        marker.showInfoWindow(map,mapView);
        String title = marker.getTitle().toString();
        String tus = marker.getSnippet();
        String[] tusplit = tus.split(":");
        String[] titsplit = title.split(" | ");
        nrps = titsplit[0];
        tugass = tusplit[1];
        openDialogInfo();
        return true;
    }

    private void openDialogInfo(){
        ProgressDialog pdg = new ProgressDialog(anggotaLokasi.this);
        pdg.show();
        AndroidNetworking.post(serv.server+"/profile/getNrpDetailProfile.php")
                .addBodyParameter("nrp",nrps)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            nama = response.getString("nama");
                            foto = response.getString("foto");
                            pangkat = response.getString("pangkat");

                            setImageProfile();
                            pdg.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(anggotaLokasi.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void setImageProfile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(anggotaLokasi.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(anggotaLokasi.this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.detail_marker, null);
        final TextView txtnama,txtpangkat,txtnrp,txttugas;
        final ImageView imgView;

        txtnama = view.findViewById(R.id.namaMarkDetail);
        txtpangkat = view.findViewById(R.id.jabatanMarkDetail);
        txtnrp = view.findViewById(R.id.nrpMarkDetail);
        txttugas = view.findViewById(R.id.tugasMarkDetail);
        imgView = view.findViewById(R.id.imgMarkDetail);

        txtnama.setText(nama);
        txtpangkat.setText(pangkat);
        txtnrp.setText(nrps);
        txttugas.setText(tugass);


        if(!foto.equals("null")){
            byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

            imgView.setImageBitmap(bitmap);
        }
        builder.setView(view)
                .setTitle("Detail Personil")
                .setIcon(R.mipmap.ic_presisi)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog ald = builder.create();
        ald.show();

    }

}