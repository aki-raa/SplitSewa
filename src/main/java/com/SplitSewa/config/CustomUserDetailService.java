package com.SplitSewa.config;

import com.SplitSewa.model.UserEntity;
import com.SplitSewa.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<UserEntity>user=userRepo.findUserByEmail(email);

        if(user.isPresent()){
return User.builder()
        .username(user.get().getEmail())
        .password(user.get().getPassword())
        .roles("USER")
        .build();
        }
        throw new UsernameNotFoundException("user not found");
    }
}
