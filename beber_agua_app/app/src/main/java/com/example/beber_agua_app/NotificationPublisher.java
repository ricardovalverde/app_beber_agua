package com.example.beber_agua_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationPublisher extends BroadcastReceiver {
    public static final String KEY_NOTIFICATION = null;
    public static final String NOTIFICATION_ID = null;


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        String msg = intent.getStringExtra(KEY_NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = getNotification(msg, context, notificationManager, pIntent);
        notificationManager.notify(id, notification);
    }

    private Notification getNotification(String content, Context context, NotificationManager notificationManager, PendingIntent pIntent) {

        Notification.Builder builder = new Notification.Builder(context.getApplicationContext())
                .setContentText(content)
                .setContentIntent(pIntent)
                .setContentTitle("Beber Água")
                .setTicker("Alerta")
                .setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.icon_water_bottle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "YOUR CHANNEL ID";
            NotificationChannel channel = new NotificationChannel(channelID, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }
        return builder.build();
    }
}
