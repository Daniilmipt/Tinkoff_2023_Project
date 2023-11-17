package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Role role;

    public void setRole(Role role) {
        this.role = role;
        this.role.setUserEntity(this);
    }

    public UserEntity(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
}
