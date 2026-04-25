package com.SplitSewa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")  // "user" is a reserved word in MySQL — rename this now
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // use IDENTITY, not AUTO
    private Long id;

    private String username;
    private String password;
    private String email;

    // No groups list here. Navigate via GroupMemberRepository instead.
}