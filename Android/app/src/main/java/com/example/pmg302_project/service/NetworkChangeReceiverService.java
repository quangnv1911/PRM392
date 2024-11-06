package com.example.pmg302_project.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.pmg302_project.HomePageActivity;
import com.example.pmg302_project.MainActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.receiver.NetworkChangeReceiver;

public class NetworkChangeReceiverService extends Service {

    private static final long TEN_MINUTES = 10 * 60 * 1000; // 10 minutes in milliseconds
    private static final String CHANNEL_ID = "NetworkChange_Channel";
    private NetworkChangeReceiver networkChangeReceiver;
    private Handler handler;
    private Runnable stopServiceRunnable;


    @Override
    public void onCreate() {
        super.onCreate();
        networkChangeReceiver = new NetworkChangeReceiver();

        // Register BroadcastReceiver for network changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        // Start foreground service with notification
        startForegroundServiceWithNotification();

        // Schedule stopping the service after 10 minutes
        handler = new Handler();
        stopServiceRunnable = this::stopSelf;  // Stop service after 10 minutes
        handler.postDelayed(stopServiceRunnable, TEN_MINUTES);
    }

    private void startForegroundServiceWithNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        Intent notificationIntent = new Intent(this, HomePageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Network Monitor Service")
                .setContentText("Monitoring network connectivity changes.")
                .setSmallIcon(R.mipmap.ic_launcher) // Ensure this icon exists in your resources
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .build();

        // Add the foreground service type for connected devices if the SDK version is Android 10 (Q) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(1, notification);
        }
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Network Change Monitoring",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Channel for network monitoring service");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;  // Service should restart if killed by system
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        if (handler != null) {
            handler.removeCallbacks(stopServiceRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
