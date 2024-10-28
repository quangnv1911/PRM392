package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.PaymentAdapter;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Product;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPayment;
    private PaymentAdapter paymentAdapter;
    private List<Product> cartList;
    private TextView totalQuantityTextView;
    private TextView totalPriceTextView;
    private TextView errorCoupon;

    private TextView userPayFullName;
    private TextView userPayPhone;
    private TextView userPayAddress;
    public Account account;

    private Button payment;
    private Button checkCoupon;
    private TextView txtErrorCoupon;

    OkHttpClient client = new OkHttpClient();
    private String ip = COMMONSTRING.ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        recyclerViewPayment = findViewById(R.id.recyclerViewPayment);
        recyclerViewPayment.setLayoutManager(new LinearLayoutManager(this));

        totalQuantityTextView = findViewById(R.id.totalQuan);
        totalPriceTextView = findViewById(R.id.totalPri);
        errorCoupon = findViewById(R.id.errorCouponMessage);

        userPayFullName = findViewById(R.id.userPayFullName);
        userPayPhone = findViewById(R.id.userPayPhone);
        userPayAddress = findViewById(R.id.userPayAddress);

        // Fetch cart data from CartPreferences
        cartList = CartPreferences.loadCart(this);
        paymentAdapter = new PaymentAdapter(this, cartList);
        recyclerViewPayment.setAdapter(paymentAdapter);

        payment=findViewById(R.id.checkoutButton);
        checkCoupon=findViewById(R.id.btnCheckCoupon);
        txtErrorCoupon=findViewById(R.id.errorCouponMessage);

        loadAccountLogin();
    }

    private void loadAccountLogin() {
        String username = InMemoryStorage.get("username");
        String url = "http://" + ip + ":8081/api/account?username=" + username;

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
                        JSONObject jsonObject = new JSONObject(responseData);
                        Account account = new Account(
                                jsonObject.getInt("accountId"),
                                jsonObject.getString("fullname"),
                                jsonObject.getString("phone"),
                                jsonObject.getString("address")
                        );

                        // Update UI with account data on the main thread
                        runOnUiThread(() -> {
                            userPayFullName.setText("Tên của bạn: "+account.getFullname());
                            userPayPhone.setText("Số điện thoại: "+account.getPhone());
                            userPayAddress.setText("Địa chỉ: "+account.getAddress());
                            updateCartSummary();
                        });

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

    public void updateCartSummary() {
        int totalQuantity = 0;
        double totalPrice = 0.0;

        for (Product product : cartList) {
            totalQuantity += product.getQuantity();
            totalPrice += product.getQuantity() * product.getPrice();
        }

        totalQuantityTextView.setText("Total Quantity: " + totalQuantity);
        totalPriceTextView.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }
}

