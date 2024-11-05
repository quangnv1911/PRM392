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
import com.example.pmg302_project.adapter.PaymentAdapter;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPayment;
    private PaymentAdapter orderHisAdapter;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
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

        toolbar = findViewById(R.id.toolbar_order_detail_his);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.draw_order_detail_his);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            CharSequence title = item.getTitle();
            if (title.equals("Profile")) {
                Intent profileIntent = new Intent(OrderDetailActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            } else if (title.equals("Settings")) {
                Toast.makeText(OrderDetailActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
            } else if (title.equals("Logout")) {
                InMemoryStorage.clear();
                Intent intent3 = new Intent(OrderDetailActivity.this, HomePageActivity.class);
                startActivity(intent3);
                navigationView.getMenu().clear(); // Clear the current menu
            } else if (title.equals("About us")) {
                Intent intent = new Intent(OrderDetailActivity.this, LandingPageActivity.class);
                startActivity(intent);
            } else if (title.equals("Login")) {
                Intent intent2 = new Intent(OrderDetailActivity.this, MainActivity.class);
                startActivity(intent2);
            }else if (title.equals("Order History")) {
                Intent intent2 = new Intent(OrderDetailActivity.this, OrderHistoryActivity.class);
                startActivity(intent2);
            }
            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView_order_detail);
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
                    Intent intent = new Intent(OrderDetailActivity.this, ListProductActivity.class);
                    intent.putExtra("PRODUCT_LIST", new Gson().toJson(products));
                    startActivity(intent);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
