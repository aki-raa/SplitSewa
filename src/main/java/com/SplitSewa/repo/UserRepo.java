package com.SplitSewa.repo;

import com.SplitSewa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findUserByEmail(String email);
    Optional<UserEntity> findByUsername(String username); // needed for CustomUserDetailsService
}
