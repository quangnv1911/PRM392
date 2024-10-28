package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.PaymentAdapter;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Product;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    private TextView txtCouponPay;
    private Account account;
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    private Button payment;
    private Button editInfor;
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

        //Coupon
        txtCouponPay=findViewById(R.id.txtCouponPay);
        checkCoupon=findViewById(R.id.btnCheckCoupon);
        txtErrorCoupon=findViewById(R.id.errorCouponMessage);
        editInfor=findViewById(R.id.editInfor);

        //hiển thị dialog edit thông tin người mua hàng
        editInfor.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_infomation, null);
            builder.setView(dialogView);

            TextView userName = dialogView.findViewById(R.id.editName);
            TextView userPhone = dialogView.findViewById(R.id.editPhone);
            TextView userAddress = dialogView.findViewById(R.id.editAddress);
            Button btnEdit=dialogView.findViewById(R.id.btnEdit);

            //update thông tin người mua hàng
            btnEdit.setOnClickListener(view->{
                getAccount().setFullname(String.valueOf(userName.getText()));
                getAccount().setPhone(String.valueOf(userPhone.getText()));
                getAccount().setAddress(String.valueOf(userAddress.getText()));
                updateAccount(getAccount());
            });

            userName.setText(getAccount().getFullname());
            userPhone.setText(getAccount().getPhone());
            userAddress.setText(getAccount().getAddress());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
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
                        setAccount(account);
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

        totalQuantityTextView.setText("Tổng số lượng: " + totalQuantity);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(totalPrice);
        totalPriceTextView.setText("Tổng giá tiền: " + formattedPrice+" VNĐ");
    }

    private void updateAccount(Account account) {
        String url = "http://" + ip + ":8081/api/updateAccountPayment";

        // Tạo JSON từ thông tin của Account
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountId", account.getId());
            jsonObject.put("fullname", account.getFullname());
            jsonObject.put("phone", account.getPhone());
            jsonObject.put("address", account.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON creation error: " + e.getMessage());
            return;
        }

        // Tạo RequestBody cho POST request
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
                Log.d(TAG, "Request failed: " + e.getMessage());
                // Xử lý lỗi khi request thất bại
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Update successful. Code: " + response.code());

                    // Thông báo thành công trên giao diện
                    runOnUiThread(() -> {
                            userPayFullName.setText("Tên của bạn: "+getAccount().getFullname());
                            userPayPhone.setText("Số điện thoại: "+getAccount().getPhone());
                            userPayAddress.setText("Địa chỉ: "+getAccount().getAddress());
                            updateCartSummary();
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.d(TAG, "Update not successful. Code: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Cập nhật không thành công. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void addOrder() {
        String url = "http://" + ip + ":8081/api/addOrder";

        // Tạo JSON từ thông tin của Account
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountId", account.getId());
            jsonObject.put("fullname", account.getFullname());
            jsonObject.put("phone", account.getPhone());
            jsonObject.put("address", account.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON creation error: " + e.getMessage());
            return;
        }

        // Tạo RequestBody cho POST request
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
                Log.d(TAG, "Request failed: " + e.getMessage());
                // Xử lý lỗi khi request thất bại
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Update successful. Code: " + response.code());

                    // Thông báo thành công trên giao diện
                    runOnUiThread(() -> {
                        userPayFullName.setText("Tên của bạn: "+getAccount().getFullname());
                        userPayPhone.setText("Số điện thoại: "+getAccount().getPhone());
                        userPayAddress.setText("Địa chỉ: "+getAccount().getAddress());
                        updateCartSummary();
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.d(TAG, "Update not successful. Code: " + response.code());
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Cập nhật không thành công. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}

