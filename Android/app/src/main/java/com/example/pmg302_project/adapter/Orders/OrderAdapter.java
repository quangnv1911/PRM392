package com.example.pmg302_project.adapter.Orders;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.OrderDetailDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.OrderDetail.OrderDetailAdapter;
import com.example.pmg302_project.apis.OrderApi;
import com.example.pmg302_project.model.OrderDetail;
import com.example.pmg302_project.model.Orders;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Orders> orderList;

    public OrderAdapter(List<Orders> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = orderList.get(position);
        holder.bind(order);

        holder.itemView.setOnClickListener(v -> showOrderDetailsDialog(holder.itemView.getContext(), order.getOrderId()));
    }

    private void showOrderDetailsDialog(Context context, int orderId) {
        // Initialize dialog builder and set a loading message
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order Details");

        // Inflate dialog view
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_order_detail, null);
        builder.setView(dialogView);

        // Setup RecyclerView in dialog
        RecyclerView recyclerViewOrderDetails = dialogView.findViewById(R.id.recyclerViewOrderDetails);
        recyclerViewOrderDetails.setLayoutManager(new LinearLayoutManager(context));

        // Show the dialog with a "loading" indicator while data loads
        AlertDialog dialog = builder.create();
        dialog.show();

        // Use Retrofit to get order details
        OrderApi orderApi = RetrofitClient.getClient().create(OrderApi.class);
        Call<List<OrderDetail>> call = orderApi.getOrderDetails(orderId);
        call.enqueue(new Callback<List<OrderDetail>>() {
            @Override
            public void onResponse(Call<List<OrderDetail>> call, Response<List<OrderDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successfully received data, populate the RecyclerView
                    List<OrderDetail> orderDetails = response.body();
                    OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(orderDetails);
                    recyclerViewOrderDetails.setAdapter(orderDetailAdapter);
                } else {
                    // Handle error case where response is not successful
                                                                                                                                    Toast.makeText(context, "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDetail>> call, Throwable t) {
                // Handle failure case
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Close", (dialogInterface, which) -> dialogInterface.dismiss());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalPriceTextView, textViewOrderDate;
        Spinner spinnerStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textViewOrderId);
            totalPriceTextView = itemView.findViewById(R.id.textViewTotalPrice);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }

        public void bind(Orders order) {
            orderIdTextView.setText("Order ID: " + order.getOrderId());
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            totalPriceTextView.setText("Total Price: " + currencyFormat.format(order.getTotalPrice()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            textViewOrderDate.setText("Order Date: " + dateFormat.format(order.getOrderDate()));

            // Set up Spinner for order status
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(itemView.getContext(), R.array.statusOrder, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);
            spinnerStatus.setSelection(order.getStatus());
            spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (order.getStatus() != position) {
                        order.setStatus(position);
                        OrderApi orderApi = RetrofitClient.getClient().create(OrderApi.class);
                        Integer a = order.getOrderId();
                        Integer b = order.getStatus();
                        Call<Orders> call = orderApi.updateOrder(order.getOrderId(), order.getStatus());
                        call.enqueue(new Callback<Orders>() {
                            @Override
                            public void onResponse(Call<Orders> call, Response<Orders> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(itemView.getContext(), "Order updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(itemView.getContext(), "Failed to update order", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Orders> call, Throwable t) {
                                Toast.makeText(itemView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }
    }
}