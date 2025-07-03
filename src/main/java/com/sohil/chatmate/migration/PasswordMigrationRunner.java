package com.sohil.chatmate.migration;

import com.sohil.chatmate.entity.User;
import com.sohil.chatmate.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@AllArgsConstructor
@Slf4j
public class PasswordMigrationRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting password migration");
        List<User> users = userRepository.findAll();

        int count = 0;
        for(User user : users){
            String password = user.getPassword();
            if(password!=null && !isBcrypt(password)){
                String encode = passwordEncoder.encode(password);
                user.setPassword(encode);
                count++;
                log.info("Migrated password for the user - {}", user.getUsername());
            }
        }

        if(count == 0)
            log.info("No plain passwords found to migrate!!!");
    }

    private boolean isBcrypt(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

}
