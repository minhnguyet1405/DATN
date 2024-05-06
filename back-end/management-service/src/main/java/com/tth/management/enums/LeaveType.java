package com.tth.management.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum LeaveType {

    LEAVE("LEAVE","Nghỉ phép"),
    SICK("SICK","Nghỉ ốm"),
    ABSENT("ABSENT", "Vắng mặt"),
    ASSIGNMENT("ASSIGNMENT", "Đi công tác");

    private String code;

    private String description;

    LeaveType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static LeaveType of(String code) {
        LeaveType[] validFlags = LeaveType.values();
        for (LeaveType validFlag : validFlags) {
            if (validFlag.code().equalsIgnoreCase(code)) {
                return validFlag;
            }
        }
        return null;
    }
}
