package com.SplitSewa.service;

import com.SplitSewa.dto.UserDto;
import com.SplitSewa.model.UserEntity;
import com.SplitSewa.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto registerUser(UserDto dto) {

        if(userRepo.findUserByEmail(dto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        UserEntity user=new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapToDto(userRepo.save(user));

    }
    private UserDto mapToDto(UserEntity user) {
        UserDto userDto=new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;

    }
}
