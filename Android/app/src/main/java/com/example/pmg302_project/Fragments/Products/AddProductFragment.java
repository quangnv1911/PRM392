package com.example.pmg302_project.Fragments.Products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.apis.ProductApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductFragment extends Fragment {

    private EditText edtProductName, edtPrice, edtStockQuantity, edtDescription, edtCategoryId, edtImage;
    private Button btnSaveProduct;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        edtProductName = view.findViewById(R.id.edtProductName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtStockQuantity = view.findViewById(R.id.edtStockQuantity);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtCategoryId = view.findViewById(R.id.edtCategoryId);
        edtImage = view.findViewById(R.id.edtImage);
        btnSaveProduct = view.findViewById(R.id.btnSaveProduct);

        btnSaveProduct.setOnClickListener(v -> saveProduct());

        return view;
    }

    private void saveProduct() {
        // Capture input values
        String name = edtProductName.getText().toString();
        Double price = Double.parseDouble(edtPrice.getText().toString());
        Integer quantity = Integer.parseInt(edtStockQuantity.getText().toString());
        String description = edtDescription.getText().toString();
        int categoryId = Integer.parseInt(edtCategoryId.getText().toString());
        String imageUrl = edtImage.getText().toString();

        // Create ProductDTO and populate it
        ProductDTO newProduct = new ProductDTO();
        newProduct.setProductName(name);
        newProduct.setPrice(price);
        newProduct.setStockQuantity(quantity);
        newProduct.setDescription(description);
        newProduct.setCategoryId(categoryId);
        newProduct.setImage(imageUrl);

        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        Call<ProductDTO> call = productApi.addProduct(newProduct);
        call.enqueue(new Callback<ProductDTO>() {
            @Override
            public void onResponse(Call<ProductDTO> call, Response<ProductDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}