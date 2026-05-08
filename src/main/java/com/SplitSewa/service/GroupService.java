package com.SplitSewa.service;

import com.SplitSewa.dto.member.GroupMemberResponse;
import com.SplitSewa.dto.group.GroupResponse;
import com.SplitSewa.model.Group;
import com.SplitSewa.model.GroupMember;
import com.SplitSewa.model.UserEntity;
import com.SplitSewa.repo.GroupMemberRepository;
import com.SplitSewa.repo.GroupRepository;
import com.SplitSewa.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepo userRepository;

    public GroupResponse createGroup(String name, UserEntity creator) {
        Group group = new Group();
        group.setName(name);  // fixed — was Long.parseLong(name)
        group.setCreatedBy(creator);
        groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(creator);
        groupMemberRepository.save(member);

        return mapToResponse(group);
    }

    public GroupMemberResponse addMember(Long groupId, String email, UserEntity requestingUser) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, requestingUser.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        UserEntity newUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found with email: " + email));
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, newUser.getId())) {
            throw new RuntimeException("User is already a member");
        }
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(newUser);
        groupMemberRepository.save(member);
        return mapMemberToResponse(member);
    }

    @Transactional
    public void leaveGroup(Long groupId, UserEntity user) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, user.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, user.getId());
    }

    public List<GroupMemberResponse> getMembers(Long groupId, UserEntity requestingUser) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, requestingUser.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }
        return groupMemberRepository.findByGroupId(groupId)
                .stream().map(this::mapMemberToResponse).toList();
    }

    private GroupResponse mapToResponse(Group g) {
        GroupResponse r = new GroupResponse();
        r.setId(g.getId());
        r.setName(g.getName());                          // fixed — group name
        r.setCreatedBy(g.getCreatedBy().getUsername());  // creator's username
        r.setCreatedAt(g.getCreatedAt());
        return r;
    }

    private GroupMemberResponse mapMemberToResponse(GroupMember m) {
        GroupMemberResponse r = new GroupMemberResponse();
        r.setUserId(m.getUser().getId());
        r.setUsername(m.getUser().getUsername());
        r.setEmail(m.getUser().getEmail());
        r.setJoinedAt(m.getJoinedAt());
        r.setPhone(m.getUser().getPhone());
        return r;
    }
}