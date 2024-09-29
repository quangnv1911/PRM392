package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;


import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private OkHttpClient client = new OkHttpClient();
    private ViewFlipper productImageFlipper;
    private LinearLayout smallImagesContainer;
    private List<Product> cartList = new ArrayList<>();
    private ProductAdapter.OnAddToCartClickListener onAddToCartClickListener;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Set the onAddToCartClickListener to this
        onAddToCartClickListener = this;

        // Nhận dữ liệu từ Intent
        int productId = getIntent().getIntExtra("productId", -1);
        String name = getIntent().getStringExtra("name");
        double price = getIntent().getDoubleExtra("price", 0);
        double rate = getIntent().getDoubleExtra("rate", 0);
        int purchaseCount = getIntent().getIntExtra("purchaseCount", 0);
        String imageLink = getIntent().getStringExtra("imageLink");
        String type = getIntent().getStringExtra("type");
        String description = getIntent().getStringExtra("description");

        // Ánh xạ các view
        TextView productName = findViewById(R.id.productName);
        productImageFlipper = findViewById(R.id.productImageFlipper);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productRate = findViewById(R.id.productRate);
        TextView productPurchaseCount = findViewById(R.id.productPurchaseCount);
        TextView productDescription = findViewById(R.id.productDescription);
        Button addToCartButton = findViewById(R.id.addToCartButton);
        smallImagesContainer = findViewById(R.id.smallImagesContainer);

        // Gán dữ liệu vào các view
        productName.setText(name);
        productPrice.setText(String.format("Giá: $%.2f", price));
        productRate.setText(String.format("Đánh giá: %.1f", rate));
        productPurchaseCount.setText(String.format("Lượt mua: %d", purchaseCount));
        productDescription.setText(description);

        addToCartButton.setOnClickListener(v -> {
            Log.d(TAG, "Add to Cart button clicked");
            Product product = new Product(productId, name, description, price, imageLink, type, rate, purchaseCount);
            showAddToCartDialog(product);
        });

        // Tải hình ảnh từ API
        fetchProductImages(productId);
    }

    private void showAddToCartDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        ImageView productImage = dialogView.findViewById(R.id.productImage);
        TextView productName = dialogView.findViewById(R.id.productName);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        Spinner sizeSpinner = dialogView.findViewById(R.id.sizeSpinner);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPriceTextView);
        Button addToCartButton = dialogView.findViewById(R.id.addToCartButton);

        Picasso.get().load(product.getImageLink()).into(productImage);
        productName.setText(product.getName());

        // Add TextWatcher to quantityEditText
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int quantity = Integer.parseInt(s.toString());
                    double totalPrice = quantity * product.getPrice();
                    totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
                } catch (NumberFormatException e) {
                    totalPriceTextView.setText("Total: $0.00");
                }
            }
        });

        AlertDialog dialog = builder.create();
        addToCartButton.setOnClickListener(v -> {
            if (onAddToCartClickListener != null) {
                int quantity = Integer.parseInt(quantityEditText.getText().toString());
                String size = sizeSpinner.getSelectedItem().toString();
                onAddToCartClickListener.onAddToCartClick(product, quantity, size);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void fetchProductImages(int productId) {
        String url = "http://172.20.109.44:8081/api/product-detail?productId=" + productId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ProductImage>>() {}.getType();
                    List<ProductImage> images = gson.fromJson(jsonResponse, listType);

                    runOnUiThread(() -> displayProductImages(images));
                }
            }
        });
    }

    private void displayProductImages(List<ProductImage> images) {
        if (images == null || images.isEmpty()) return;

        productImageFlipper.removeAllViews(); // Xóa tất cả view trước khi thêm mới

        for (int i = 0; i < images.size(); i++) {
            ProductImage image = images.get(i);
            int index = i; // Tạo bản sao của i để sử dụng trong callback

            // Thêm ảnh vào ViewFlipper
            ImageView imageView = new ImageView(ProductDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(image.getImageProduct()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    productImageFlipper.addView(imageView);
                    // Bắt đầu lật hình sau khi hình ảnh đầu tiên được tải
                    if (index == 0) {
                        productImageFlipper.setFlipInterval(5000); // Lật sau mỗi 5 giây
                        productImageFlipper.startFlipping();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            // Thêm ảnh nhỏ vào container
            ImageView smallImageView = new ImageView(ProductDetailActivity.this);
            smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(8, 0, 8, 0);
            smallImageView.setLayoutParams(params);
            Picasso.get().load(image.getImageProduct()).into(smallImageView, new Callback() {
                @Override
                public void onSuccess() {
                    smallImagesContainer.addView(smallImageView);
                    // Gán sự kiện click để chuyển đến hình ảnh tương ứng trong ViewFlipper
                    smallImageView.setOnClickListener(v -> {
                        productImageFlipper.stopFlipping(); // Dừng lật khi người dùng click vào hình nhỏ
                        productImageFlipper.setDisplayedChild(index); // Chuyển đến hình ảnh được chọn
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onAddToCartClick(Product product, int quantity, String size) {
        cartList = CartPreferences.loadCart(this);
        boolean productExists = false;

        // Check if product already exists in cart
        for (Product cartProduct : cartList) {
            if (cartProduct.getId() == product.getId() && cartProduct.getSize().equals(size)) {
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
            cartList.add(product);
        }

        CartPreferences.saveCart(this, cartList); // Save cart to SharedPreferences
        Toast.makeText(this, product.getName() + " added to cart.", Toast.LENGTH_SHORT).show();
    }

    private static class ProductImage {
        private int id;
        private int productId;
        private String imageProduct;

        public String getImageProduct() {
            return imageProduct;
        }
    }
}