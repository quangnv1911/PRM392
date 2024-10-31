package com.example.pmg302_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.pmg302_project.Fragments.ProductFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManageProductActivity extends AppCompatActivity {

    private FloatingActionButton btnAddProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_activity);

        btnAddProduct = findViewById(R.id.btnAddProduct);

        // Hiển thị ProductFragment trong fragment_container
        if (savedInstanceState == null) {
            ProductFragment productFragment = new ProductFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, productFragment);
            transaction.commit();
        }

        btnAddProduct.setOnClickListener(view -> {
            // Thêm chức năng thêm sản phẩm mới tại đây
        });
    }

}
