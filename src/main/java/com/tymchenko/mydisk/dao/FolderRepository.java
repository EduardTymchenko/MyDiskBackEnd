package com.tymchenko.mydisk.dao;

import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

//"/rest/search"
    List<Folder> findAllByFolderNameContainingAndBasketAndDiskUser(String searchStr,boolean isBasket, DiskUser currentUser);

    // /rest/getAllFolders
    List<Folder> findAllByBasketAndDiskUser(boolean isBasket, DiskUser currentUser);

    // /rest/getFolders  "folders"
    List<Folder> findAllByFolderPathAndBasketAndDiskUser(String path, boolean isBasket, DiskUser currentUser);

    // /rest/getFolders  "basket"
    List<Folder> findAllByBasketAndIsShowBasketAndDiskUser(boolean isBasket, boolean isShow, DiskUser currentUser);

    // /rest/getFolders "star"
    List<Folder> findAllByBasketAndStarAndDiskUser(boolean isBasket, boolean isStar, DiskUser currentUser);

// /rest/addFolders for check folder
    Folder findByFolderNameAndFolderPathAndBasketAndDiskUser(String name, String path, boolean isBasket,DiskUser currentUser);
// for FolderServiceImpl addFolder
    List<Folder> findAllByFolderNameStartsWithAndFolderPathAndBasketAndDiskUser(String nameStart, String path, boolean isBasket,DiskUser currentUser);

// for FolderServiceImpl delFolderById
    List<Folder> findAllByFolderPathStartsWithAndDiskUser(String path, DiskUser currentUser);

// for FolderServiceImpl updateFolder
    List<Folder> findAllByFolderPathStartsWithAndBasketAndDiskUser(String path, boolean isBasket,DiskUser currentUser);

    // cleardisk
    void deleteAllByDiskUser(DiskUser currentUser);

    // clearBasket
    void deleteAllByDiskUserAndBasket(DiskUser currentUser,boolean isBasket);

    // recoverFolder for get parent ignore isBasket
    Folder findByFolderNameAndFolderPathAndDiskUser(String name, String path,DiskUser currentUser);

}
