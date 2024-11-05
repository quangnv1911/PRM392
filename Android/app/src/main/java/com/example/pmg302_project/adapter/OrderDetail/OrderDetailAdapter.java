package com.example.pmg302_project.adapter.OrderDetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.OrderDetailDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.model.OrderDetail;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private final List<OrderDetail> orderDetailList;

    public OrderDetailAdapter(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);
        holder.bind(orderDetail);
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, quantityTextView, unitPriceTextView, sizeTextView, colorTextView;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.textViewProductName);
            quantityTextView = itemView.findViewById(R.id.textViewQuantity);
            unitPriceTextView = itemView.findViewById(R.id.textViewUnitPrice);
            sizeTextView = itemView.findViewById(R.id.textViewSize);
            colorTextView = itemView.findViewById(R.id.textViewColor);
        }

        public void bind(OrderDetail orderDetail) {
            productNameTextView.setText("Product: " + orderDetail.getProduct().getName());
            quantityTextView.setText("Quantity: " + orderDetail.getQuantity());
            unitPriceTextView.setText("Unit Price: " + NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(orderDetail.getUnitPrice()));
            sizeTextView.setText("Size: " + orderDetail.getSize());
            colorTextView.setText("Color: " + orderDetail.getColor());
        }
    }
}
