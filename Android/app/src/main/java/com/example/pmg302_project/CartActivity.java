package com.example.pmg302_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.Utils.CartPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private ProductAdapter productAdapter;
    private List<Product> cartList;
    private TextView totalQuantityTextView;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar_homepage);
        setSupportActionBar(toolbar);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        totalQuantityTextView = findViewById(R.id.totalQuantity);
        totalPriceTextView = findViewById(R.id.totalPrice);

        // Fetch cart data from CartPreferences
        loadCartData();

        TextView emptyCartMessage = findViewById(R.id.emptyCartMessage);
//        if (cartList.isEmpty()) {
//            emptyCartMessage.setVisibility(View.VISIBLE);
//        } else {
//            emptyCartMessage.setVisibility(View.GONE);
//            showCartNotification(); // Show notification if cart is not empty
//            Log.d("CartActivity", "Cart size: " + cartList.size());
//        }

        updateCartSummary();
        setupCheckoutButton();
        setupBottomNavigation();
    }

    private void loadCartData() {
        cartList = CartPreferences.loadCart(this);
        productAdapter = new ProductAdapter(this, this, cartList, null, true);
        recyclerViewCart.setAdapter(productAdapter);
    }

    private void setupCheckoutButton() {
        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            String username = InMemoryStorage.get("username");
            Intent intent;
            if (!cartList.isEmpty()) {
                intent = username != null ? new Intent(CartActivity.this, PaymentActivity.class)
                        : new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_order);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent;
            if (itemId == R.id.nav_home) {
                intent = new Intent(CartActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_order) {
                // Handle order action
                return true;
            } else if (itemId == R.id.nav_map) {
                intent = new Intent(CartActivity.this, LandingPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
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

    @Override
    protected void onResume() {
        super.onResume();
        loadCartData(); // Reload cart data
        updateCartSummary(); // Update summary display
    }

}
