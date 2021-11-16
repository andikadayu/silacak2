package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

public class scan_qrcode extends AppCompatActivity {

    URLServer serv;
    boolean laststatus;
    private IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        serv = new URLServer();

        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil tidak ditemukan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(scan_qrcode.this, adminPageNew.class));
                finish();
            } else {
                String nrps = result.getContents();

                Intent kirimData = new Intent(scan_qrcode.this, info_bio_nrp.class);
                kirimData.putExtra("nrp", nrps.trim());
                kirimData.putExtra("from_menu", "home");
                startActivity(kirimData);
                finish();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}