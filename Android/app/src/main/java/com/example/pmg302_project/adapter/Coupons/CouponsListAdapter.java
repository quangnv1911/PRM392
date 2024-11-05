package com.example.pmg302_project.adapter.Coupons;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Coupon;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CouponsListAdapter extends RecyclerView.Adapter<CouponsListAdapter.CouponViewHolder> {

    private List<Coupon> couponList;
    private OnCouponClickListener listener;

    public interface OnCouponClickListener {
        void onCouponClick(Coupon coupon);     // Xem chi tiết mã giảm giá
        void onEditClick(Coupon coupon);       // Sửa mã giảm giá
        void onDeleteClick(Coupon coupon);     // Xóa mã giảm giá
    }

    public CouponsListAdapter(List<Coupon> couponList, OnCouponClickListener listener) {
        this.couponList = couponList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.couponCode.setText(coupon.getCouponCode() + "(Còn lại: " + coupon.getUsageLimit().toString()+ ")");
        if (coupon.getCouponType() == 0) {
            holder.discountValue.setText("Giảm: " + coupon.getDiscountValue() + "%");
        } else {
            holder.discountValue.setText("Giảm: " + coupon.getDiscountValue() + " VND");
        }

        holder.expiryDate.setText("Ngày hết hạn: " + outputFormat.format(coupon.getEndDate()));
        // Thêm các thông tin khác của Coupon vào đây
        holder.itemView.setOnClickListener(v -> listener.onCouponClick(coupon));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(coupon));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(coupon));

    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView couponCode, discountValue, expiryDate;
        ImageButton btnEdit, btnDelete;
        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            couponCode = itemView.findViewById(R.id.couponCode);
            discountValue = itemView.findViewById(R.id.discountValue);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
