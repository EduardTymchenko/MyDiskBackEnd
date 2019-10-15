package com.tymchenko.mydisk;

import com.tymchenko.mydisk.domain.DiskUser;
import com.tymchenko.mydisk.service.UserService;
import com.tymchenko.mydisk.service.FileService;
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
    private FileService fileService;

    public static void main(String[] args) {
        SpringApplication.run(MydiskApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
//        UserDisk userDisk;
//        for (int i = 0; i < 6; i++) {
//            userDisk = new UserDisk("User " + i, Integer.toString(i));
//            userDiskService.addUser(userDisk);
//        }
        // Default users
        DiskUser userTest = userService.addUser("test", "test", "USER");
        folderService.createRootDir(userTest);
        DiskUser userAdmin = userService.addUser("admin", "admin", "ADMIN");
        folderService.createRootDir(userAdmin);
//        Folder root = new Folder("","/");
//        folderService.addFolder(root);
//        folderService.addFolder(new Folder());
//        folderService.addFolder(new Folder());
//        folderService.addFolder(new Folder("/my2/new1"));
//        folderService.addFolder(new Folder("/my2/new2"));
//        folderService.addFolder(new Folder("/my2/new3"));
//        Folder folder1 = new Folder("/folder1");
//        folderService.addFolder(folder1);
//
//        FileDisk fileRootDisk1 = new FileDisk("file_root1",root);
//        FileDisk fileRootDisk2 = new FileDisk("file_root2",root);
//        FileDisk fileDisk1 = new FileDisk("fileDisk1",folder1);
//        FileDisk fileDisk2 = new FileDisk("fileDisk2",folder1);
//        fileService.addFile(fileRootDisk1);
//        fileService.addFile(fileRootDisk2);
//        fileService.addFile(fileDisk1);
//        fileService.addFile(fileDisk2);

//        long[] id = {fileDisk1.getId()};
//        fileService.delFiles(id);

    }
}
