package com.SplitSewa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import com.SplitSewa.model.UserEntity;
@Data
@Entity
@Table(name = "split_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
