package org.example.sciencesocialmedia.config;

import lombok.AllArgsConstructor;
import org.example.sciencesocialmedia.entity.User;
import org.example.sciencesocialmedia.service.ArticleService;
import org.example.sciencesocialmedia.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@AllArgsConstructor
public class InitialDataLoader implements ApplicationRunner {

    UserService userService;
    ArticleService articleService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();

        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Testovich");
        user.setBio("Test bio");

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        userService.saveUser(user);
    }
}
