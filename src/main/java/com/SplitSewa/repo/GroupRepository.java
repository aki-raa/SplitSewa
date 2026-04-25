package com.SplitSewa.repo;

import com.SplitSewa.model.Group;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findGroupById(@Valid Long id);
}
