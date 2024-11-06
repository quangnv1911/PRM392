package com.example.pmg302_project.Fragments.Products;

import android.app.AlertDialog;
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
import com.example.pmg302_project.adapter.ProductManageAdapter;
import com.example.pmg302_project.apis.ProductApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment implements ProductManageAdapter.OnProductActionListener {
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
                    productAdapter = new ProductManageAdapter(getContext(), productList, ProductFragment.this);
                    recyclerView.setAdapter(productAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductDTO>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onEditProduct(ProductDTO product) {
        // Open AddProductFragment in edit mode
        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        Call<ProductDTO> call = productApi.getProduct(product.getProductId());
        call.enqueue(new Callback<ProductDTO>() {
            @Override
            public void onResponse(Call<ProductDTO> call, Response<ProductDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductDTO fullProductDetails = response.body();

                    // Open AddProductFragment in edit mode with the fetched product details
                    AddProductFragment editProductFragment = AddProductFragment.newInstance(fullProductDetails);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, editProductFragment)
                            .addToBackStack(null)
                            .commit();

                } else {
                    Toast.makeText(getContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDeleteProduct(ProductDTO product) {
        // Show a confirmation dialog before deletion
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm không?")
                .setPositiveButton("Có", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Không", null)
                .show();
    }

    private void deleteProduct(ProductDTO product) {
        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        Call<Void> call = productApi.deleteProduct(product.getProductId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    productList.remove(product); // Remove the product from the list
                    productAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                } else {
                    Toast.makeText(getContext(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}