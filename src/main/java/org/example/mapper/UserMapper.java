package org.example.mapper;

import org.example.conf.EncoderConfig;
import org.example.dto.UserDto;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserMapper {


    public static UserDto entityToDto(User entity){
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setUserName(entity.getUserName());
        userDto.setPassword(entity.getPassword());

        return userDto;
    }

    public static User dtoToEntity(UserDto dto){
        User user = new User();
        user.setId(dto.getId());
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());

        return user;
    }

    public static Optional<UserDto> optionalEntityToDto(Optional<User> entity){
        if (entity.isEmpty()){
            return Optional.empty();
        }
        UserDto userDto = new UserDto();
        userDto.setId(entity.get().getId());
        userDto.setUserName(entity.get().getUserName());
        userDto.setPassword(entity.get().getPassword());

        return Optional.of(userDto);
    }
}
