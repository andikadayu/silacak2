package com.example.silacak2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class notConfirmPage extends AppCompatActivity {
    SessionManager sessionManager;
    TextView txnama;
    Button btLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_confirm_page);

        txnama = (TextView) findViewById(R.id.txtnama);
        btLogout = (Button) findViewById(R.id.out);

        sessionManager = new SessionManager(notConfirmPage.this);
        if (!sessionManager.isLoggedIn()) {
            movetoLogin();
        }

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutSession();
                movetoLogin();
            }
        });
    }
    private void movetoLogin() {
        Intent i = new Intent(notConfirmPage.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }
}