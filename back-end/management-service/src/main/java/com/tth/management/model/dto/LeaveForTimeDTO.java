package com.tth.management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveForTimeDTO {
    private String week;
    private long number;
}
