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

import androidx.appcompat.widget.Toolbar;

import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;


import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {
    private OkHttpClient client = new OkHttpClient();
    private ViewFlipper productImageFlipper;
    private LinearLayout smallImagesContainer;
    private List<Product> cartList = new ArrayList<>();
    private ProductAdapter.OnAddToCartClickListener onAddToCartClickListener;
    String ip = COMMONSTRING.ip;
    private Button submitReviewButton;

    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // Set the onAddToCartClickListener to this
        onAddToCartClickListener = this;
        Toolbar toolbar = findViewById(R.id.toolbar_homepage);
        setSupportActionBar(toolbar);

        AtomicReference<String> username = new AtomicReference<>(InMemoryStorage.get("username"));
        AtomicInteger productId = new AtomicInteger(getIntent().getIntExtra("productId", -1));
        if (username.get() == null || username.get().isEmpty()) {
            submitReviewButton.setEnabled(false);
        } else {
            checkPurchaseStatus(username.get(), productId.get(), () -> {
                // User has purchased the product, enable the button
                submitReviewButton.setEnabled(false);
            }, () -> {
                // User has not purchased the product, disable the button
                submitReviewButton.setEnabled(true);
            });
        }
        // Nhận dữ liệu từ Intent
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
            Product product = new Product(productId.get(), name, description, price, imageLink, type, rate, purchaseCount);
            showAddToCartDialog(product);
        });

        submitReviewButton.setOnClickListener(v -> {
            username.set(InMemoryStorage.get("username"));
            productId.set(getIntent().getIntExtra("productId", -1));
            String feedback = ((EditText) findViewById(R.id.reviewInput)).getText().toString().trim();

            if (feedback.isEmpty()) {
                Toast.makeText(ProductDetailActivity.this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            } else {
                submitFeedback(username.get(), productId.get(), feedback);

            }
        });

        // Tải hình ảnh từ API
        fetchProductImages(productId.get());
        fetchFeedbacks(productId);
    }

    private void fetchFeedbacks(AtomicInteger productId) {
        String url = "http://" + ip + ":8081/api/getFeedbacks?productId=" + productId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    runOnUiThread(() -> displayFeedbacks(jsonResponse));
                }
            }
        });
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void displayFeedbacks(String jsonResponse) {
        try {
            JSONObject feedbacks = new JSONObject(jsonResponse);
            LinearLayout reviewsContainer = findViewById(R.id.reviewsContainer);
            reviewsContainer.removeAllViews();

            for (Iterator<String> it = feedbacks.keys(); it.hasNext(); ) {
                String fullName = it.next();
                String feedback = feedbacks.getString(fullName);

                View reviewView = getLayoutInflater().inflate(R.layout.item_feedback, reviewsContainer, false);
                TextView reviewText = reviewView.findViewById(R.id.reviewText);

                reviewText.setText(fullName + ": " + feedback);

                reviewsContainer.addView(reviewView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkPurchaseStatus(String username, int productId, Runnable onSuccess, Runnable onFailure) {
        String url = "http://" + ip + ":8081/api/checkPurchase?username=" + username + "&productId=" + productId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(onFailure);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    boolean hasPurchased = Boolean.parseBoolean(responseData);
                    runOnUiThread(() -> {
                        if (hasPurchased) {
                            onSuccess.run();
                        } else {
                            onFailure.run();
                        }
                    });
                } else {
                    runOnUiThread(onFailure);
                }
            }
        });
    }

    private void submitFeedback(String username, int productId, String feedback) {
        String url = "http://" + ip + ":8081/api/submitFeedback";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("productId", productId);
            jsonObject.put("feedback", feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProductDetailActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                        EditText reviewInput = findViewById(R.id.reviewInput);
                        reviewInput.setText("");
                        recreate(); // Reload the activity
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showAddToCartDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        ImageView productImage = dialogView.findViewById(R.id.productImage);
        TextView productName = dialogView.findViewById(R.id.productName);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        Spinner sizeSpinner = dialogView.findViewById(R.id.sizeSpinner);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPriceTextView);
        Button addToCartButton = dialogView.findViewById(R.id.addToCartButton);

        Picasso.get().load(product.getImageLink()).into(productImage);
        productName.setText(product.getName());

        // Add TextWatcher to quantityEditText
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
            String quantityStr = quantityEditText.getText().toString();
            if (quantityStr.isEmpty() || !quantityStr.matches("\\d+")) {
                quantityEditText.setError("Please enter a valid quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            String size = sizeSpinner.getSelectedItem().toString();
            String color = colorSpinner.getSelectedItem().toString();
            if (onAddToCartClickListener != null) {
                onAddToCartClickListener.onAddToCartClick(product, quantity, size, color);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void fetchProductImages(int productId) {
        String url = "http://" + ip + ":8081/api/product-detail?productId=" + productId;
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
                    Type listType = new TypeToken<List<ProductImage>>() {
                    }.getType();
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
    public void onAddToCartClick(Product product, int quantity, String size, String color) {
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
            product.setColor(color);
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