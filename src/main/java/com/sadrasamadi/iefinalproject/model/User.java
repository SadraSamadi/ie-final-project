package com.sadrasamadi.iefinalproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private long id;

    @Column
    @NotEmpty(message = "لطفا نام خود را وارد کنید")
    private String name;

    @Column
    private String phone;

    @Column
    @Email(message = "لطفا یک ایمیل صحیح وارد کنید")
    @NotEmpty(message = "لطفا ایمیل خود را وارد کنید")
    private String email;

    @Column
    @Size(min = 6, message = "رمز عبور باید حداقل 6 حرف باشد")
    private String password;

    @Transient
    private String matchPassword;

    @Column
    private String avatar;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String name = role.name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(name);
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Role {

        ADMIN,

        USER

    }

}
