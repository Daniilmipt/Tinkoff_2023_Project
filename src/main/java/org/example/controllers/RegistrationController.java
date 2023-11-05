package org.example.controllers;

import org.example.dto.AuthenticationDto;
import org.example.security.utils.UserSaver;
import org.example.services.impl.UserServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@Validated
public class RegistrationController {
    private final UserSaver userSaver;

    public RegistrationController(UserServiceImpl userService, AuthenticationManager authenticationManager) {
        this.userSaver = new UserSaver(userService, authenticationManager);
    }

    // регистрация
    @PostMapping("/registration")
    public void addUser(@Valid @RequestBody AuthenticationDto authenticationDto,
                        Model model) {
        userSaver.setModel(model);
        userSaver.addUser(authenticationDto);
    }

    // авторизация
    @PostMapping("/auth")
    public void authorization(@Valid @RequestBody AuthenticationDto authenticationDto,
                                          Model model) {
        userSaver.setModel(model);
        userSaver.authUser(authenticationDto);
    }

    // выход
    @DeleteMapping("/out")
    public void logoutPage() {
        userSaver.logoutUser();
    }
}
