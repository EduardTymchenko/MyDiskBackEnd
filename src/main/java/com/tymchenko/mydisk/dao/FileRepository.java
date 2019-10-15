package com.tymchenko.mydisk.dao;

import com.tymchenko.mydisk.domain.FileDisk;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.data.jpa.repository.JpaRepository;
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
            "where fd.fileName like concat('%',?1,'%') and f.basket=false and u = ?2" )
    List<FileDisk> getFilesBySearch(String searchStr,DiskUser currentUser);


    @Query("SELECT SUM (fd.sizeFile) from FileDisk fd join fd.folder as f join f.diskUser as u where u = ?1")
    Long sumSizeFiles(DiskUser currentUser);

    @Query("SELECT SUM (fd.sizeFile) from FileDisk as fd join fd.folder as f join f.diskUser as u " +
            "where u = ?3 and (f.folderPath like concat(?1,?2,'/','%') or (f.folderPath = ?1 and f.folderName = ?2))")
    Long sizeFilesInFolder(String pathFolder, String nameFolder, DiskUser currentUser);


}
