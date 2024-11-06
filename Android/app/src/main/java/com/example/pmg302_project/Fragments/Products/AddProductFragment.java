package com.example.pmg302_project.Fragments.Products;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.apis.CateogryApi;
import com.example.pmg302_project.apis.ProductApi;
import com.example.pmg302_project.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddProductFragment extends Fragment {
    private TextView tvSizes, tvColors, tvImageDetails, configTitle;
    private EditText edtProductName, edtPrice, edtStockQuantity, edtDescription, edtCategoryId, edtImage, edtSize, edtColor, edtImageDetail;
    private Button btnSaveProduct, btnAddSize, btnAddColor, btnAddImageDetail;
    private Spinner categorySpinner;
    private List<String> sizes = new ArrayList<>();
    private List<String> colors = new ArrayList<>();
    private List<String> imageDetails = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private ProductDTO currentProduct;

    public static AddProductFragment newInstance(ProductDTO product) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product); // Pass product if editing
        fragment.setArguments(args);
        return fragment;
    }


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        if (getArguments() != null && getArguments().containsKey("product")) {
            currentProduct = (ProductDTO) getArguments().getSerializable("product");
        }
        // Initialize views
        tvSizes = view.findViewById(R.id.tvSizes);
        tvColors = view.findViewById(R.id.tvColors);
        tvImageDetails = view.findViewById(R.id.tvImageDetails);
        edtProductName = view.findViewById(R.id.edtProductName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtStockQuantity = view.findViewById(R.id.edtStockQuantity);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtImage = view.findViewById(R.id.edtImage);
        edtSize = view.findViewById(R.id.edtSize);
        edtColor = view.findViewById(R.id.edtColor);
        edtImageDetail = view.findViewById(R.id.edtImageDetail);
        categorySpinner = view.findViewById(R.id.spinnerCategory);
        configTitle = view.findViewById(R.id.configTitle);

        btnSaveProduct = view.findViewById(R.id.btnSaveProduct);
        btnAddSize = view.findViewById(R.id.btnAddSize);
        btnAddColor = view.findViewById(R.id.btnAddColor);
        btnAddImageDetail = view.findViewById(R.id.btnAddImageDetail);

        // Set click listeners
        view.findViewById(R.id.btnAddSize).setOnClickListener(v -> addToList(edtSize, sizes, tvSizes));
        view.findViewById(R.id.btnAddColor).setOnClickListener(v -> addToList(edtColor, colors, tvColors));
        view.findViewById(R.id.btnAddImageDetail).setOnClickListener(v -> addToList(edtImageDetail, imageDetails, tvImageDetails));

        if (currentProduct != null) {
            populateFieldsForEdit();
            configTitle.setText("Cập nhật sản phẩm");
            btnSaveProduct.setText("Câp nhật"); // Change button text to "Update Product"
        } else {
            configTitle.setText("Thêm sản phẩm");
            btnSaveProduct.setText("Thêm mới"); // Button text for adding new product
        }

        btnSaveProduct.setOnClickListener(v -> {
            if (currentProduct == null) {
                saveProduct(); // Call save method if adding
            } else {
                updateProduct(); // Call update method if editing
            }
        });

        fetchCategories();

        return view;
    }


    private void populateFieldsForEdit() {
        edtProductName.setText(currentProduct.getProductName());
        edtPrice.setText(String.valueOf(currentProduct.getPrice()));
        edtStockQuantity.setText(String.valueOf(currentProduct.getStockQuantity()));
        edtDescription.setText(currentProduct.getDescription());
        edtImage.setText(currentProduct.getImage());

        // Populate sizes, colors, and image details
        sizes.addAll(currentProduct.getSizes());
        colors.addAll(currentProduct.getColors());
        imageDetails.addAll(currentProduct.getImageProductDetails());

        tvSizes.setText(TextUtils.join(", ", sizes));
        tvColors.setText(TextUtils.join(", ", colors));
        tvImageDetails.setText(TextUtils.join(", ", imageDetails));

        // Set category in spinner
        for (int i = 0; i < categorySpinner.getCount(); i++) {
            Category category = (Category) categorySpinner.getItemAtPosition(i);
            if (category.getCategoryId() == currentProduct.getCategoryId()) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }

    private void updateProduct() {
        // Code to update the existing product
        currentProduct.setProductName(edtProductName.getText().toString());
        currentProduct.setPrice(Double.parseDouble(edtPrice.getText().toString()));
        currentProduct.setStockQuantity(Integer.parseInt(edtStockQuantity.getText().toString()));
        currentProduct.setDescription(edtDescription.getText().toString());
        currentProduct.setImage(edtImage.getText().toString());
        currentProduct.setCategoryId(((Category) categorySpinner.getSelectedItem()).getCategoryId());
        currentProduct.setSizes(sizes);
        currentProduct.setColors(colors);
        currentProduct.setImageProductDetails(imageDetails);

        // API call to update product
        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        Call<ProductDTO> call = productApi.updateProduct(currentProduct.getProductId(), currentProduct);
        call.enqueue(new Callback<ProductDTO>() {
            @Override
            public void onResponse(Call<ProductDTO> call, Response<ProductDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProduct() {
        // Validate input values
        String name = edtProductName.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String quantityText = edtStockQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String imageUrl = edtImage.getText().toString().trim();

        if (name.isEmpty()) {
            edtProductName.setError("Không được để trống tên sản phẩm");
            edtProductName.requestFocus();
            return;
        }

        if (priceText.isEmpty()) {
            edtPrice.setError("Không được để trống giá tiền");
            edtPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                edtPrice.setError("Số tiền phải lớn hơn 0");
                edtPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Vui lòng nhập đúng định dạng của giá tiền");
            edtPrice.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtStockQuantity.setError("Cần điền số hàng có trong kho");
            edtStockQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                edtStockQuantity.setError("Số lượng hàng không được nhỏ hơn 0");
                edtStockQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            edtStockQuantity.setError("Vui lòng nhập đúng định dạng");
            edtStockQuantity.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            edtDescription.setError("Miêu tả sản phẩm là bắt buộc");
            edtDescription.requestFocus();
            return;
        }

        if (categorySpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Sản phẩm cần có category", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = ((Category) categorySpinner.getSelectedItem()).getCategoryId();

        if (imageUrl.isEmpty()) {
            edtImage.setError("Link ảnh không được để trống");
            edtImage.requestFocus();
            return;
        }

        // Ensure sizes, colors, and image details are not empty
        if (sizes.isEmpty()) {
            Toast.makeText(getContext(), "Hãy thêm tối thiểu 1 kích cỡ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (colors.isEmpty()) {
            Toast.makeText(getContext(), "Hãy thêm tối thiểu 1 màu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageDetails.isEmpty()) {
            Toast.makeText(getContext(), "Hãy thêm tối thiểu 1 link ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create ProductDTO and populate it
        ProductDTO newProduct = new ProductDTO();
        newProduct.setProductName(name);
        newProduct.setPrice(price);
        newProduct.setStockQuantity(quantity);
        newProduct.setDescription(description);
        newProduct.setCategoryId(categoryId);
        newProduct.setImage(imageUrl);
        newProduct.setSizes(sizes);
        newProduct.setColors(colors);
        newProduct.setImageProductDetails(imageDetails);

        // Proceed with API call if validation passes
        ProductApi productApi = RetrofitClient.getClient().create(ProductApi.class);
        Call<ProductDTO> call = productApi.addProduct(newProduct);
        call.enqueue(new Callback<ProductDTO>() {
            @Override
            public void onResponse(Call<ProductDTO> call, Response<ProductDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchCategories() {
        CateogryApi cateogryApi = RetrofitClient.getClient().create(CateogryApi.class);
        Call<List<Category>> call = cateogryApi.getAllCategory();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToList(EditText editText, List<String> list, TextView display) {
        String text = editText.getText().toString().trim();
        if (!text.isEmpty()) {
            list.add(text);
            editText.setText("");
            display.setText(TextUtils.join(", ", list));  // Update TextView to show current list
        } else {
            Toast.makeText(getContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

}
