package com.example.pmg302_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pmg302_project.Fragments.Products.AddProductFragment;
import com.example.pmg302_project.Fragments.Products.ProductFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManageProductActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_activity);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        FloatingActionButton btnAddProduct = findViewById(R.id.btnAddProduct);

        // Hiển thị ProductFragment trong fragment_container
        if (savedInstanceState == null) {
            ProductFragment productFragment = new ProductFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, productFragment);
            transaction.commit();
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload the ProductFragment
            showProductFragment();
            swipeRefreshLayout.setRefreshing(false); // Stop the refresh animation after loading
        });
        btnAddProduct.setOnClickListener(view -> {
            AddProductFragment addProductFragment = new AddProductFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, addProductFragment);
            transaction.addToBackStack(null); // Allows user to navigate back
            transaction.commit();
        });
    }

    private void showProductFragment() {
        ProductFragment productFragment = new ProductFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, productFragment);
        transaction.commit();
    }
}
