package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.model.Coupon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;


public class CouponDetailActivity extends AppCompatActivity {

    private EditText editCouponCode, editDiscountValue, editMinOrderValue, editMaxOrderValue,edit_min_order_value;
    private EditText editUsageLimit, editUsageCount, editStartDate, editEndDate;
    private Button btnSaveCoupon;
    private Coupon coupon;
    private Spinner spinnerDiscountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);

        // Ánh xạ các thành phần giao diện
        editCouponCode = findViewById(R.id.edit_coupon_code);

        editDiscountValue = findViewById(R.id.edit_discount_value);
        editMinOrderValue = findViewById(R.id.edit_min_order_value);
        editMaxOrderValue = findViewById(R.id.edit_max_order_value);
        editUsageLimit = findViewById(R.id.edit_usage_limit);
        editUsageCount = findViewById(R.id.edit_usage_count);
        editStartDate = findViewById(R.id.edit_start_date);
        editEndDate = findViewById(R.id.edit_end_date);
        btnSaveCoupon = findViewById(R.id.btn_save_coupon);
        spinnerDiscountType = findViewById(R.id.spinner_discount_type);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.discount_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiscountType.setAdapter(adapter);
        // Kiểm tra nếu có Intent chứa thông tin mã giảm giá (chỉnh sửa)
        if (getIntent().hasExtra("coupon")) {
            coupon = (Coupon) getIntent().getSerializableExtra("coupon");
            loadCouponData(coupon);
        } else {
            coupon = new Coupon();  // Tạo mới nếu không có
        }
        Integer couponType = coupon.getCouponType();
        if (couponType != null) {
            if (couponType == 0) {
                spinnerDiscountType.setSelection(0); // Chọn %
            } else {
                spinnerDiscountType.setSelection(1); // Chọn giảm trực tiếp
            }
        }else{
            spinnerDiscountType.setSelection(0); // Chọn %
        }
        if (coupon == null || coupon.getId() == null) { // Nếu đây là một mã giảm giá mới
            disableAllFieldsExceptSave();
        }
        Button backButton = findViewById(R.id.btn_back_coupon);
        backButton.setOnClickListener(v -> finish());
    }


    private void disableAllFieldsExceptSave() {
        editCouponCode.setFocusable(false);
        editDiscountValue.setFocusable(false);
        editMinOrderValue.setFocusable(false);
        editMaxOrderValue.setFocusable(false);
        editUsageLimit.setFocusable(false);
        editUsageCount.setFocusable(false);
        editStartDate.setFocusable(false);
        editEndDate.setFocusable(false);
        spinnerDiscountType.setFocusable(false);
        btnSaveCoupon.setEnabled(true); // Để lại nút Save hoạt động
    }
    private void loadCouponData(Coupon coupon) {
        editCouponCode.setText(coupon.getCouponCode());
        editDiscountValue.setText(String.valueOf(coupon.getDiscountValue()));
        editMinOrderValue.setText(String.valueOf(coupon.getMinOrderValue()));
        editMaxOrderValue.setText(String.valueOf(coupon.getMaxOrderValue()));
        editUsageLimit.setText(String.valueOf(coupon.getUsageLimit()));
        editUsageCount.setText(String.valueOf(coupon.getUsageCount()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Chuyển đổi Date thành chuỗi định dạng "dd/MM/yyyy"
        Date startDate = coupon.getStartDate();
        Date endDate = coupon.getEndDate();

        editStartDate.setText(dateFormat.format(startDate));
        editEndDate.setText(dateFormat.format(endDate));
    }



}
