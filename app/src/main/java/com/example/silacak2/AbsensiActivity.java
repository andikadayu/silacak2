package com.example.silacak2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AbsensiActivity extends AppCompatActivity {

    URLServer serv = new URLServer();
    String absen, id_user, role;
    boolean condtiotionScan = false;
    SessionManager sessionManager;
    String afternoon, jam_masuk, jam_keluar, jam_keluar_friday, exp_jam_masuk, exp_jam_keluar, exp_jam_keluar_friday;
    private IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

        sessionManager = new SessionManager(AbsensiActivity.this);
        id_user = sessionManager.getUserDetail().get(SessionManager.ID_USER);
        role = sessionManager.getUserDetail().get(SessionManager.ROLE);

        AndroidNetworking.initialize(AbsensiActivity.this);

        initializeTime();

    }

    private void initializeApps() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.FRIDAY) {

            String today = (String) DateFormat.format(
                    "hh:mm:ss", new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            try {
                Date afternoons = sdf.parse(afternoon);

                Date befores = sdf.parse(jam_masuk);
                Date expireds = sdf.parse(exp_jam_masuk);
                Date todays = sdf.parse(today);
                Date Kbefores = sdf.parse(jam_keluar_friday);
                Date Kexpired = sdf.parse(exp_jam_keluar_friday);

                assert todays != null;
                if (afternoons.before(todays)) {
                    absen = "in";

                    condtiotionScan = todays.after(befores) && todays.before(expireds);
                    goScan();
                } else {
                    absen = "out";
                    condtiotionScan = todays.after(Kbefores) && todays.before(Kexpired);
                    goScan();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AbsensiActivity.this, "Time ERROR", Toast.LENGTH_SHORT).show();
                if (role.equalsIgnoreCase("admin")) {
                    startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                } else {
                    startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                }
                finish();
            }

        } else {

            String today = (String) DateFormat.format(
                    "hh:mm:ss", new Date());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            try {
                Date afternoons = sdf.parse(afternoon);

                Date befores = sdf.parse(jam_masuk);
                Date expireds = sdf.parse(exp_jam_masuk);
                Date todays = sdf.parse(today);
                Date Kbefores = sdf.parse(jam_keluar);
                Date Kexpired = sdf.parse(exp_jam_keluar);

                assert todays != null;
                if (afternoons.before(todays)) {
                    absen = "in";

                    condtiotionScan = todays.after(befores) && todays.before(expireds);
                    goScan();
                } else {
                    absen = "out";
                    condtiotionScan = todays.after(Kbefores) && todays.before(Kexpired);
                    goScan();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AbsensiActivity.this, "Time ERROR", Toast.LENGTH_SHORT).show();
                if (role.equalsIgnoreCase("admin")) {
                    startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                } else {
                    startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                }
                finish();
            }
        }
    }

    private void initializeTime() {
        ProgressDialog pdg = new ProgressDialog(AbsensiActivity.this);
        pdg.setMessage("Initialzing....");
        pdg.setTitle("Absensi");
        pdg.show();
        AndroidNetworking.post(serv.server + "/absensi/absensi.json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            afternoon = response.getString("afternoon");
                            jam_masuk = response.getString("jam_masuk");
                            jam_keluar = response.getString("jam_keluar");
                            jam_keluar_friday = response.getString("jam_keluar_friday");
                            exp_jam_masuk = response.getString("exp_jam_masuk");
                            exp_jam_keluar = response.getString("exp_jam_keluar");
                            exp_jam_keluar_friday = response.getString("exp_jam_keluar_friday");

                            initializeApps();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AbsensiActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                            if (role.equalsIgnoreCase("admin")) {
                                startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                            } else {
                                startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(AbsensiActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                        finish();
                    }
                });
    }

    private void goScan() {

        if (condtiotionScan) {
            intentIntegrator = new IntentIntegrator(AbsensiActivity.this);
            intentIntegrator.initiateScan();

        } else {
            Toast.makeText(AbsensiActivity.this, "Tidak Bisa Scan(Belum Waktunya atau sudah Habis)", Toast.LENGTH_SHORT).show();
            if (role.equalsIgnoreCase("admin")) {
                startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
            } else {
                startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
            }
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
                if (role.equalsIgnoreCase("admin")) {
                    startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                } else {
                    startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                }
                finish();
            } else {
                String qrcode = result.getContents().trim();

                sendToServer(qrcode);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendToServer(String idQrCode) {
        ProgressDialog pdg = new ProgressDialog(AbsensiActivity.this);
        pdg.setMessage("Loading....");
        pdg.setTitle("Absensi");
        pdg.show();
        AndroidNetworking.post(serv.server + "/absensi/addAbsen.php")
                .addBodyParameter("id_qrcode", idQrCode)
                .addBodyParameter("absen", absen)
                .addBodyParameter("id_user", id_user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = response.getString("msg");
                            Toast.makeText(AbsensiActivity.this, msg, Toast.LENGTH_SHORT).show();
                            pdg.dismiss();
                            if (role.equalsIgnoreCase("admin")) {
                                startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                            } else {
                                startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                            }
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AbsensiActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                            pdg.dismiss();
                            if (role.equalsIgnoreCase("admin")) {
                                startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                            } else {
                                startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(AbsensiActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        pdg.dismiss();
                        if (role.equalsIgnoreCase("admin")) {
                            startActivity(new Intent(AbsensiActivity.this, adminPageNew.class));
                        } else {
                            startActivity(new Intent(AbsensiActivity.this, anggotaPage.class));
                        }
                        finish();
                    }
                });
    }
}

