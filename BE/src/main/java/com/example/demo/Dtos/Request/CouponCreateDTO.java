package com.example.demo.Dtos.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateDTO {
    private Integer id;
    private String couponCode;//mã của coupon
    private Integer discountValue;//value của coupon
    private Integer minOrderValue;//giá tiền nhỏ nhất được áp coupon
    private Integer maxOrderValue;//giá tiền lớn nhất được áp coupon
    private Date startDate;//ngày bắt đầu sử dụng dc coupon
    private Date endDate;//ngày hết hạn
    private Integer usageLimit;//số lượng limit của coupon
    private Integer usageCount;//số lượng người đã dùng
    private Boolean isActive;//có kích hoạt không
    private Integer createdBy;//tạo bởi
    private Integer couponType;//0-phần trăm, 1-sô
}
