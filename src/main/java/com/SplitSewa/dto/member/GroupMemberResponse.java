package com.SplitSewa.dto.member;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class GroupMemberResponse {
    private Long userId;
    private String username;
    private String email;
    private LocalDateTime joinedAt;
    private String phone;
}