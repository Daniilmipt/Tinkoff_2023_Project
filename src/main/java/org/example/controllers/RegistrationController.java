package org.example.controllers;

import org.example.dto.AuthenticationDto;
import org.example.dto.UserDto;
import org.example.security.exceptions.AuthorizationException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.services.impl.UserServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@Validated
public class RegistrationController {
    private final UserServiceImpl userService;
    private final AuthenticationManager authenticationManager;

    public RegistrationController(UserServiceImpl userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // регистрация
    @PostMapping("/registration")
    public void addUser(@Valid @RequestBody AuthenticationDto authenticationDto,
                        Model model) {
        User user = new User(authenticationDto.getUserName(), authenticationDto.getPassword());
        userService.save(user);
        model.addAttribute("user", UserMapper.entityToDto(user));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken
                        (
                                authenticationDto.getUserName(),
                                authenticationDto.getPassword()
                        )
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // авторизация
    @PostMapping("/auth")
    public void authorization(@Valid @RequestBody AuthenticationDto authenticationDto,
                                          Model model) {
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

    // выход
    @DeleteMapping("/out")
    public void logoutPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthorizationException(
                    "Пользователь не авторизован",
                    "/out"
            );
        } else {
            SecurityContextHolder.clearContext();
            model.addAttribute("user", null);
        }
    }
}
