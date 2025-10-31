package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String bio;

    @Transient
    private String passwordConfirm;

    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> roles = new HashSet<>();

    @ManyToMany
    private Set<User> subscribers;

    @ManyToMany
    private Set<User> subscriptions;

    @OneToMany(mappedBy = "authorId")
    private Set<Article> articles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles.contains("USER")) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        } else if (roles.contains("ADMIN")) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
        }
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName;
    }
}
