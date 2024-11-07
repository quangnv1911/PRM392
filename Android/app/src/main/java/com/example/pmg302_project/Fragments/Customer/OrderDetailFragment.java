package com.example.pmg302_project.Fragments.Customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Orders;
import com.example.pmg302_project.service.CustomerService;
import com.example.pmg302_project.util.RetrofitClientInstance;
import com.google.gson.internal.LinkedTreeMap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailFragment extends Fragment {
    private TextView orderIdTextView;
    private TextView orderDateTextView;
    private TextView totalQuantityTextView;
    private TextView totalPriceTextView;
    private TextView statusTextView;
    private int accountId;
    private static final String ARG_ACCOUNT_ID = "account_id";

    public static OrderDetailFragment newInstance(int accountId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACCOUNT_ID, accountId);  // Ensure ARG_ACCOUNT_ID is used consistently
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        orderIdTextView = view.findViewById(R.id.text_order_id);
        orderDateTextView = view.findViewById(R.id.text_order_date);
        totalQuantityTextView = view.findViewById(R.id.text_total_quantity);
        totalPriceTextView = view.findViewById(R.id.text_total_price);
        statusTextView = view.findViewById(R.id.text_status);

        // Initialize the back button
        FloatingActionButton backButton = view.findViewById(R.id.button_back);

        // Set up the back button click listener
        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(); // Navigate back to the previous fragment
        });

        if (getArguments() != null) {
            accountId = getArguments().getInt(ARG_ACCOUNT_ID, -1);  // Default to -1 if not found
            if (accountId == -1) {
                Log.e("OrderDetailFragment", "accountId was not set correctly");
            } else {
                Log.d("OrderDetailFragment", "Account ID received: " + accountId);
                fetchOrderDetails(accountId);  // Fetch order details with the correct accountId
            }
        } else {
            Log.e("OrderDetailFragment", "Arguments were null, accountId not set");
        }

        return view;
    }

    private void fetchOrderDetails(int accountId) {
        CustomerService customerService = RetrofitClientInstance.getCustomerService();
        Call<Map<String, Object>> call = customerService.getCustomerOrdersWithTotal(accountId);
        Log.e("fetchOrderDetails", "accountId: " + accountId);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();

                    // Parse orders list
                    List<LinkedTreeMap<String, Object>> orders = (List<LinkedTreeMap<String, Object>>) responseBody.get("orders");
                    Log.e("fetchOrderDetails", "orders: " + orders.toString());

                    // Display details of the first order, if available
                    if (!orders.isEmpty()) {
                        LinkedTreeMap<String, Object> firstOrder = orders.get(0);
                        displayOrderDetails(firstOrder);
                    }

                    // Display total purchase amount
                    Double totalPurchaseAmount = (Double) responseBody.get("totalPurchaseAmount");
                    totalPriceTextView.setText(String.valueOf(totalPurchaseAmount));
                } else {
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("fetchOrderDetails", "Failed to fetchOrderDetails: " + t.getMessage());
            }
        });
    }

    private void displayOrderDetails(LinkedTreeMap<String, Object> order) {
        // Set Order ID
        Integer orderId = ((Double) order.get("orderId")).intValue();  // Assuming orderId is returned as a Double
        orderIdTextView.setText(String.valueOf(orderId));

        // Get and format the order date
        Object orderDateObj = order.get("orderDate");
        String formattedDate = "";

        if (orderDateObj instanceof String) {
            // Assuming orderDate is a String in the format "yyyy-MM-dd'T'HH:mm:ss"
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                formattedDate = outputFormat.format(inputFormat.parse((String) orderDateObj));
            } catch (Exception e) {
                Log.e("OrderDetailFragment", "Failed to parse order date: " + e.getMessage());
                formattedDate = "Invalid date";
            }
        } else if (orderDateObj instanceof Long) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            formattedDate = sdf.format(new Date((Long) orderDateObj));
        } else {
            formattedDate = "Unknown date format";
        }

        // Set Order Date
        orderDateTextView.setText(formattedDate);

        // Set Total Quantity
        Integer totalQuantity = ((Double) order.get("totalQuantity")).intValue();  // Assuming totalQuantity is returned as a Double
        totalQuantityTextView.setText(String.valueOf(totalQuantity));

        // Set Total Price (formatting as VND)
        Integer totalPrice = ((Double) order.get("totalPrice")).intValue();  // Assuming totalPrice is returned as a Double
        totalPriceTextView.setText(String.format(Locale.getDefault(), "%,d VND", totalPrice));  // Use %d for integer formatting
// Assuming `order.get("status")` returns a Double value representing the status
        Integer status = ((Double) order.get("status")).intValue();  // Convert status to Integer

// Set status text based on the value of status
        String statusText = (status == 1) ? "Đang xử lý" : "Chờ xử lý";  // Check status value
        statusTextView.setText(statusText);  // Set the appropriate status text

    }
}