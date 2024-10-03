package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePageActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private Toolbar toolbar;
    private ViewFlipper viewFlipper;
    private OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerViewTopProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> cartList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    String ip = COMMONSTRING.ip;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage); // Đảm bảo bạn có layout cho HomePageActivity
        FirebaseApp.initializeApp(this);
        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        viewFlipper = findViewById(R.id.viewFlipper);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up navigation item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case 1000049:
                    Toast.makeText(HomePageActivity.this, "Profile selected", Toast.LENGTH_SHORT).show();
                    break;
                case 1000000:
                    Toast.makeText(HomePageActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                    break;
                case 1000066:
                    Toast.makeText(HomePageActivity.this, "Logout selected", Toast.LENGTH_SHORT).show();
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        fetchImageUrls();
        setupButtonListeners();

        recyclerViewTopProducts = findViewById(R.id.recyclerViewTopProducts);
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));

        productAdapter = new ProductAdapter(this, productList, this, false);
        recyclerViewTopProducts.setAdapter(productAdapter);

        fetchTopProducts();

        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(view -> openChatBubble());
    }


    private void openChatBubble() {
        // Code to open the chat bubble
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void fetchTopProducts() {
        String url = "http://" + ip + ":8081/api/top";

        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d(TAG, "Sending request to: " + request.url());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    try {
                        Log.d(TAG, "Sending request to: " + request.url());
                        Log.d(TAG, "body: " + responseData);
                        JSONArray jsonArray = new JSONArray(responseData);
                        runOnUiThread(() -> {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Product product = new Product(
                                            jsonObject.getInt("id"),
                                            jsonObject.getString("name"),
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
                            productAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupButtonListeners() {
        Button saleButton = findViewById(R.id.sale);
        saleButton.setOnClickListener(view -> openListProductActivity("Sale"));
        Button jeanButton = findViewById(R.id.jean);
        jeanButton.setOnClickListener(view -> openListProductActivity("Jean"));
        Button suitButton = findViewById(R.id.suit);
        suitButton.setOnClickListener(view -> openListProductActivity("Suit"));
        Button shoeButton = findViewById(R.id.shoe);
        shoeButton.setOnClickListener(view -> openListProductActivity("Shoe"));
        Button vestButton = findViewById(R.id.vest);
        vestButton.setOnClickListener(view -> openListProductActivity("Vest"));
        Button shirtButton = findViewById(R.id.t_shirt);
        shirtButton.setOnClickListener(view -> openListProductActivity("T-Shirt"));
        Button watchButton = findViewById(R.id.watch);
        watchButton.setOnClickListener(view -> openListProductActivity("Watch"));
        Button hatButton = findViewById(R.id.hat);
        hatButton.setOnClickListener(view -> openListProductActivity("Hat"));
        Button beltButton = findViewById(R.id.belt);
        beltButton.setOnClickListener(view -> openListProductActivity("Belt"));
        Button itemButton = findViewById(R.id.item);
        itemButton.setOnClickListener(view -> openListProductActivity("Item"));
        Button sandalButton = findViewById(R.id.sandal);
        sandalButton.setOnClickListener(view -> openListProductActivity("Sandal"));
        Button unisexButton = findViewById(R.id.unisex);
        unisexButton.setOnClickListener(view -> openListProductActivity("Unisex"));

    }

    private void openListProductActivity(String productType) {
        Intent intent = new Intent(this, ListProductActivity.class);
        intent.putExtra("PRODUCT_TYPE", productType);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 2131296314) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_profile) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void fetchImageUrls() {
        String url = "http://" + ip + ":8081/api/flipper";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

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
                                    String imageUrl = jsonObject.getString("imageLink");
                                    ImageView imageView = new ImageView(HomePageActivity.this);
                                    Picasso.get().load(imageUrl).into(imageView);
                                    viewFlipper.addView(imageView);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            setupViewFlipper();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupViewFlipper() {
        viewFlipper.setFlipInterval(3000); // 3 seconds
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
    }

    @Override
    public void onAddToCartClick(Product product, int quantity, String size, String color) {
        cartList = CartPreferences.loadCart(this);
        boolean productExists = false;

        // Check if product already exists in cart
        for (Product cartProduct : cartList) {
            if (cartProduct.getId() == product.getId() && size != null && size.equals(cartProduct.getSize()) && color != null && color.equals(cartProduct.getColor())) {
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
