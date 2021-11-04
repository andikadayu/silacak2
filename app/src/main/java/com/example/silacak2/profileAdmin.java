package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
public class profileAdmin extends AppCompatActivity {
    SessionManager sessionManager;
    TextView txnama;
    Button btLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        txnama = (TextView) findViewById(R.id.txtnamaadmin);
        btLogout = (Button) findViewById(R.id.btnlogouts);

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminNav);
        //bottomNavigationView.setSelectedItemId(R.id.profiles);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.profiles:
//                        return true;
                    case R.id.homes:
                        startActivity(new Intent(profileAdmin.this, adminPageNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listuser:
                        startActivity(new Intent(profileAdmin.this, adminListUser.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.listanggota:
                        startActivity(new Intent(profileAdmin.this, adminListAnggota.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.newUser:
                        startActivity(new Intent(profileAdmin.this, adminUserNew.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.pesan:
                        startActivity(new Intent(profileAdmin.this, adminPesan.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        sessionManager = new SessionManager(profileAdmin.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        txnama.setText(sessionManager.getUserDetail().get(SessionManager.NAMA));

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutSession();
                movetoLogin();
                stopService(new Intent(getApplicationContext(), LocationServer.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_admin, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.oProfileAdmin){
            startActivity(new Intent(this, profileAdmin.class));
        }
//        if(item.getItemId()==R.id.oListPerintah){
//            startActivity(new Intent(this,adminListPerintah.class));
//        }
        if(item.getItemId()==R.id.oLokasiUser){
            startActivity(new Intent(this,adminLokasiAll.class));
        }
        return true;
    }
    private void movetoLogin() {

        Intent i = new Intent(profileAdmin.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}