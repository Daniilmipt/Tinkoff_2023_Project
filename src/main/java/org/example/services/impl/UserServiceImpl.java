package org.example.services.impl;

import org.example.conf.EncoderConfig;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.Role;
import org.example.model.UserEntity;
import org.example.repositories.Hiber.UserHiberRepository;
import org.example.security.roles.RolesEnum;
import org.example.services.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserHiberRepository userHiberRepository;

    public UserServiceImpl(UserHiberRepository userHiberRepository) {
        this.userHiberRepository = userHiberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userHiberRepository.findUserByUserName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return User
                .withUsername(username)
                .password(user.get().getPassword())
                .roles(user.get().getRole().getName())
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto save(UserEntity userEntity){
        Optional<UserEntity> userDataBase = userHiberRepository.findUserByUserName(userEntity.getUserName());
        if (userDataBase.isPresent()){
            return UserMapper.entityToDto(userDataBase.get());
        }
        Role role = new Role(RolesEnum.USER);

        userEntity.setPassword(EncoderConfig.getPasswordEncoder().encode(userEntity.getPassword()));
        userEntity.setRole(role);
        UserDto userDto = UserMapper.entityToDto(userHiberRepository.save(userEntity));
        userDto.setRole(role);
        return userDto;
    }

    @Override
    @Transactional(isolation= Isolation.READ_COMMITTED, readOnly = true)
    public Optional<UserDto> get(Long userId){
        return UserMapper.optionalEntityToDto(userHiberRepository.findById(userId));
    }

    @Override
    @Transactional(isolation= Isolation.READ_COMMITTED, readOnly = true)
    public Optional<UserDto> getByUserName(String userName){
        return UserMapper.optionalEntityToDto(userHiberRepository.findUserByUserName(userName));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long userId){
        userHiberRepository.deleteById(userId);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUserName(Long userId, String userName){
        userHiberRepository.updateUserName(userId, userName);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updatePassword(Long userId, String password){
        userHiberRepository.updatePassword(userId, EncoderConfig.getPasswordEncoder().encode(password));
    }

}
