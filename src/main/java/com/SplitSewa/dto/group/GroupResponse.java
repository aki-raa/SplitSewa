package com.SplitSewa.dto.group;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
}