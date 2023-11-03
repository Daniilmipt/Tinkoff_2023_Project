package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.model.Role;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDto {
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("user_name")
    private String userName;

    @NotNull
    @JsonProperty("password")
    private String password;

    private Role role;
}
