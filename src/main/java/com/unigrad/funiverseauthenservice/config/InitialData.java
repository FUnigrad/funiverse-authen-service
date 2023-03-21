package com.unigrad.funiverseauthenservice.config;

import com.unigrad.funiverseauthenservice.entity.Role;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.service.IUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialData implements CommandLineRunner {

    private final IUserService userService;

    public InitialData(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByMail("funigrad2023@gmail.com").isEmpty()) {
            User user = User.builder()
                    .eduMail("funigrad2023@gmail.com")
                    .personalMail("funigrad2023@gmail.com")
                    .password("$2a$12$rUjQqFcAh4pTQejxENutfu5oIZ1XgCTa44JpsgBpoDmGfDKh0mRne")
                    .isActive(true)
                    .role(Role.SYSTEM_ADMIN)
                    .build();

            userService.save(user);
        }
    }
}