package com.sorted.commons.entity.beans;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Coordinates {
    private BigDecimal lat;
    private BigDecimal lng;
}
