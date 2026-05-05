package com.SplitSewa.repo;

import com.SplitSewa.model.GroupMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    @Transactional
    void deleteByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByUserId(Long userId);
}