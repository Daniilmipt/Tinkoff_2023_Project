package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AuthenticationDto {
    @NotNull
    @JsonProperty("user_name")
    private String userName;

    @NotNull
    @JsonProperty("password")
    private String password;
}
