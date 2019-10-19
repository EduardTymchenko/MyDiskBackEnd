package com.tymchenko.mydisk.service;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.domain.DiskUser;

import java.util.List;

public interface FolderService {


    List<Folder> getFoldersBySearch(String searchStr, DiskUser currentUser);
    Folder getFolderByFullName(String fullNameFolder, DiskUser currentUser); //  "/rest/getFiles"

    Folder createRootDir(DiskUser diskUser); //ok
    List<Folder> getAllByPath(String path, DiskUser currentUser);//ok
    List<Folder> getAllActiveFolders(DiskUser currentUser);//ok
    List<Folder> getAllIsBasket(DiskUser currentUser);//ok
    List<Folder> getAllIsStar(DiskUser currentUser);//ok

    void addFolder(String nameFolder,String pathFolder,DiskUser currentUser);
    void deleteFolderById(long idDelFolder, DiskUser currentUser);
    void updateFolder(long folderId, String newName, String newPath, DiskUser currentUser);

    void changeStatusStar(long id);
    void clearDisk(DiskUser currentUser);
    void clearBasket(DiskUser currentUser);
    void recoverFolder(Long id);
    void recoverFullPathFolder(String fullFolderPath, DiskUser currentUser);// for recovery files

}
