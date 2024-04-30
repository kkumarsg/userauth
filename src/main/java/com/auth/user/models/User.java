package com.auth.user.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "user_self")
@Setter
@Getter
public class User extends BaseModel{

    private String name;
    private String email;
    private String hashedPassword;

    @ManyToMany
    private List<Role> roles;
    private boolean isEmailVerified;

}
