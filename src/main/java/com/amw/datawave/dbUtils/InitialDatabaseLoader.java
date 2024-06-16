package com.amw.datawave.dbUtils;

import com.amw.datawave.user.Role;
import com.amw.datawave.user.User;
import com.amw.datawave.user.UserRepository;
import com.amw.datawave.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InitialDatabaseLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setEmail("admin@admin.com");
            userRepository.save(admin);
        }
        if (userRepository.findByEmail("user@user.com").isEmpty()) {
            User user = new User();
            user.setName("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(Role.USER);
            user.setEmail("user@user.com");
            userRepository.save(user);
        }

    }
}
