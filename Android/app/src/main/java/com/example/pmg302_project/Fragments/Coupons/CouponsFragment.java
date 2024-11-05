package com.example.pmg302_project.Fragments.Coupons;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.CouponDetailActivity;
import com.example.pmg302_project.CouponFormActivity;
import com.example.pmg302_project.CouponListActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.Coupons.CouponsListAdapter;
import com.example.pmg302_project.apis.CouponApi;
import com.example.pmg302_project.model.Coupon;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponsFragment extends Fragment implements CouponsListAdapter.OnCouponClickListener {

    private RecyclerView recyclerView;
    private CouponsListAdapter couponAdapter;
    private List<Coupon> couponList;

    // Khai báo các ActivityResultLauncher để thêm và chỉnh sửa mã giảm giá
    private final ActivityResultLauncher<Intent> addCouponLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra("coupon")) {
                        Coupon coupon = (Coupon) data.getSerializableExtra("coupon");
                        couponList.add(coupon);
                        CouponApi couponApi = RetrofitClient.getClient().create(CouponApi.class);
                        couponApi.createCoupon(coupon).enqueue(new Callback<Coupon>() {
                            @Override
                            public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thêm mã giảm giá thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi thêm mã giảm giá", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Coupon> call, Throwable t) {
                                Toast.makeText(getContext(), "Không thể kết nối với server", Toast.LENGTH_SHORT).show();
                            }
                        });
                        couponAdapter.notifyDataSetChanged();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> editCouponLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra("coupon")) {
                        Coupon coupon = (Coupon) data.getSerializableExtra("coupon");
                        int index = findCouponIndexById(coupon.getId());
                        if (index != -1) {
                            couponList.set(index, coupon);


                            // Thực hiện gọi Retrofit để cập nhật mã giảm giá trên server
                            CouponApi couponApi = RetrofitClient.getClient().create(CouponApi.class);
                            couponApi.updateCoupon(coupon.getId(), coupon).enqueue(new Callback<Coupon>() {
                                @Override
                                public void onResponse(Call<Coupon> call, Response<Coupon> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Cập nhật mã giảm giá thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Lỗi khi cập nhật mã giảm giá", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Coupon> call, Throwable t) {
                                    Toast.makeText(getContext(), "Không thể kết nối với server", Toast.LENGTH_SHORT).show();
                                }
                            });
                            couponAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCoupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách mã giảm giá
        couponList = new ArrayList<>();
        couponAdapter = new CouponsListAdapter(couponList, this);
        recyclerView.setAdapter(couponAdapter);

        // Tải danh sách mã giảm giá hoặc thêm các dữ liệu mẫu
        loadCoupons();

        FloatingActionButton fabAddCoupon = view.findViewById(R.id.fab_add_coupon);
        fabAddCoupon.setOnClickListener(v -> addCoupon());

        return view;
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
                    Log.d("CouponListActivity", "Loaded coupons: " + response.body().size());
                } else {
                    Log.e("CouponListActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<Coupon>> call, Throwable t) {
                Log.e("CouponListActivity", "Failed to load coupons: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to load coupons", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Phương thức gọi để thêm mới mã giảm giá
    private void addCoupon() {
        Intent intent = new Intent(getContext(), CouponFormActivity.class);
        addCouponLauncher.launch(intent);
    }

    // Phương thức gọi để chỉnh sửa mã giảm giá
    private void editCoupon(Coupon coupon) {
        Intent intent = new Intent(getContext(), CouponFormActivity.class);
        intent.putExtra("coupon", coupon);
        editCouponLauncher.launch(intent);
    }

    private int findCouponIndexById(int id) {
        for (int i = 0; i < couponList.size(); i++) {
            if (couponList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    // Các phương thức của OnCouponClickListener để xử lý sự kiện nhấn vào nút
    @Override
    public void onCouponClick(Coupon coupon) {
        Intent intent = new Intent(getContext(), CouponDetailActivity.class);
        intent.putExtra("coupon", coupon); // Giả sử `newCoupon` là đối tượng Coupon mới
        startActivity(intent);
    }

    @Override
    public void onEditClick(Coupon coupon) {
        editCoupon(coupon); // Gọi phương thức chỉnh sửa mã giảm giá
    }

    @Override
    public void onDeleteClick(Coupon coupon) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa mã giảm giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {

                    CouponApi couponApi = RetrofitClient.getClient().create(CouponApi.class);
                    couponApi.deleteCoupon(coupon.getId()).enqueue(new Callback<Void>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Xóa mã giảm giá thành công", Toast.LENGTH_SHORT).show();
                                couponList.remove(coupon);
                                couponAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Lỗi khi xóa mã giảm giá", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Không thể kết nối với server", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
