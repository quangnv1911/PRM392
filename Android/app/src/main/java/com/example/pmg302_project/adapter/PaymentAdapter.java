package com.example.pmg302_project.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public PaymentAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ProductViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
            PaymentAdapter.ProductViewHolder productHolder = (PaymentAdapter.ProductViewHolder) holder;
            productHolder.productName.setText(product.getName());
            productHolder.productPrice.setText("Price: "+product.getPrice() + "$");
            productHolder.productSize.setText("Size: " + product.getSize());
            productHolder.productQuantity.setText("Quantity: " + product.getQuantity());
            productHolder.productColor.setText("Color: " + product.getColor()); // Set color
            Picasso.get().load(product.getImageLink()).into(productHolder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product, int quantity, String size, String color);
    }

    private void showEditInforDialog(Account account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_infomation, null);
        builder.setView(dialogView);

        TextView userName = dialogView.findViewById(R.id.editName);
        TextView userPhone = dialogView.findViewById(R.id.editPhone);
        TextView userAddress = dialogView.findViewById(R.id.editAddress);

        userName.setText(account.getFullname());
        userPhone.setText(account.getPhone());
        userAddress.setText(account.getAddress());

        // Add TextWatcher to quantityEditText
//        quantityEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                try {
//                    int quantity = Integer.parseInt(s.toString());
//                    double totalPrice = quantity * product.getPrice();
//                    totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
//                } catch (NumberFormatException e) {
//                    totalPriceTextView.setText("Total: $0.00");
//                }
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        addToCartButton.setOnClickListener(v -> {
//            String quantityStr = quantityEditText.getText().toString();
//            if (quantityStr.isEmpty() || !quantityStr.matches("\\d+")) {
//                quantityEditText.setError("Please enter a valid quantity");
//                return;
//            }
//
//            int quantity = Integer.parseInt(quantityStr);
//            String size = sizeSpinner.getSelectedItem().toString();
//            String color = colorSpinner.getSelectedItem().toString();
//            if (onAddToCartClickListener != null) {
//                onAddToCartClickListener.onAddToCartClick(product, quantity, size, color);
//            }
//            dialog.dismiss();
//        });
//
//        dialog.show();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productSize;
        TextView productQuantity;
        TextView productColor;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.payProductImage);
            productName = itemView.findViewById(R.id.payProductName);
            productPrice = itemView.findViewById(R.id.payProductPrice);
            productSize = itemView.findViewById(R.id.payProductSize);
            productQuantity = itemView.findViewById(R.id.payProductQuantity);
            productColor = itemView.findViewById(R.id.payProductColor);
        }
    }
}