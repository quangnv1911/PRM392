package com.example.pmg302_project.service;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.pmg302_project.OrderHistoryActivity;
import com.example.pmg302_project.model.Orders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransferCheckService extends Service {
    private static final long DELAY_SECONDS = 10 * 1000; // 10 seconds in milliseconds
    private static final long MAX_RUNTIME = 2 * 60 * 1000; // 10 seconds max runtime
    private Handler handler;
    private Runnable timeoutTask;
    private Runnable checkHistoryTask;
    private Orders orders;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Run", "Service started");

        handler = new Handler();

        // Schedule the timeout task to stop the service after MAX_RUNTIME
        timeoutTask = new Runnable() {
            @Override
            public void run() {
                Log.d("TransferCheckService", "Service timed out, stopping service...");
                launchNewActivity(orders, 0);  // Launch activity with status = 0 (failed)
                notifyOriginalActivityToClose();
                stopSelf();  // Stop the service
            }
        };
        handler.postDelayed(timeoutTask, MAX_RUNTIME);

        // Schedule the repeating check task
        checkHistoryTask = new Runnable() {
            @Override
            public void run() {
                Log.d("TransferCheckService", "Service check...");
                checkHistory(orders.getOrderId());
                handler.postDelayed(this, DELAY_SECONDS);  // Schedule the next check
            }
        };
        handler.post(checkHistoryTask);  // Start the repeating check task
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders = (Orders) intent.getSerializableExtra("orders");
        return START_STICKY;
    }

    private void checkHistory(int orderCode) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://oauth.casso.vn/v2/transactions")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Apikey AK_CS.7049fbe09cc211ef8a02890bf6befcfe.MnfHLYT9P2zB1VTnt9090gaQPsvmeRcGE4cILpgzkFith9LNaFGMjaZ2YCYgM6tx5Xec3bub")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("tag",responseData);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject dataObject = jsonResponse.getJSONObject("data");
                        JSONArray recordsArray = dataObject.getJSONArray("records");
                        for (int i = 0; i < recordsArray.length(); i++) {
                            JSONObject record = recordsArray.getJSONObject(i);
                            String description = record.getString("description");
                            // Check if description matches the order code
                            if (description.equals(String.valueOf(orderCode))) {
                                Log.d(TAG, "Transaction found for order code: " + orderCode);

                                // Broadcast the result back to the activity (optional)
                                //Intent intent = new Intent( OrderHistoryActivity.class);
                                launchNewActivity(orders,1);
                                notifyOriginalActivityToClose();
                                stopSelf();
                                break;
                            }
                        }
                    }catch (Exception e){
                        stopSelf(); // Stop the service if a matching transaction is found
                    }


                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);  // Remove all callbacks
        }
        Log.d("TransferCheckService", "Service destroyed");
    }

    private void launchNewActivity(Orders orders, int status) {
        Intent intent = new Intent(this, OrderHistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("orders", orders);
        intent.putExtra("status", status);

        // Show toast message based on the status
        new Handler(Looper.getMainLooper()).post(() -> {
            if (status == 1) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Giao dịch thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(intent);
    }

    private void notifyOriginalActivityToClose() {
        Intent closeIntent = new Intent("com.example.pmg302_project.ACTION_CLOSE_ORIGINAL_ACTIVITY");
        sendBroadcast(closeIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
