package com.sorted.commons.beans;

import java.math.BigDecimal;
import java.util.List;

public record CreateZoneRequest(
        String name,
        String zoneId,
        List<List<List<Double>>> coordinates
) {
}
