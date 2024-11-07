package com.example.pmg302_project.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.CartActivity;
import com.example.pmg302_project.InMemoryStorage;
import com.example.pmg302_project.ProductDetailActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.repository.FavoriteRepository;
import com.example.pmg302_project.service.FavoriteService;
import com.example.pmg302_project.service.ProductService;
import com.example.pmg302_project.util.ApiResponse;
import com.example.pmg302_project.util.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnAddToCartClickListener onAddToCartClickListener;
    private boolean isCart;
    private Set<Integer> favoriteProductIds;
    private FavoriteService favoriteService;
    private OkHttpClient client = new OkHttpClient();
    private Activity activity; // Add this field
    String ip = COMMONSTRING.ip;
    String username;
    public ProductAdapter(Activity activity, Context context, List<Product> productList, OnAddToCartClickListener onAddToCartClickListener, boolean isCart) {
        this.context = context;
        this.productList = productList;
        this.onAddToCartClickListener = onAddToCartClickListener;
        this.isCart = isCart;
        this.favoriteProductIds = new HashSet<>();
        this.activity = activity; // Initialize this field
        this.favoriteService = RetrofitClientInstance.getFavoriteService();
    }

    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Override
    public int getItemViewType(int position) {
        return isCart ? R.layout.item_cart : R.layout.item_product;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return isCart ? new CartViewHolder(view) : new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (isCart) {
            CartViewHolder cartHolder = (CartViewHolder) holder;
            cartHolder.cartProductName.setText(product.getName());
            cartHolder.cartProductPrice.setText(product.getPrice() + "$");
            cartHolder.cartProductSize.setText("Size: " + product.getSize());
            cartHolder.cartProductQuantity.setText("Quantity: " + product.getQuantity());
            cartHolder.cartProductColor.setText("Color: " + product.getColor()); // Set color

            Picasso.get().load(product.getImageLink()).into(cartHolder.cartProductImage);
            cartHolder.removeFromCartButton.setOnClickListener(v -> {
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
                CartPreferences.saveCart(context, productList); // Update cart in SharedPreferences

                // Update cart summary
                if (context instanceof CartActivity) {
                    ((CartActivity) context).updateCartSummary();
                }
            });
        } else {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.productName.setText(product.getName());
            productHolder.productPrice.setText(product.getPrice() + "$");
            productHolder.productDescription.setText(product.getDescription());
            productHolder.productRate.setText("Đánh giá: " + product.getRate() + "/5");
            productHolder.productPurchase.setText("Lượt mua: " + product.getPurchaseCount());
            Picasso.get().load(product.getImageLink()).into(productHolder.productImage);
            productHolder.addToCartButton.setOnClickListener(v -> showAddToCartDialog(product));

            // Set click listener for detailButton
            productHolder.detailButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("imageLink", product.getImageLink());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("rate", product.getRate());
                intent.putExtra("purchaseCount", product.getPurchaseCount());
                intent.putExtra("description", product.getDescription());
                context.startActivity(intent);
            });

            int productId = product.getId();
            productHolder.imgFavorite.setImageResource(
                    favoriteProductIds.contains(productId) ? R.drawable.ic_heart_filled : R.drawable.ic_heart
            );

            productHolder.imgFavorite.setOnClickListener(view -> {
                // Get userId from storage
                String userIdString = InMemoryStorage.get("userId");

                // Ensure userIdString is not null or empty to avoid parsing errors
                if (userIdString == null || userIdString.isEmpty()) {
                    Log.d("ProductAdapter", "UserId is null or empty, cannot toggle favorite.");
                    return;
                }

                Long userId;
                try {
                    userId = Long.parseLong(userIdString);
                } catch (NumberFormatException e) {
                    Log.e("ProductAdapter", "Error parsing userId: " + e.getMessage());
                    return;
                }

                Log.d("ProductAdapter", "UserId: " + userId);

                // Toggle favorite status
                if (favoriteProductIds.contains(productId)) {
                    // Remove favorite if currently favorited
                    getAccountByUserId(userId, accountId -> {
                        favoriteService.removeFavorite(accountId, productId)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            // Update UI and list after removal
                                            favoriteProductIds.remove(productId);
                                            productHolder.imgFavorite.setImageResource(R.drawable.ic_heart);
                                            Log.d("ProductAdapter", "Favorite removed successfully.");
                                            Toast.makeText(context,"Favorite removed successfully.",Toast.LENGTH_LONG);
                                        } else {
                                            Log.d("ProductAdapter", "Failed to remove favorite.");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("ProductAdapter", "Error removing favorite: " + t.getMessage());
                                    }
                                });
                    });
                } else {
                    // Add favorite if not currently favorited
                    getAccountByUserId(userId, accountId -> {
                        favoriteService.addFavorite(accountId, productId)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            // Update UI and list after addition
                                            favoriteProductIds.add(productId);
                                            productHolder.imgFavorite.setImageResource(R.drawable.ic_heart_filled);
                                            Log.d("ProductAdapter", "Favorite added successfully.");

                                            Toast.makeText(context,"Favorite added successfully.",Toast.LENGTH_LONG);
                                        } else {
                                            Log.d("ProductAdapter", "Failed to add favorite.");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("ProductAdapter", "Error adding favorite: " + t.getMessage());
                                    }
                                });
                    });
                }
            });
        }
    }
    public void getAccountByUserId(Long userId, AccountIdCallback callback) {
        if (favoriteService == null) {
            Log.e("ProductAdapter", "FavoriteService is null!");
            return; // Exit if favoriteService is null
        }

        // Call the API using userId to get the account details
        Call<Account> call = favoriteService.getAccountByUserId(userId);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account account = response.body();

                    // Log the account ID only for confirmation
                    Log.d("ProductAdapter GetAccountId", "Retrieved accountId: " + account.getId());

                    // Pass accountId to the callback
                    callback.onAccountIdRetrieved(account.getId());
                } else {
                    Log.d("ProductAdapter", "Failed to retrieve account.");
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("ProductAdapter", "Error fetching account: " + t.getMessage());
            }
        });
    }


    public void getAccountByUsername(String username, AccountIdCallback callback) {
        if (favoriteService == null) {
            Log.e("ProductAdapter", "FavoriteService is null!");
            return; // Exit if favoriteService is null
        }

        Call<Account> call = favoriteService.getAccountByUsername(username);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Log.d("ProductAdapter Getacc: ", String.valueOf(response.body()));

                if (response.isSuccessful() && response.body() != null) {
                    Account account = response.body();

                    callback.onAccountIdRetrieved(account.getId());
                } else {
                    Log.d("ProductAdapter", "Failed to retrieve account.");
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("ProductAdapter", "Error fetching account: " + t.getMessage());
            }
        });
    }

    // Callback interface for accountId retrieval
    public interface AccountIdCallback {
        void onAccountIdRetrieved(int accountId);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product, int quantity, String size, String color);
    }

    private void fetchSizesAndColors(int productId, Spinner sizeSpinner, Spinner colorSpinner) {
        Log.d("fetchSizesAndColors", "Fetching sizes and colors for productId: " + productId);
        ProductService productService = RetrofitClientInstance.getProductService();
        Call<ApiResponse> call = productService.getSizesAndColors(productId);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("fetchSizesAndColors", "onResponse called");
                if (response.isSuccessful() && response.body() != null) {
                    List<String> sizes = new ArrayList<>();
                    List<String> colors = new ArrayList<>();

                    for (String size : response.body().getSizes()) {
                        sizes.add("size " + size); // Prepend "size"
                    }

                    for (String color : response.body().getColors()) {
                        colors.add("màu " + color); // Prepend "màu"
                    }
                    Log.d("fetchSizesAndColors", sizes.toString());
                    Log.d("fetchSizesAndColors", colors.toString());

                    activity.runOnUiThread(() -> {
                        Log.d("fetchSizesAndColors", "Updating UI with sizes and colors");
                        Log.d("fetchSizesAndColors", "Context: " + context);
                        Log.d("fetchSizesAndColors", "Activity: " + activity);

                        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sizes);
                        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sizeSpinner.setAdapter(sizeAdapter);

                        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colors);
                        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        colorSpinner.setAdapter(colorAdapter);
                    });
                } else {
                    Log.d("fetchSizesAndColors", "Response is not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("fetchSizesAndColors", "onFailure called", t);
            }
        });
    }

    private void showAddToCartDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        ImageView productImage = dialogView.findViewById(R.id.productImage);
        TextView productName = dialogView.findViewById(R.id.productName);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        Spinner sizeSpinner = dialogView.findViewById(R.id.sizeSpinner);
        Spinner colorSpinner = dialogView.findViewById(R.id.colorSpinner);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPriceTextView);
        Button addToCartButton = dialogView.findViewById(R.id.addToCartButton);

        // Load product image and set name
        Picasso.get().load(product.getImageLink()).into(productImage);
        productName.setText(product.getName());

        // Fetch sizes and colors
        fetchSizesAndColors(product.getId(), sizeSpinner, colorSpinner);

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
            String quantityStr = quantityEditText.getText().toString();
            if (quantityStr.isEmpty() || !quantityStr.matches("\\d+")) {
                quantityEditText.setError("Please enter a valid quantity");
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            String size = sizeSpinner.getSelectedItem() != null ? sizeSpinner.getSelectedItem().toString() : "";
            String color = colorSpinner.getSelectedItem() != null ? colorSpinner.getSelectedItem().toString() : "";

            if (onAddToCartClickListener != null) {
                onAddToCartClickListener.onAddToCartClick(product, quantity, size, color);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        TextView productRate;
        TextView productPurchase;
        Button addToCartButton;
        Button detailButton; // Add reference to detailButton
        ImageView imgFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            productRate = itemView.findViewById(R.id.productRate);
            productPurchase = itemView.findViewById(R.id.productPurchase);
            addToCartButton = itemView.findViewById(R.id.button);
            detailButton = itemView.findViewById(R.id.detailButton); // Initialize detailButton
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartProductImage;
        TextView cartProductName;
        TextView cartProductPrice;
        TextView cartProductSize;
        TextView cartProductQuantity;
        TextView cartProductColor;
        Button removeFromCartButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartProductImage = itemView.findViewById(R.id.cartProductImage);
            cartProductName = itemView.findViewById(R.id.cartProductName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartProductSize = itemView.findViewById(R.id.cartProductSize);
            cartProductQuantity = itemView.findViewById(R.id.cartProductQuantity);
            cartProductColor = itemView.findViewById(R.id.cartProductColor); // Initialize color TextView
            removeFromCartButton = itemView.findViewById(R.id.removeFromCartButton);
        }
    }
}