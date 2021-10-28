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

public class profilUser extends AppCompatActivity {
    SessionManager sessionManager;
    TextView txnamau;
    Button btLogoutu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_user);

        txnamau = (TextView) findViewById(R.id.txtnamauser);
        btLogoutu = (Button) findViewById(R.id.btnlogoutsUser);

        BottomNavigationView bottomNavigationView = findViewById(R.id.penggunaNav);
        bottomNavigationView.setSelectedItemId(R.id.profilesUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homesUser:
                        startActivity(new Intent(profilUser.this, userPage.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesanUser:
                        startActivity(new Intent(profilUser.this, userPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.profilesUser:
                        return true;
                }
                return false;
            }
        });

        sessionManager = new SessionManager(profilUser.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        txnamau.setText(sessionManager.getUserDetail().get(SessionManager.NAMA));

        btLogoutu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutSession();
                movetoLogin();
                stopService(new Intent(getApplicationContext(), LocationServer.class));
            }
        });
    }

    private void movetoLogin() {
        Intent i = new Intent(profilUser.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}