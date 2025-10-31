package org.example.sciencesocialmedia.service;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.exception.UserNotFoundException;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UserNotFoundException(username);
        }

        return user;
    }

    public User getUserById(String id) {
        User userFromDb = userRepository.findById(id).orElse(null);
        if (userFromDb == null) {
            throw new UserNotFoundException(String.format("Пользователем с id: %s не найден", id));
        }

        return userFromDb;
    }

    public User findByIdWithSubscriptions(String id) {
        User user = userRepository.findByIdWithSubscriptions(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public boolean saveUser(User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb != null) {
            return false;
        }
        user.setRoles(user.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }
}
