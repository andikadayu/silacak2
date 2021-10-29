package com.example.silacak2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotificationBerkumpul extends Service {

    URLServer serv = new URLServer();
    private static int count = 0;
    Handler handler = new Handler();
    Runnable runnable;

    private  void getBerkumpul(){
        AndroidNetworking.post(serv.getNotifBerkumpul())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                JSONArray ja = response.getJSONArray("notify");
                                for (int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    String jumlah = jo.getString("jumlah");
                                    double lat = Double.parseDouble(jo.getString("latitude"));
                                    double lng = Double.parseDouble(jo.getString("longitude"));
                                    create_notif(jumlah,lat,lng);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void create_notif(String jumlah,double lat,double lng){

        String channelid = "berkumpul_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(),adminPageNew.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("latitude",lat);
        resultIntent.putExtra("longitude",lng);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelid
        );
        builder.setSmallIcon(R.mipmap.ic_presisi);
        builder.setContentTitle("Warning");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Terdapat "+jumlah+" anggota yang sedang berkumpul di "+lat+","+lng);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.direction_arrive,"Go To Map",pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelid) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelid,
                        "Notification Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This Channel is used by Notification service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(count, builder.build());
        count++;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getBerkumpul();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
