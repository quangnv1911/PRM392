// CartActivity.java
package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.Utils.CartPreferences;

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

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));

        totalQuantityTextView = findViewById(R.id.totalQuantity);
        totalPriceTextView = findViewById(R.id.totalPrice);

        // Fetch cart data from CartPreferences
        cartList = CartPreferences.loadCart(this);
        productAdapter = new ProductAdapter(this, cartList, null, true);
        recyclerViewCart.setAdapter(productAdapter);

        TextView emptyCartMessage = findViewById(R.id.emptyCartMessage);
        if (cartList.isEmpty()) {
            emptyCartMessage.setVisibility(TextView.VISIBLE);
        } else {
            emptyCartMessage.setVisibility(TextView.GONE);
        }

        updateCartSummary();
        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=InMemoryStorage.get("username");
                Intent intent;
                if(username!=null){
                     intent = new Intent(CartActivity.this, PaymentActivity.class);
                }else{
                     intent = new Intent(CartActivity.this, MainActivity.class);
                }

                startActivity(intent);
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