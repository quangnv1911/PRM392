package com.example.pmg302_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Fragments.Orders.OrdersFragment;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Load OrdersFragment into the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrdersFragment())
                    .commit();
        }
    }
}