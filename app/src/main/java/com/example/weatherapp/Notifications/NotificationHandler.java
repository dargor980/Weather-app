package com.example.weatherapp.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;

public class NotificationHandler extends ContextWrapper {

    private NotificationManager manager;

    public static final String CHANNEL_HIGH_ID = "1";
    private final String CHANNEL_HIGH_NAME = "HIGH CHANNEL";
    public static final String CHANNEL_LOW_ID = "2";
    private final String CHANNEL_LOW_NAME = "LOW CHANNEL";
    private final int SUMMARY_GROUP_ID = 1001;
    private final String SUMMARY_GROUP_NAME = "GROUPPING_NOTIFICATION";


    public NotificationHandler(Context context) {
        super(context);
        createChannels();
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel highChannel = new NotificationChannel(CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.YELLOW);
            highChannel.setShowBadge(true);
            highChannel.enableVibration(true);
            highChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            highChannel.setSound(defaultSoundUri, null);

            NotificationChannel lowChannel = new NotificationChannel(CHANNEL_LOW_ID, CHANNEL_LOW_NAME, NotificationManager.IMPORTANCE_LOW);

            getManager().createNotificationChannel(highChannel);
            getManager().createNotificationChannel(lowChannel);
        }
    }

    public Notification.Builder createNotification(String title, String message, boolean isHighImportance) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (isHighImportance) {
                return this.createNotificationWithChannel(title, message, CHANNEL_HIGH_ID);
            }
            return this.createNotificationWithChannel(title, message, CHANNEL_LOW_ID);
        }
        return this.createNotificationWithoutChannel(title, message);
    }

    private Notification.Builder createNotificationWithChannel(String title, String message, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification.Action action = new Notification.Action.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_menu_send),
                    "Ver Detalles",
                    pIntent
            ).build();
            return new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pIntent)
                    .setColor(getColor(R.color.design_default_color_on_primary))
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setGroup(SUMMARY_GROUP_NAME)
                    .setAutoCancel(true);
        }
        return null;
    }

    private Notification.Builder createNotificationWithoutChannel(String title, String message) {
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setAutoCancel(true);
    }

    public void publishNotificationSummaryGroup(boolean isHighImportance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = (isHighImportance) ? CHANNEL_HIGH_ID : CHANNEL_LOW_ID;
            Notification summaryNotification = new Notification.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                    .setGroup(SUMMARY_GROUP_NAME)
                    .setGroupSummary(true)
                    .build();
            getManager().notify(SUMMARY_GROUP_ID, summaryNotification);
        }
    }
}

