package com.unigrad.funiverseauthenservice.controller;

import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.service.IEmailService;
import com.unigrad.funiverseauthenservice.service.IUserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final IEmailService emailService;

    @GetMapping
    public ResponseEntity<List<User>> get() {

        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user) throws MessagingException, IOException {
        String DEFAULT_PASS = "user";

        user.setPassword(passwordEncoder.encode(DEFAULT_PASS));

        User newUser = userService.save(user);
//        emailService.sendWelcomeEmail(newUser.getWorkspace(), newUser, "user");

        return ResponseEntity.created(null).body(newUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        if (userService.isExist(id)) {
            userService.inactivate(id);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
}