package com.example.pmg302_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductManageAdapter extends RecyclerView.Adapter<ProductManageAdapter.ProductViewHolder> {
    private List<ProductDTO> productList = new ArrayList<>();
    private Context context;
    private OnProductActionListener listener;

    public ProductManageAdapter(Context context, List<ProductDTO> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_manage, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDTO product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.price.setText("Giá: " + product.getPrice().toString());
        holder.quantity.setText("Trong kho: " + product.getStockQuantity().toString());
        holder.rate.setText("Đánh giá: " +product.getRate().toString() + "/10");
        holder.productPurchase.setText("Đã bán: " + product.getPurchaseCount().toString());

        Picasso.get()
                .load(product.getImageLink()) // Sử dụng link hình ảnh từ đối tượng product
                .placeholder(R.drawable.baseline_image_24) // Hình ảnh hiển thị khi đang tải
                .error(R.drawable.baseline_image_24) // Hình ảnh hiển thị khi tải thất bại
                .into(holder.image);
        // Thêm sự kiện chỉnh sửa và xóa sản phẩm tại đây.
        holder.editButton.setOnClickListener(v -> listener.onEditProduct(product));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteProduct(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, price, productPurchase, quantity, rate;
        ImageView image;
        ImageButton editButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameManage);
            price = itemView.findViewById(R.id.productPriceManage);
            productPurchase = itemView.findViewById(R.id.productPurchaseManage);
            quantity = itemView.findViewById(R.id.productQuantityManage);
            rate = itemView.findViewById(R.id.productRateManage);
            image = itemView.findViewById(R.id.productImageManage);
            editButton = itemView.findViewById(R.id.editButtonManage);
            deleteButton = itemView.findViewById(R.id.deleteButtonManage);
        }
    }

    public interface OnProductActionListener {
        void onEditProduct(ProductDTO product);
        void onDeleteProduct(ProductDTO product);
    }
}
