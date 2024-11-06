package com.example.pmg302_project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.model.Coupon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class CouponFormActivity extends AppCompatActivity {

    private EditText editCouponCode, editDiscountValue, editMinOrderValue, editMaxOrderValue, edit_min_order_value;
    private EditText editUsageLimit, editUsageCount, editStartDate, editEndDate;
    private Button btnSaveCoupon;
    private Coupon coupon;
    private Spinner spinnerDiscountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_add);

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
        } else {
            spinnerDiscountType.setSelection(0); // Chọn %
        }

        btnSaveCoupon.setOnClickListener(v -> saveCoupon());
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


    private void saveCoupon() {
        try {
            // Kiểm tra mã giảm giá
            String couponCode = editCouponCode.getText().toString().trim();
            if (couponCode.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
                return;
            }
            coupon.setCouponCode(couponCode);

            // Kiểm tra giá trị giảm giá
            // Kiểm tra giá trị giảm giá
            String discountValueText = editDiscountValue.getText().toString().trim();
            if (discountValueText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập giá trị giảm giá", Toast.LENGTH_SHORT).show();
                return;
            }
            int discountValue = Integer.parseInt(discountValueText);
            if (discountValue <= 0) {
                Toast.makeText(this, "Giá trị giảm giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer selectedDiscountType = spinnerDiscountType.getSelectedItemPosition();
            if (selectedDiscountType == 0) {
                if (discountValue > 100) {
                    Toast.makeText(this, "Giá trị giảm giá theo % phải trong khoảng 0-100%", Toast.LENGTH_SHORT).show();
                    return;
                }
                coupon.setCouponType(0); // 0 cho %
            } else {
                coupon.setCouponType(1); // 1 cho giảm trực tiếp vào tiền
            }

            coupon.setDiscountValue(discountValue);


            // Kiểm tra giá trị đơn hàng tối thiểu
            String minOrderValueText = editMinOrderValue.getText().toString().trim();
            if (minOrderValueText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập giá trị đơn hàng tối thiểu", Toast.LENGTH_SHORT).show();
                return;
            }
            int minOrderValue = Integer.parseInt(minOrderValueText);
            if (minOrderValue < 0) {
                Toast.makeText(this, "Giá trị đơn hàng tối thiểu không được âm", Toast.LENGTH_SHORT).show();
                return;
            }
            coupon.setMinOrderValue(minOrderValue);


            String maxOrderValueText = editMaxOrderValue.getText().toString().trim();
            if (maxOrderValueText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập giá trị đơn hàng tối đa", Toast.LENGTH_SHORT).show();
                return;
            }
            int maxOrderValue = Integer.parseInt(maxOrderValueText);
            if (maxOrderValue < minOrderValue) {
                Toast.makeText(this, "Giá trị đơn hàng tối đa phải lớn hơn giá trị tối thiểu", Toast.LENGTH_SHORT).show();
                return;
            }
            coupon.setMaxOrderValue(maxOrderValue);

            // Kiểm tra số lượng còn lại
            String usageLimitText = editUsageLimit.getText().toString().trim();
            if (usageLimitText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng còn lại", Toast.LENGTH_SHORT).show();
                return;
            }
            int usageLimit = Integer.parseInt(usageLimitText);
            if (usageLimit < 0) {
                Toast.makeText(this, "Số lượng còn lại không được âm", Toast.LENGTH_SHORT).show();
                return;
            }
            coupon.setUsageLimit(usageLimit);

            // Kiểm tra số lượng đã sử dụng
            String usageCountText = editUsageCount.getText().toString().trim();
            if (usageCountText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng đã sử dụng", Toast.LENGTH_SHORT).show();
                return;
            }
            int usageCount = Integer.parseInt(usageCountText);
            if (usageCount < 0 || usageCount > usageLimit) {
                Toast.makeText(this, "Số lượng đã sử dụng phải trong khoảng từ 0 đến số lượng còn lại", Toast.LENGTH_SHORT).show();
                return;
            }
            coupon.setUsageCount(usageCount);

            // Kiểm tra ngày bắt đầu và ngày kết thúc
            String startDateText = editStartDate.getText().toString().trim();
            String endDateText = editEndDate.getText().toString().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            if (startDateText.isEmpty() || endDateText.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ ngày bắt đầu và ngày hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }

            Date startDate = dateFormat.parse(startDateText);
            Date endDate = dateFormat.parse(endDateText);

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Ngày bắt đầu phải trước ngày hết hạn", Toast.LENGTH_SHORT).show();
                return;
            }

            coupon.setStartDate(startDate);
            coupon.setEndDate(endDate);

            // Trả về kết quả cho Activity gọi nó
            Intent resultIntent = new Intent();
            resultIntent.putExtra("coupon", coupon); // Đảm bảo `Coupon` implement `Serializable`
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá trị nhập không hợp lệ. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ. Vui lòng nhập ngày đúng định dạng dd/MM/yyyy.", Toast.LENGTH_SHORT).show();
        }
    }


}

