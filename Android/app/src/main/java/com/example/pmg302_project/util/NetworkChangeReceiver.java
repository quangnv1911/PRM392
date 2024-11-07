package com.example.pmg302_project.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                // Show a toast or handle lost connection
                Toast.makeText(context, "Lost Wi-Fi connection", Toast.LENGTH_SHORT).show();
            } else {
                // Optional: Reconnect or refresh data
//                Toast.makeText(context, "Connected to Wi-Fi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
