package com.tth.management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveForTypeDTO {
    private String type;
    private Integer count;
}
