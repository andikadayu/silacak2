package com.example.silacak2;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderYesterday extends Service{
    URLServer server = new URLServer();
    private static final String channel_id = "no-absen-notify";

    private void isShown(){
            String today = (String) DateFormat.format(
                    "HH:mm:ss", new Date());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try{
            Date todays = sdf.parse(today);
            Date befores = sdf.parse("06:00:00");
            Date expireds = sdf.parse("09:00:00");
            assert todays != null;
            if(todays.after(befores)){
                if(todays.before(expireds)){
                    getReminder();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void getReminder(){
        AndroidNetworking.post(server.server+"/absensi/getReminder.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String msg = response.getString("msg");
                            createNotification(msg);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void createNotification(String msg){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(),RekapAbsensi.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channel_id
        );
        builder.setSmallIcon(R.mipmap.ic_presisi);
        builder.setContentTitle("Pemberitahuan Rekap Absensi Kemarin");
        builder.setContentText(msg);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channel_id) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channel_id,
                        "Absen Notification Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This Channel is used by Notification service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(66666,builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isShown();
        return super.onStartCommand(intent, flags, startId);
    }
}
