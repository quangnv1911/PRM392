package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.adapter.OrderHistoryAdapter;
import com.example.pmg302_project.model.Orders;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.service.ProductService;
import com.example.pmg302_project.util.RetrofitClientInstance;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
    private NavigationView navigationView;
    private List<Orders> ordersList = new ArrayList<>();
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private String ip = COMMONSTRING.ip;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        toolbar = findViewById(R.id.toolbar_homepage_order_his);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.draw_order_his);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            CharSequence title = item.getTitle();
            if (title.equals("Profile")) {
                Intent profileIntent = new Intent(OrderHistoryActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            } else if (title.equals("Settings")) {
                Toast.makeText(OrderHistoryActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
            } else if (title.equals("Logout")) {
                InMemoryStorage.clear();
                Intent intent3 = new Intent(OrderHistoryActivity.this, HomePageActivity.class);
                startActivity(intent3);
                navigationView.getMenu().clear(); // Clear the current menu
            } else if (title.equals("About us")) {
                Intent intent = new Intent(OrderHistoryActivity.this, LandingPageActivity.class);
                startActivity(intent);
            } else if (title.equals("Login")) {
                Intent intent2 = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(intent2);
            }else if (title.equals("Order History")) {
                Intent intent2 = new Intent(OrderHistoryActivity.this, OrderHistoryActivity.class);
                startActivity(intent2);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_order);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                // Handle home action
                intent = new Intent(this, HomePageActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_order) {
                // Handle order action
                intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_map) {
                // Handle map action
                intent = new Intent(this, LandingPageActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

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
                int status=getIntent().getIntExtra("status",0);
                Orders order = (Orders) getIntent().getSerializableExtra("orders");
                if(status!=0){
                    changeStatus(order,1);
                }
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
                            runOnUiThread(()->{
                                orderHisAdapter.notifyDataSetChanged();
                            });

                        });
                    }

                }
            });
    }
    private void changeStatus(Orders orders, int status){
        final CountDownLatch latch = new CountDownLatch(1);
        String url = "http://" + ip + ":8081/api/changeOrderStatus";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orders.getOrderId());
            jsonObject.put("statusId", status);
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
                latch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    orders.setStatus(1);
                    ordersList.get(0).setStatus(1);
                        runOnUiThread(()->{
                            orderHisAdapter.notifyDataSetChanged();
                        });
                    latch.countDown();
                }

            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                        runOnUiThread(()->{
                            orderHisAdapter.notifyDataSetChanged();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_profile) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.action_search) {
            showSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Product");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String productName = input.getText().toString();
            searchProduct(productName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void searchProduct(String productName) {
        ProductService productService = RetrofitClientInstance.getProductService();
        retrofit2.Call<List<Product>> call = productService.searchProducts(productName);

        call.enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    // Handle the product list (e.g., pass it to ListProductActivity)
                    Intent intent = new Intent(OrderHistoryActivity.this, ListProductActivity.class);
                    intent.putExtra("PRODUCT_LIST", new Gson().toJson(products));
                    startActivity(intent);
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Search failed", t.getMessage());

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProduct(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
}
