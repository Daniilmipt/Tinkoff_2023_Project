package org.example.security.utils;

import org.example.dto.AuthenticationDto;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.UserEntity;
import org.example.security.exceptions.AuthorizationException;
import org.example.services.impl.UserServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Optional;

public class UserSaver {

    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;

    private Model model;

    public UserSaver(UserServiceImpl userService,
                     AuthenticationManager authenticationManager){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public void setModel(Model model){
        if (this.model == null){
            this.model = model;
        }
    }


    public void addUser(AuthenticationDto authenticationDto){
        UserEntity userEntity = new UserEntity(authenticationDto.getUserName(), authenticationDto.getPassword());
        userService.save(userEntity);
        model.addAttribute("user", UserMapper.entityToDto(userEntity));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (
                                authenticationDto.getUserName(),
                                authenticationDto.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void authUser(AuthenticationDto authenticationDto){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (
                                authenticationDto.getUserName(),
                                authenticationDto.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<UserDto> userDto = userService.getByUserName(authenticationDto.getUserName());
        if (userDto.isEmpty()){
            throw new AuthorizationException("Ошибка при попытки авторизации пользователя", "/auth");
        }
        model.addAttribute("user", userDto.get());
    }

    public void logoutUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthorizationException(
                    "Пользователь не авторизован",
                    "/out"
            );
        } else {
            SecurityContextHolder.clearContext();
            if (model != null) {
                model.addAttribute("user", null);
            }
        }
    }
}
