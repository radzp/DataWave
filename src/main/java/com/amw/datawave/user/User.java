package com.amw.datawave.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity(name = "user")
@Table(name = "user_details")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    @XmlTransient
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @XmlElement
    public String getPassword() {
        return password;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
