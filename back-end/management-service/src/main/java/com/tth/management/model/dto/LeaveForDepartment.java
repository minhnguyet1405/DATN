package com.tth.management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveForDepartment {
    private String department;
    private Integer number;
}
