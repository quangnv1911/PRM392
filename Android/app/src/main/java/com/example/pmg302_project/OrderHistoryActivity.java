package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.adapter.OrderHistoryAdapter;
import com.example.pmg302_project.model.Orders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnOrderClickListener{
    private RecyclerView recyclerViewPayment;
    private OrderHistoryAdapter orderHisAdapter;

    private List<Orders> ordersList = new ArrayList<>();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private String ip = COMMONSTRING.ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);


        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // Công việc trên luồng phụ
            loadOrderList();

            // Chuyển về luồng chính
            mainHandler.post(() -> {
                recyclerViewPayment = findViewById(R.id.recyclerViewOrderHis);
                recyclerViewPayment.setLayoutManager(new LinearLayoutManager(this));
                orderHisAdapter = new OrderHistoryAdapter(this, ordersList,this);
                recyclerViewPayment.setAdapter(orderHisAdapter);
            });
        }).start();



    }

    @Override
    public void onCancelOrderClick(Orders order, String status) {
        String[] statusArray = getResources().getStringArray(R.array.statusOrder);
            int statusId = -1;
            for (int i = 0; i < statusArray.length; i++) {
                if (statusArray[i].equals(status)) {
                    statusId = i;
                    break;
                }
            }
            String url = "http://" + ip + ":8081/api/changeOrderStatus";

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("orderId", order.getOrderId());
                jsonObject.put("statusId", statusId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Tạo RequestBody từ JSON body
            RequestBody body = RequestBody.create(
                    jsonObject.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        runOnUiThread(()->{
                            String[] statusArray = getResources().getStringArray(R.array.statusOrder);
                            int statusId = -1;
                            for (int i = 0; i < statusArray.length; i++) {
                                if (statusArray[i].equals(status)) {
                                    statusId = i;
                                    break;
                                }
                            }
                                order.setStatus(statusId);
                                orderHisAdapter.notifyDataSetChanged();
                        });
                    }

                }
            });
    }

    @Override
    public void onDetailOrderClick(Orders order) {
        Intent intent = new Intent(OrderHistoryActivity.this,OrderDetailActivity.class);
        intent.putExtra("orderId", order.getOrderId());
        startActivity(intent);
    }
    private void loadOrderList() {
        String username = InMemoryStorage.get("username");
        String url = "http://" + ip + ":8081/api/getOrderHistoryByAccId?username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed: " + e.getMessage());
                // Handle error, maybe show a Toast or log the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Response code: " + response.code());
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Sending request to: " + request.url());
                    Log.d(TAG, "body: " + responseData);

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Orders orders = new Orders();
                            orders.setOrderId(jsonObject.getInt("orderId"));
                            orders.setTotalQuantity(jsonObject.getInt("totalQuantity"));
                            orders.setTotalPrice(jsonObject.getDouble("totalPrice"));
                            orders.setStatus(jsonObject.getInt("status"));
                            //orders.setCoupon(jsonObject.getJSONObject("coupon"));
                            ordersList.add(orders);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "Request not successful. Code: " + response.code());
                }
            }
        });
    }
}
