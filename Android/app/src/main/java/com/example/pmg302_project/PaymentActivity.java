package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.OrderHistoryAdapter;
import com.example.pmg302_project.adapter.PaymentAdapter;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Coupon;
import com.example.pmg302_project.model.OrderDetail;
import com.example.pmg302_project.model.Orders;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.zalopay.Api.CreateOrder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

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

    private EditText txtCouponPay;
    private Account account;
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    private Coupon coupon;
    public Coupon getCoupon() {
        return coupon;
    }
    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    private double totalPriceUseCoupon;
    public double getTotal() {
        return totalPriceUseCoupon;
    }
    public void setTotal(double totalPriceUseCoupon) {
        this.totalPriceUseCoupon = totalPriceUseCoupon;
    }

    private int totalQuantity;
    public int getTotalQuan() {
        return totalQuantity;
    }
    public void setTotalQuan(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    private Boolean flag;
    public Boolean getFlg() {
        return flag;
    }
    public void setFlg(Boolean flag) {
        this.flag = flag;
    }

    private Integer newQuan;
    public Integer getQuan() {
        return newQuan;
    }
    public void setQuan(Integer newQuan) {
        this.newQuan = newQuan;
    }

    private Button checkoutButton;
    private Button editInfor;
    private Button checkCoupon;
    private TextView txtErrorCoupon;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private String ip = COMMONSTRING.ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        //zaloPay setup
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(553, Environment.SANDBOX);


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

        //Coupon
        txtCouponPay=findViewById(R.id.txtCouponPay);
        txtCouponPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = s.toString();
                // Ví dụ: Kiểm tra độ dài của văn bản
                if (inputText.length() > 0) {
                    handleCoupon();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        checkCoupon=findViewById(R.id.btnCheckCoupon);
        txtErrorCoupon=findViewById(R.id.errorCouponMessage);
        checkCoupon.setOnClickListener(vi->{
            handleCoupon();
            if(!validateCoupon()){
                setCoupon(null);
                updateCartSummary();
            };
        });

        //Checkout
        checkoutButton=findViewById(R.id.paymentButton);
        checkoutButton.setOnClickListener(vie->{
            handleCoupon();
            setFlg(true);
            setQuan(0);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            new Thread(() -> {
                CountDownLatch latch = new CountDownLatch(cartList.size());

                for (Product product : cartList) {
                    new Thread(() -> {
                        int quantity = getQuantityProductSync(product.getId()); // Thực hiện kiểm tra hàng tồn kho đồng bộ
                        mainHandler.post(() -> {
                            Log.d("TagL", quantity + "..." + product.getQuantity());
                            if (quantity < product.getQuantity()) {
                                runOnUiThread(() ->
                                        Toast.makeText(getApplicationContext(), product.getName() + " đã hết hàng!", Toast.LENGTH_SHORT).show()
                                );
                                setFlg(false);
                            }
                            Log.d("Tag1", quantity + " " + product.getQuantity() + " " + getFlg());
                            latch.countDown(); // Đếm ngược latch sau khi kiểm tra xong sản phẩm
                        });
                    }).start();
                }

                // Đợi cho đến khi tất cả các sản phẩm được kiểm tra xong
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Tất cả các kiểm tra đã hoàn tất, chuyển về luồng chính
                mainHandler.post(() -> {
                    if (getFlg()) {
                        Log.d("Tag2", "" + getFlg());
                        if (txtCouponPay.getText().toString().isEmpty()) {
                            createOrder();
                        } else {
                            if (!validateCoupon()) {
                                setCoupon(null);
                                updateCartSummary();
                            } else {
                                createOrder();
                            }
                        }
                    }
                });
            }).start();
        });
        loadAccountLogin();
    }


            //Toast.makeText(getApplicationContext(), ""+getFlg(), Toast.LENGTH_SHORT).show();
            //Thoả mãn điều kiện thì sẽ tạo Order
            //createOrder();
//            if(getFlg()){
//                if(txtCouponPay.getText().toString().isEmpty()){
//                    createOrder();
//                }else{
//                    if(!validateCoupon()){
//                        setCoupon(null);
//                        updateCartSummary();
//                    }else {
//                        createOrder();
//                    }
//                }
//            }





    private void createOrder() {

        Orders orders=new Orders();
        Date da=new Date();
        orders.setAccountId(getAccount().getId());
        orders.setOrderDate(da);
        orders.setStatus(0);
        orders.setTotalPrice(getTotal());
        orders.setTotalQuantity(getTotalQuan());
        orders.setCoupon(getCoupon());


        String url = "http://" + ip + ":8081/api/createOrder";

        // Tạo JSON từ thông tin của Account
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accountid", orders.getAccountId());
            jsonObject.put("couponcode", txtCouponPay.getText());
            jsonObject.put("orderdate", orders.getOrderDate());
            jsonObject.put("status", orders.getStatus());
            jsonObject.put("totalprice", orders.getTotalPrice());
            jsonObject.put("totalquantity", orders.getTotalQuantity());
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(responseData);
                        int id = jsonResponse.getInt("id");
                        if(getCoupon()!=null){
                            getCoupon().setUsageCount(getCoupon().getUsageCount()+1);
                        }
                        orders.setOrderId(id);

                        //Tạo đơn hàng trong database thành công->tạo zalopay
                        CreateOrder orderApi = new CreateOrder();

                        try {
                            Integer price=orders.getTotalPrice().intValue();
                            JSONObject data = orderApi.createOrder(price.toString());
                            String code = data.getString("returncode");
                            //Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                            if (code.equals("1")) {

                                String token= data.getString("zptranstoken");
                                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this,token, "demozpdk://app", new PayOrderListener() {
                                    @Override
                                    public void onPaymentSucceeded(String s, String s1, String s2) {
                                        Intent intent =new Intent(PaymentActivity.this, OrderHistoryActivity.class);
                                        intent.putExtra("status",1);
                                        intent.putExtra("orders",orders);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onPaymentCanceled(String s, String s1) {
                                        Intent intent =new Intent(PaymentActivity.this, OrderHistoryActivity.class);
                                        intent.putExtra("status",0);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Bạn đã hủy thanh toán!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                        Intent intent =new Intent(PaymentActivity.this, OrderHistoryActivity.class);
                                        intent.putExtra("status",0);
                                        startActivity(intent);
                                        Toast.makeText(getApplicationContext(), "Lỗi thanh toán!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        createOrderDetail(orders);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void createOrderDetail(Orders orders) {
        ArrayList<OrderDetail> orderDetailList = new ArrayList();
        for (Product product : cartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(orders);
            orderDetail.setProduct(product);
            orderDetail.setUnitPrice(product.getPrice());
            orderDetail.setQuantity(product.getQuantity());
            orderDetail.setSize(product.getSize());
            orderDetail.setColor(product.getColor());
            orderDetailList.add(orderDetail);
        }
        String url = "http://" + ip + ":8081/api/createOrderDetail";
        JSONArray jsonArray = new JSONArray();
        try {
            for (OrderDetail orderDetail : orderDetailList) {
                JSONObject orderDetailJson = new JSONObject();
                orderDetailJson.put("orderId", orderDetail.getOrder().getOrderId()); // Hoặc các thuộc tính của Order cần thiết
                orderDetailJson.put("productId", orderDetail.getProduct().getId()); // Hoặc các thuộc tính của Product cần thiết
                orderDetailJson.put("unitPrice", orderDetail.getUnitPrice());
                orderDetailJson.put("quantity", orderDetail.getQuantity());
                orderDetailJson.put("size", orderDetail.getSize());
                orderDetailJson.put("color", orderDetail.getColor());
                jsonArray.put(orderDetailJson); // Thêm từng `orderDetailJson` vào `jsonArray`
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSON creation error: " + e.getMessage());
            return;
        }

        // Tạo RequestBody cho POST request
        RequestBody body = RequestBody.create(
                jsonArray.toString(),
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(()->{
                        //Toast.makeText(getApplicationContext(), responseData.toString(), Toast.LENGTH_SHORT).show();
                        if(responseData.toString().equals("Đặt hàng thành công!")){
                            cartList.clear();
                            CartPreferences.saveCart(getApplicationContext(),cartList);
                        }
                    });
                }
            }
        });

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
        runOnUiThread(() -> {
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

            setTotal(totalPrice);
            setTotalQuan(totalQuantity);
        });
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

    private void handleCoupon() {
        String code = String.valueOf(txtCouponPay.getText());
        if (code == null || code.isEmpty()) {
            setCoupon(null);
            return;
        }

        String url = "http://" + ip + ":8081/api/coupondetail?couponcode=" + code;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Yêu cầu thất bại", Toast.LENGTH_SHORT).show();
                });
                setCoupon(null);
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

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date startDate = null;
                        Date endDate = null;

                        try {
                            // Chuyển đổi chuỗi startDate và endDate từ JSONObject thành Date
                            startDate = formatter.parse(jsonObject.getString("startDate"));
                            endDate = formatter.parse(jsonObject.getString("endDate"));
                        } catch (Exception ex) {
                            // Handle exception
                        }

                        Coupon coupon = new Coupon(
                                jsonObject.getInt("id"),
                                jsonObject.getString("couponCode"),
                                jsonObject.getInt("discountValue"),
                                jsonObject.getInt("minOrderValue"),
                                jsonObject.getInt("maxOrderValue"),
                                startDate,
                                endDate,
                                jsonObject.getInt("usageLimit"),
                                jsonObject.getInt("usageCount"),
                                jsonObject.getBoolean("isActive"),
                                jsonObject.getInt("couponType")
                        );
                        setCoupon(coupon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "JSON parsing error: " + e.getMessage());
                        setCoupon(null);
                    }
                } else {
                    Log.d(TAG, "Request not successful. Code: " + response.code());
                    setCoupon(null);
                }
            }
        });

    }

    private boolean validateCoupon() {
        Date d = new Date();
        if (getCoupon() == null) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Không có mã giảm giá", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Không có mã giảm giá");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        // Check if the coupon is expired
        if (d.after(getCoupon().getEndDate())) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Mã giảm giá quá hạn", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Mã giảm giá quá hạn");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        // Check if the coupon has started
        if (d.before(getCoupon().getStartDate())) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Mã giảm giá chưa mở", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Mã giảm giá chưa mở");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        // Check if the coupon is active
        if (!getCoupon().getActive()) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Mã giảm giá không kích hoạt", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Mã giảm giá không kích hoạt");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        // Check max and min order values
        double totalPrice = cartList.stream().mapToDouble(product -> product.getQuantity() * product.getPrice()).sum();

        if (getCoupon().getMaxOrderValue() < totalPrice) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Tổng tiền vượt quá yêu cầu áp dụng giảm giá", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Tổng tiền vượt quá yêu cầu áp dụng giảm giá");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        if (getCoupon().getMinOrderValue() > totalPrice) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Tổng tiền không đạt yêu cầu áp dụng giảm giá", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Tổng tiền không đạt yêu cầu áp dụng giảm giá");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        // Check usage limit
        if (getCoupon().getUsageCount() >= getCoupon().getUsageLimit()) {
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Mã giảm giá đã hết lượt sử dụng", Toast.LENGTH_SHORT).show();
                errorCoupon.setText("Mã giảm giá đã hết lượt sử dụng");
                errorCoupon.setVisibility(View.VISIBLE);
            });
            return false;
        }

        String discountType = getCoupon().getCouponType() == 0 ? "%" : "VNĐ";
        runOnUiThread(() -> {
            //Toast.makeText(getApplicationContext(), "Thành công! Bạn được giảm " + coupon.getDiscountValue() + " " + discountType, Toast.LENGTH_SHORT).show();
            errorCoupon.setText("Thành công! Bạn được giảm " + getCoupon().getDiscountValue() + " " + discountType);
            errorCoupon.setVisibility(View.VISIBLE);
        });

        // Apply discount
        if (getCoupon().getCouponType() == 0) {
            totalPrice -= totalPrice * getCoupon().getDiscountValue() / 100;
        } else {
            totalPrice -= getCoupon().getDiscountValue();
        }
        setTotal(totalPrice);
        runOnUiThread(() -> {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(getTotal());
            totalPriceTextView.setText("Tổng giá tiền: " + formattedPrice + " VNĐ");
        });
        return true;
    }

    private int getQuantityProductSync(int productId ) {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] quantity = {0};
        String url = "http://" + ip + ":8081/api/productById?id=" + productId;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Sending request to: " + request.url());
                    Log.d(TAG, "body: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        int stockQuantity = jsonObject.getInt("stockQuantity");
                        quantity[0] = stockQuantity;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return quantity[0];

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

}

