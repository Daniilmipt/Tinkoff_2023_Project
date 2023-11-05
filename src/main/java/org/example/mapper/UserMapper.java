package org.example.mapper;

import org.example.dto.UserDto;
import org.example.model.UserEntity;

import java.util.Optional;

public class UserMapper {


    public static UserDto entityToDto(UserEntity entity){
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setUserName(entity.getUserName());
        userDto.setPassword(entity.getPassword());

        return userDto;
    }

    public static UserEntity dtoToEntity(UserDto dto){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getId());
        userEntity.setUserName(dto.getUserName());
        userEntity.setPassword(dto.getPassword());

        return userEntity;
    }

    public static Optional<UserDto> optionalEntityToDto(Optional<UserEntity> entity){
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
