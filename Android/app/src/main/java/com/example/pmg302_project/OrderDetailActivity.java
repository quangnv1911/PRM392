package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.adapter.OrderHistoryAdapter;
import com.example.pmg302_project.adapter.PaymentAdapter;
import com.example.pmg302_project.model.Orders;
import com.example.pmg302_project.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPayment;
    private PaymentAdapter orderHisAdapter;

    private List<Product> orderDetailList = new ArrayList<>();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private String ip = COMMONSTRING.ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_history);
        int orderid=getIntent().getIntExtra("orderId",-1);

        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            // Công việc trên luồng phụ
            loadOrderDetailList(orderid);

            // Chuyển về luồng chính
            mainHandler.post(() -> {
                recyclerViewPayment = findViewById(R.id.recyclerViewOrderDetailHis);
                recyclerViewPayment.setLayoutManager(new LinearLayoutManager(this));
                orderHisAdapter = new PaymentAdapter(this, orderDetailList);
                recyclerViewPayment.setAdapter(orderHisAdapter);
            });
        }).start();

    }

    private void loadOrderDetailList( int orderid) {
        String url = "http://" + ip + ":8081/api/getOrderDetailByOrderId?orderId=" + orderid;

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
                            Product orders = new Product();
                            orders.setId(jsonObject.getInt("id"));
                            orders.setSize(jsonObject.getString("size"));
                            orders.setColor(jsonObject.getString("color"));
                            orders.setPrice(jsonObject.getDouble("unitPrice"));
                            orders.setQuantity(jsonObject.getInt("quantity"));

                            JSONObject product = jsonArray.getJSONObject(i).getJSONObject("product");
                            orders.setName(product.getString("productName"));
                            orders.setImageLink(product.getString("imageLink"));
                            //orders.setCoupon(jsonObject.getJSONObject("coupon"));
                            orderDetailList.add(orders);
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
