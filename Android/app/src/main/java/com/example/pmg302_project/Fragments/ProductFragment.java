package com.example.pmg302_project.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.adapter.ProductManageAdapter;
import com.example.pmg302_project.apis.ProductApi;
import com.example.pmg302_project.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductManageAdapter productAdapter;
    private List<ProductDTO> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_product, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewProductManage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProducts();
        return view;
    }

    private void loadProducts() {
        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        productApi.getProducts().enqueue(new Callback<List<ProductDTO>>() {
            @Override
            public void onResponse(Call<List<ProductDTO>> call, Response<List<ProductDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    productAdapter = new ProductManageAdapter(getContext(), productList);
                    recyclerView.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}