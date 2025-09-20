package com.sorted.commons.beans;

import com.sorted.commons.enums.UsageStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldNameConstants
@Data
@Builder
public class CouponUsage {
    @Field("user_id")
    private String userId;
    @Field("order_id")
    private String orderId;
    @Field("discount_amount")
    private BigDecimal discountAmount; // Actual discount applied
    @Field("used_at")
    private LocalDateTime usedAt;
    @Field("status")
    private UsageStatus status;
}
