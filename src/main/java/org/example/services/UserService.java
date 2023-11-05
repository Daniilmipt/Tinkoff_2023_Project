package org.example.services;

import org.example.dto.UserDto;
import org.example.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDto save(UserEntity userEntity);
    Optional<UserDto> get(Long userId);
    Optional<UserDto> getByUserName(String userName);
    void delete(Long userId);
    void updateUserName(Long userId, String userName);
    void updatePassword(Long userId, String password);
}
