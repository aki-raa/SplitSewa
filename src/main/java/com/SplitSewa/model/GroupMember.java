package com.SplitSewa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// GroupMember.java
@Data
@Entity
@Table(name = "group_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() { this.joinedAt = LocalDateTime.now(); }
}