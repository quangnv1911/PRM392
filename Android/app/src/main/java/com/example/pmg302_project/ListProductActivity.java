package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar; // Correct import
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.service.FavoriteService;
import com.example.pmg302_project.util.RetrofitClientInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListProductActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private Toolbar toolbar;
    OkHttpClient client = new OkHttpClient();
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> cartList = new ArrayList<>();
    private RecyclerView recyclerView;
    String ip = COMMONSTRING.ip;
    private FavoriteService favoriteService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_product);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Nhận loại sản phẩm từ Intent
        String productType = getIntent().getStringExtra("PRODUCT_TYPE");
        if (productType != null) {
            getSupportActionBar().setTitle(productType);
        }

        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteService = RetrofitClientInstance.getFavoriteService();
        // Initialize the ProductAdapter with the favoriteService
        productAdapter = new ProductAdapter(this, this,productList, this, false);
        productAdapter.setFavoriteService(favoriteService); // Set the favorite service
        recyclerView.setAdapter(productAdapter); // Set the adapter

        fetchProduct(productType);
    }

    private void fetchProduct(String productType) {
        String url = "http://"+ip+":8081/api/product?type=" + productType;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Sending request to: " + request.url());
                    Log.d(TAG, "body: " + responseData);
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        runOnUiThread(() -> {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Product product = new Product(
                                            jsonObject.getInt("productId"),
                                            jsonObject.getString("productName"),
                                            jsonObject.getString("description"),
                                            jsonObject.getDouble("price"),
                                            jsonObject.getString("imageLink"),
                                            jsonObject.getString("type"),
                                            jsonObject.getDouble("rate"),
                                            jsonObject.getInt("purchaseCount")
                                    );
                                    productList.add(product);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            productAdapter.notifyDataSetChanged(); // Cập nhật adapter
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onAddToCartClick(Product product, int quantity, String size, String color) {
        cartList = CartPreferences.loadCart(this);
        boolean productExists = false;

        // Check if product already exists in cart
        for (Product cartProduct : cartList) {
            if (cartProduct.getId() == product.getId() && cartProduct.getSize().equals(size)) {
                // Product exists, increase quantity
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                productExists = true;
                break;
            }
        }

        // If product does not exist, add to cart with the specified quantity
        if (!productExists) {
            product.setQuantity(quantity);
            product.setSize(size);
            product.setColor(color);
            cartList.add(product);
        }

        CartPreferences.saveCart(this, cartList); // Save cart to SharedPreferences
        Toast.makeText(this, product.getName() + " added to cart.", Toast.LENGTH_SHORT).show();
    }

}
