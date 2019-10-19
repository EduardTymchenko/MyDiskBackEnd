package com.tymchenko.mydisk;

import com.tymchenko.mydisk.domain.DiskUser;
import com.tymchenko.mydisk.service.UserService;
import com.tymchenko.mydisk.service.FolderService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor

public class MydiskApplication implements ApplicationRunner {
    private UserService userService;
    private FolderService folderService;

    public static void main(String[] args) {
        SpringApplication.run(MydiskApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) {
        // Default users
        DiskUser userTest = userService.addUser("test", "test", "USER");
        folderService.createRootDir(userTest);
        DiskUser userAdmin = userService.addUser("admin", "admin", "ADMIN");
        folderService.createRootDir(userAdmin);

    }
}
