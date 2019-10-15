package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.domain.FileDisk;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.domain.DiskUser;

import java.util.List;

public interface FileService {
    FileDisk getFileId(Long id) ;

    List<FileDisk> getAllFilesInFolder(Folder folder);

    List<FileDisk> getAllFilesIsStar(DiskUser currentUser);

    List<FileDisk> getAllFilesIsBasket(DiskUser currentUser);

    List<FileDisk> getFilesBySearch(String searchStr, DiskUser currentUser);

    void addFile(FileDisk fileDisk);

    void delFile(long idFile);

    void updateFile(long id,String newName,Folder newFolder);

    void changeStatusStar(long id);

    long getCurrentSize(DiskUser currentUser);

    long getSizeFolderByFullPath(String fullPathFolder, DiskUser currentUser);

    void clearBasket(DiskUser currentUser);

    FileDisk recoverFile(Long id);
}
