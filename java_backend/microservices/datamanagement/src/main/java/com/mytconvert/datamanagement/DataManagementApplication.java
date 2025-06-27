package com.mytconvert.datamanagement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ResponseStatusException;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.service.user.UserService;

@SpringBootApplication
public class DataManagementApplication {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(DataManagementApplication.class, args);
    }

@Bean
public CommandLineRunner createAdminUserRunner() {
    return args -> {
        
        List<String> filteredArgs = Arrays.stream(args)
            .filter(arg -> !arg.startsWith("--"))
            .collect(Collectors.toList());
        
        if (filteredArgs.size() >= 2 && "create-admin".equals(filteredArgs.get(0))) {
            createAdminUser(filteredArgs.get(1), filteredArgs.get(2), filteredArgs.get(3));
        } else {
            System.out.println("Aplicação iniciada! Use o comando 'create-admin' para criar um usuario administrador.");
        }
    };
}

    private void createAdminUser(String name, String email, String password) {
        try {
            User newAdmin = userService.createAdminUser(name, email, password);
            System.out.println("Admin Usuario cadastrado com sucesso: " + newAdmin);
        } catch (ResponseStatusException e) {
            System.out.println("Erro ao criar administrador: " + e.getReason());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
        System.exit(0);
    }
}