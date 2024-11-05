package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.service.ProductService;
import com.example.pmg302_project.util.RetrofitClientInstance;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
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
        setContentView(R.layout.homepage);
        FirebaseApp.initializeApp(this);

        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        viewFlipper = findViewById(R.id.viewFlipper);
        toolbar = findViewById(R.id.toolbar_hompage);
        setSupportActionBar(toolbar);

        String username = InMemoryStorage.get("username");
        navigationView.getMenu().clear(); // Clear the current menu

        if (username == null || username.isEmpty()) {
            // If username is not available, show About us and Login buttons
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu_guest);
        } else {
            // If username is available, show the current drawer menu
            navigationView.inflateMenu(R.menu.drawer_menu);
        }

        // Set up navigation item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            CharSequence title = item.getTitle();
            if (title.equals("Profile")) {
                Intent profileIntent = new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            } else if (title.equals("Settings")) {
                Toast.makeText(HomePageActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
            } else if (title.equals("Logout")) {
                InMemoryStorage.clear();
                Intent intent3 = new Intent(HomePageActivity.this, HomePageActivity.class);
                startActivity(intent3);
                navigationView.getMenu().clear(); // Clear the current menu
            } else if (title.equals("About us")) {
                Intent intent = new Intent(HomePageActivity.this, LandingPageActivity.class);
                startActivity(intent);
            } else if (title.equals("Login")) {
                Intent intent2 = new Intent(HomePageActivity.this, MainActivity.class);
                startActivity(intent2);
            }else if (title.equals("Order History")) {
                Intent intent2 = new Intent(HomePageActivity.this, OrderHistoryActivity.class);
                startActivity(intent2);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                // Handle home action
                intent = new Intent(this, HomePageActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_order) {
                // Handle order action
                intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_map) {
                // Handle map action
                intent = new Intent(this, LandingPageActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        fetchImageUrls();
        fetchButtonData(); // Fetch button data from API

        recyclerViewTopProducts = findViewById(R.id.recyclerViewTopProducts);
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));

        productAdapter = new ProductAdapter(this,this, productList, this, false);
        recyclerViewTopProducts.setAdapter(productAdapter);

        fetchTopProducts();

        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(view -> openChatBubble());
    }

    private void fetchButtonData() {
        String url = "http://" + ip + ":8081/api/buttons"; // Replace with your actual API endpoint

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
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        runOnUiThread(() -> {
                            try {
                                createButtons(jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void createButtons(JSONArray jsonArray) throws JSONException {
        HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontal_scroll_view);
        LinearLayout containerLayout = new LinearLayout(this);
        containerLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout firstRow = new LinearLayout(this);
        firstRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout secondRow = new LinearLayout(this);
        secondRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String buttonText = jsonObject.getString("text");
            String buttonType = jsonObject.getString("type");

            Button button = new Button(this);
            button.setText(buttonText);
            button.setTextColor(Color.WHITE); // Set text color to white
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    80 // height in dp
            );
            params.setMargins(10, 10, 10, 10); // Set margins
            button.setLayoutParams(params);
            button.setBackgroundResource(R.drawable.button_background); // Set your custom background
            button.setPadding(10, 10, 10, 20); // Set padding
            button.setOnClickListener(view -> openListProductActivity(buttonType));

            if (i < 6) {
                firstRow.addView(button);
            } else {
                secondRow.addView(button);
            }
        }

        containerLayout.addView(firstRow);
        containerLayout.addView(secondRow);
        horizontalScrollView.removeAllViews(); // Clear any existing views
        horizontalScrollView.addView(containerLayout); // Add the container layout as the single child
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
            String productName = input.getText().toString().trim();
            if (!productName.isEmpty()) {
                searchProduct(productName);
            } else {
                Toast.makeText(HomePageActivity.this, "Enter a product name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void searchProduct(String productName) {
        ProductService productService = RetrofitClientInstance.getProductService();
        retrofit2.Call<List<Product>> call = productService.searchProducts(productName);
        Log.d(TAG, "search: " + productName);
        call.enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Product>> call, @NonNull retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    Intent intent = new Intent(HomePageActivity.this, ListProductActivity.class);
                    intent.putExtra("PRODUCT_LIST", new Gson().toJson(products));
                    startActivity(intent);
                } else {
                    Toast.makeText(HomePageActivity.this, "No products found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(HomePageActivity.this, "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Search", "Search failed", t);
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
