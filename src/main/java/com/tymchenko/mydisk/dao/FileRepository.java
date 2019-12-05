package com.tymchenko.mydisk.dao;

import com.tymchenko.mydisk.domain.FileDisk;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<FileDisk, Long> {
    //    getAllFilesInFolder
    List<FileDisk> findAllByFolderAndBasket(Folder folder, boolean isBasket);

    //    getAllFilesIsStar
    @Query("SELECT fd from FileDisk as fd join fd.folder as f join f.diskUser as u " +
            "where u = ?1 and f.basket = false and  fd.star = true and fd.basket = false")
    List<FileDisk> getAllFilesIsStarForUser(DiskUser currentUser);

    //    getAllFilesIsBasket
    @Query("SELECT fd from FileDisk as fd join fd.folder as f join f.diskUser as u " +
            "where u = ?1 and fd.basket = true ")
    List<FileDisk> getAllFilesIsBasketForUser(DiskUser currentUser);

    @Query("SELECT fd from FileDisk as fd join fd.folder as f join f.diskUser as u " +
            "where fd.fileName like concat('%',?1,'%') and f.basket=false and u = ?2")
    List<FileDisk> getFilesBySearch(String searchStr, DiskUser currentUser);


    @Query("SELECT SUM (fd.sizeFile) from FileDisk fd join fd.folder as f join f.diskUser as u where u = ?1")
    Long sumSizeFiles(DiskUser currentUser);

    @Query("SELECT SUM (fd.sizeFile) from FileDisk as fd join fd.folder as f join f.diskUser as u " +
            "where fd.basket = false and u = ?3  and (f.folderPath like concat(?1,?2,'/','%') or (f.folderPath = ?1 and f.folderName = ?2))")
    Long sizeFilesInFolder(String pathFolder, String nameFolder, DiskUser currentUser);

    // get file by name for check
    FileDisk findByFileNameAndFolderAndBasket(String fileName, Folder folder, boolean isBasket);

    // addFile for check equal name  file
    @Query("SELECT fd.fileName from FileDisk as fd join fd.folder as f " +
            "where f = ?3 and fd.basket = ?4 and fd.fileName like concat(?1,'%',?2)")
    List<String> findAllIncludeFileName(String fileName, String expansion, Folder folder, boolean isBasket);

    // check to exist for update file
    boolean existsByIdAndBasket(Long id, boolean isBasket);
    // get update file name
    @Query("SELECT fd.fileName from FileDisk as fd where fd.id = ?1")
    String  getUpdateFileName(Long id);

    // get folder update file
    @Query("SELECT fd.folder from FileDisk as fd where fd.id = ?1")
    Folder getFolderUpdateFile(Long id);

    // check equals file name in folder
    boolean existsFileDiskByFileNameAndFolderAndBasket(String fileName,Folder folder, boolean isBasket);

    // update file name
    @Modifying
    @Query("update FileDisk fd set fd.fileName = ?2 where fd.id = ?1")
    int  updateFileName(Long id, String fileName);

    // update file folder
    @Modifying
    @Query("update FileDisk fd set fd.folder = ?2 where fd.id = ?1")
    int  updateFileFolder(Long id, Folder newFolder);

    // update file name and folder
    @Modifying
    @Query("update FileDisk fd set fd.fileName = ?2, fd.folder = ?3  where fd.id = ?1")
    int  updateFileNameAndFolder(Long id, String fileName, Folder newFolder);
}
