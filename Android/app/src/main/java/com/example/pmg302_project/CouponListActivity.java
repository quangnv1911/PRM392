package com.example.pmg302_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.Coupons.CouponsListAdapter;
import com.example.pmg302_project.apis.CouponApi;
import com.example.pmg302_project.apis.ProductApi;
import com.example.pmg302_project.model.Coupon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CouponsListAdapter couponAdapter;
    private List<Coupon> couponList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);

        recyclerView = findViewById(R.id.recyclerViewCoupons);
        couponAdapter = new CouponsListAdapter(couponList);
        recyclerView.setAdapter(couponAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCoupons();
    }

    private void loadCoupons() {
        CouponApi couponApi = RetrofitClient.getClient().create(CouponApi.class);
        couponApi.getAllCoupons().enqueue(new Callback<List<Coupon>>() {
            @Override
            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    couponList.clear();
                    couponList.addAll(response.body());
                    couponAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Toast.makeText(CouponListActivity.this, "Failed to load coupons", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thêm phương thức cho create, edit, delete coupon tại đây
}
