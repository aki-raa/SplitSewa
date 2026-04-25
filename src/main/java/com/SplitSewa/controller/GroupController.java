package com.SplitSewa.controller;

import com.SplitSewa.dto.member.AddMemberRequest;
import com.SplitSewa.dto.group.CreateGroupRequest;
import com.SplitSewa.dto.member.GroupMemberResponse;
import com.SplitSewa.dto.group.GroupResponse;
import com.SplitSewa.model.UserEntity;
import com.SplitSewa.repo.UserRepo;
import com.SplitSewa.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserRepo userRepo;  // make it final, remove @Autowired

    private UserEntity getCurrentUser(Authentication auth) {
        String email = auth.getName();
        return userRepo.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @RequestBody @Valid CreateGroupRequest req,
            Authentication auth) {
        return ResponseEntity.ok(groupService.createGroup(req.getName(), getCurrentUser(auth)));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<GroupMemberResponse> addMember(
            @PathVariable Long id,
            @RequestBody @Valid AddMemberRequest req,
            Authentication auth) {
        return ResponseEntity.ok(groupService.addMember(id, req.getEmail(), getCurrentUser(auth)));
    }

    @DeleteMapping("/{id}/members/me")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long id,
            Authentication auth) {
        groupService.leaveGroup(id, getCurrentUser(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberResponse>> getMembers(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(groupService.getMembers(id, getCurrentUser(auth)));
    }
}