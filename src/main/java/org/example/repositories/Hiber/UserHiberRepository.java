package org.example.repositories.Hiber;

import org.example.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserHiberRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByUserName(String userName);

    @Query("update User u set u.userName = :userName where u.id=:userId")
    @Modifying
    void updateUserName(Long userId, String userName);

    @Query("update User u set u.password = :password where u.id=:userId")
    @Modifying
    void updatePassword(Long userId, String password);
}
