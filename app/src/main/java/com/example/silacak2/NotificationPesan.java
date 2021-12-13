package com.example.silacak2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class NotificationPesan extends Service {
    SessionManager sessionManager;
    private static int count = 0;
    URLServer serv = new URLServer();

    private void notifyServer(String purpose, String pesan) {
        sessionManager = new SessionManager(NotificationPesan.this);
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
                                    showNotificationPerintah(getString(R.string.app_name), pesan);
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

    private void showNotificationPerintah(String title, String contents) {
        String channel_id = "my-pesan-notification";

        // Create Custom Notification
        String nama = sessionManager.getUserDetail().get(SessionManager.NAMA);
        Intent resultIntent = new Intent(getApplicationContext(),anggotaPesanTampil.class);
        resultIntent.putExtra("nama", nama);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channel_id);
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

        notificationManager.notify(count, builder.build());
        count++;

        notifyServer("done","kosong");

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifyServer("done","kosong");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
