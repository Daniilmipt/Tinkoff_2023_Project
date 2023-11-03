package org.example.services.impl.Hiber;

import org.example.conf.EncoderConfig;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.Role;
import org.example.model.User;
import org.example.repositories.Hiber.UserHiberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService{
    private final UserHiberRepository userHiberRepository;

    public UserServiceImpl(UserHiberRepository userHiberRepository) {
        this.userHiberRepository = userHiberRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userHiberRepository.findUserByUserName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.get().getPassword())
                .roles(user.get().getRole().getName())
                .build();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto save(User user){
        Optional<User> userDataBase = userHiberRepository.findUserByUserName(user.getUserName());
        if (userDataBase.isPresent()){
            return UserMapper.entityToDto(userDataBase.get());
        }
        Role role = new Role("USER");

        user.setPassword(EncoderConfig.getPasswordEncoder().encode(user.getPassword()));
        user.setRole(role);
        UserDto userDto = UserMapper.entityToDto(userHiberRepository.save(user));
        userDto.setRole(role);
        return userDto;
    }

    @Transactional(isolation= Isolation.READ_COMMITTED, readOnly = true)
    public Optional<UserDto> get(Long userId){
        return UserMapper.optionalEntityToDto(userHiberRepository.findById(userId));
    }

    @Transactional(isolation= Isolation.READ_COMMITTED, readOnly = true)
    public Optional<UserDto> getByUserName(String userName){
        return UserMapper.optionalEntityToDto(userHiberRepository.findUserByUserName(userName));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long userId){
        userHiberRepository.deleteById(userId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUserName(Long userId, String userName){
        userHiberRepository.updateUserName(userId, userName);
    }
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updatePassword(Long userId, String password){
        userHiberRepository.updatePassword(userId, EncoderConfig.getPasswordEncoder().encode(password));
    }

}
