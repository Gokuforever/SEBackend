package com.sorted.commons.beans;

import com.sorted.commons.enums.WeekDay;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BusinessHours {

    private Integer start_time;
    private Integer end_time;
    private List<WeekDay> fixed_off_days;
}
