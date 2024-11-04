package com.example.pmg302_project.adapter.Coupons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Coupon;

import java.util.List;

public class CouponsListAdapter extends RecyclerView.Adapter<CouponsListAdapter.CouponViewHolder> {

    private List<Coupon> couponList;

    public CouponsListAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.couponCode.setText(coupon.getCouponCode());
        holder.discountValue.setText("Discount: " + coupon.getDiscountValue());
        // Thêm các thông tin khác của Coupon vào đây
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView couponCode, discountValue;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            couponCode = itemView.findViewById(R.id.couponCode);
            discountValue = itemView.findViewById(R.id.discountValue);
        }
    }
}
