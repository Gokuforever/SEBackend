package com.sorted.commons.beans;

import java.math.BigDecimal;

public record FeeResult(BigDecimal revenue, BigDecimal cost) {

    @Override
    public String toString() {
        return "Revenue (fee): " + revenue + " paise, Cost (actual): " + cost + " paise";
    }
}
