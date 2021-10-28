package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
public class profileAnggota extends AppCompatActivity {
    SessionManager sessionManager;
    TextView txnama;
    Button btLogouta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_anggota);

        txnama = (TextView) findViewById(R.id.txtnamaanggota);
        btLogouta = (Button) findViewById(R.id.btnlogoutsAnggota);

        BottomNavigationView bottomNavigationView = findViewById(R.id.anggotaNav);
        bottomNavigationView.setSelectedItemId(R.id.profilesAnggota);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilesAnggota:
                        return true;
                    case R.id.homesAnggota:
                        startActivity(new Intent(profileAnggota.this, anggotaPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.lokasiAnggota:
                        startActivity(new Intent(profileAnggota.this, anggotaLokasi.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanAnggota:
                        startActivity(new Intent(profileAnggota.this, anggotaPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        sessionManager = new SessionManager(profileAnggota.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        txnama.setText(sessionManager.getUserDetail().get(SessionManager.NAMA));

        btLogouta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutSession();
                movetoLogin();
                stopService(new Intent(getApplicationContext(), LocationServer.class));
            }
        });
    }
    private void movetoLogin() {
        Intent i = new Intent(profileAnggota.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}