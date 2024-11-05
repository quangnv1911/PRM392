package com.example.pmg302_project.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.model.Orders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class OrderHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Orders> ordersList;
    private Context context;
    private OnOrderClickListener onOrderClickListener;
    public OrderHistoryAdapter(Context context, List<Orders> ordersList, OnOrderClickListener onOrderClickListener) {
        this.context = context;
        this.ordersList = ordersList;
        this.onOrderClickListener=onOrderClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderhistory, parent, false);
        return new OrderHistoryAdapter.OrdersViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Orders orders = ordersList.get(position);
        OrderHistoryAdapter.OrdersViewHolder ordersHolder = (OrderHistoryAdapter.OrdersViewHolder) holder;
        ordersHolder.txtIdOrderHis.setText("Đơn hàng #"+(position+1));

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(orders.getTotalPrice());
        ordersHolder.txtPriceOrderHis.setText("Tổng tiền: "+formattedPrice + " VNĐ");
        ordersHolder.txtQuanOrderHis.setText("Tổng số lượng: " + orders.getTotalQuantity()+ " sản phẩm");

        String[] statusArray = context.getResources().getStringArray(R.array.statusOrder);
        String status = statusArray[orders.getStatus()];
        ordersHolder.txtStatusOrderHis.setText("Trạng thái: "+status);
        if(orders.getStatus() == 0){
            ordersHolder.btnCancelOrder.setVisibility(View.VISIBLE);
        }else {
            ordersHolder.btnCancelOrder.setVisibility(View.GONE);
        }

        ordersHolder.bind(orders,"Đã hủy", onOrderClickListener);

    }

    public interface OnOrderClickListener {
        void onCancelOrderClick(Orders order, String status);

        void onDetailOrderClick(Orders order);
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    private String ip = COMMONSTRING.ip;


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdOrderHis;
        TextView txtPriceOrderHis;
        TextView txtQuanOrderHis;
        TextView txtStatusOrderHis;
        Button btnCancelOrder;
        Button btnDetailOrder;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIdOrderHis = itemView.findViewById(R.id.txtIdOrderHis);
            txtPriceOrderHis = itemView.findViewById(R.id.txtPriceOrderHis);
            txtQuanOrderHis = itemView.findViewById(R.id.txtQuanOrderHis);
            txtStatusOrderHis = itemView.findViewById(R.id.txtStatusOrderHis);

            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnDetailOrder = itemView.findViewById(R.id.btnDetailOrder);
        }

        public void bind(Orders order,String status, OnOrderClickListener listener) {
            btnCancelOrder.setOnClickListener(v -> listener.onCancelOrderClick(order,status));
            btnDetailOrder.setOnClickListener(v-> listener.onDetailOrderClick(order));
        }
    }

}
