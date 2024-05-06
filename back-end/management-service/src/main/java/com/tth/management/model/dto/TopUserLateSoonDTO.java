package com.tth.management.model.dto;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TopUserLateSoonDTO {
    String userId;
    Integer count;
    List<String> dates;
}
