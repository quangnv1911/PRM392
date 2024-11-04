package com.example.pmg302_project.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.CartActivity;
import com.example.pmg302_project.ProductDetailActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.Utils.CartPreferences;
import com.example.pmg302_project.model.Product;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnAddToCartClickListener onAddToCartClickListener;
    private boolean isCart;
    private Set<Integer> favoriteProductIds;
    private OkHttpClient client = new OkHttpClient();
    private Activity activity; // Add this field
    String ip = COMMONSTRING.ip;

    public ProductAdapter(Activity activity, Context context, List<Product> productList, OnAddToCartClickListener onAddToCartClickListener, boolean isCart) {
        this.context = context;
        this.productList = productList;
        this.onAddToCartClickListener = onAddToCartClickListener;
        this.isCart = isCart;
        this.favoriteProductIds = new HashSet<>();
        this.activity = activity; // Initialize this field
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
                favoriteProductIds.contains(productId) ?
                        R.drawable.ic_heart_filled : R.drawable.ic_heart
        ); // Change icon based on favorite status
    }
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product, int quantity, String size, String color);
    }

    // Add this method to fetch sizes and colors from the API
private void fetchSizesAndColors(int productId, Spinner sizeSpinner, Spinner colorSpinner) {
    String url = "http://"+ip+":8081/api/product/sizes-colors?productId=" + productId;

    Request request = new Request.Builder()
            .url(url)
            .build();

    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray sizesArray = jsonObject.getJSONArray("sizes");
                    JSONArray colorsArray = jsonObject.getJSONArray("colors");

                    List<String> sizes = new ArrayList<>();
                    List<String> colors = new ArrayList<>();

                    for (int i = 0; i < sizesArray.length(); i++) {
                        sizes.add(sizesArray.getString(i));
                    }

                    for (int i = 0; i < colorsArray.length(); i++) {
                        colors.add(colorsArray.getString(i));
                    }

                    activity.runOnUiThread(() -> {
                        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sizes);
                        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sizeSpinner.setAdapter(sizeAdapter);

                        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, colors);
                        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        colorSpinner.setAdapter(colorAdapter);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

        Picasso.get().load(product.getImageLink()).into(productImage);
        productName.setText(product.getName());

        fetchSizesAndColors(product.getId(), sizeSpinner, colorSpinner);


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