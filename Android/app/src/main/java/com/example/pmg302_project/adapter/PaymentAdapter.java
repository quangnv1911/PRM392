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
import com.example.pmg302_project.model.OrderDetail;
import com.example.pmg302_project.model.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


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

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(product.getPrice());
            productHolder.productPrice.setText("Giá: "+formattedPrice + " VNĐ");
            productHolder.productSize.setText("Kích cỡ: " + product.getSize());
            productHolder.productQuantity.setText("Số lượng: " + product.getQuantity());
            productHolder.productColor.setText("Màu: " + product.getColor()); // Set color
            Picasso.get().load(product.getImageLink()).into(productHolder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
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