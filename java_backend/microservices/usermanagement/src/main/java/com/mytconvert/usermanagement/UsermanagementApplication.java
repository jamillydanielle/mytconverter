package com.mytconvert.usermanagement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.service.UserService;

@SpringBootApplication
public class UsermanagementApplication {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(UsermanagementApplication.class, args);
    }

@Bean
public CommandLineRunner createAdminUserRunner() {
    return args -> {
        System.out.println("Arguments received: " + String.join(", ", args));
        
        List<String> filteredArgs = Arrays.stream(args)
            .filter(arg -> !arg.startsWith("--"))
            .collect(Collectors.toList());
        
        if (filteredArgs.size() >= 2 && "create-admin".equals(filteredArgs.get(0))) {
            createAdminUser(filteredArgs.get(1), filteredArgs.get(2));
        } else {
            System.out.println("Application started normally. Use 'create-admin' command to create an admin user.");
        }
    };
}

    private void createAdminUser(String name, String email) {
        try {
            User newAdmin = userService.createAdminUser(name, email);
            System.out.println("Admin user created successfully: " + newAdmin);
        } catch (ResponseStatusException e) {
            System.out.println("Error creating admin user: " + e.getReason());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
        System.exit(0);
    }
}