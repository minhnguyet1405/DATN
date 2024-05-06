package com.tth.management.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeaveForTypeDTO {
    private String type;
    private Integer count;

    public LeaveForTypeDTO(String type, Integer count) {
        this.setType(type);
        this.setCount(count);
    }
}
