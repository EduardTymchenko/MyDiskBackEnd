package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.dao.FileRepository;
import com.tymchenko.mydisk.domain.FileDisk;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.exeption.FileNotFoundException;
import com.tymchenko.mydisk.domain.DiskUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private FileRepository fileRepository;

    @Override
    @Transactional
    public List<FileDisk> getAllFilesInFolder(Folder folder) {
        return fileRepository.findAllByFolderAndBasket(folder, false);
    }

    @Override
    @Transactional
    public List<FileDisk> getAllFilesIsStar(DiskUser currentUser) {
        return fileRepository.getAllFilesIsStarForUser(currentUser);
    }

    @Override
    @Transactional
    public List<FileDisk> getAllFilesIsBasket(DiskUser currentUser) {
        return fileRepository.getAllFilesIsBasketForUser(currentUser);
    }

    @Override
    @Transactional
    public void delFile(long idFile) {
        FileDisk delFile = fileRepository.findById(idFile).orElseThrow(()->
                new FileNotFoundException("id = " + idFile));
        boolean isBascket = delFile.getBasket();
        if (!isBascket) {
            delFile.setBasket(true);
//            delFile.setShowBasket(true);
        } else {
            fileRepository.deleteById(idFile);
        }
    }

    @Override
    @Transactional
    public void updateFile(long id, String newName, Folder newFolder) {
        FileDisk updateFile = fileRepository.findById(id).orElseThrow(()->
                new FileNotFoundException("id = " + id));
        //            TODO одинаковые имена
        if (!newName.equals("")) updateFile.setFileName(newName);
        if (newFolder != null) updateFile.setFolder(newFolder);
    }

    @Override
    @Transactional
    public void changeStatusStar(long id) {
        FileDisk fileDisk = fileRepository.findById(id).orElseThrow(()->
                new FileNotFoundException("id = " + id ));
        fileDisk.setStar(!fileDisk.getStar());
    }

    @Override
    @Transactional
    public void addFile(FileDisk fileDisk) {
        fileRepository.save(fileDisk);
    }

    @Override
    @Transactional
    public long getCurrentSize(DiskUser currentUser) {
        Long currentSize = fileRepository.sumSizeFiles(currentUser);
        if (currentSize == null) return 0;
        return currentSize;
    }

    @Override
    @Transactional
    public long getSizeFolderByFullPath(String fullPathFolder,DiskUser currentUser) {
        Long folderSize;
        String name;
        String path;
        String[] pathArray = fullPathFolder.split("/");
        name = pathArray[pathArray.length - 1];
        path = fullPathFolder.substring(0, fullPathFolder.lastIndexOf(name));
        folderSize = fileRepository.sizeFilesInFolder(path,name,currentUser);
        if (folderSize == null) return 0;
        return folderSize;
    }

    @Override
    @Transactional
    public void clearBasket(DiskUser currentUser) {
        List<FileDisk> filesInBasket = fileRepository.getAllFilesIsBasketForUser(currentUser);
        fileRepository.deleteAll(filesInBasket);
    }

    @Override
    @Transactional
    public FileDisk recoverFile(Long id) {
        FileDisk  recFile = fileRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Не удалось восстановить файл"));
        recFile.setBasket(false);
        return recFile;
    }

    @Override
    @Transactional
    public List<FileDisk> getFilesBySearch(String searchStr, DiskUser currentUser) {
        return fileRepository.getFilesBySearch(searchStr,currentUser);
    }

    @Override
    @Transactional
    public FileDisk getFileId(Long id)  {
        FileDisk fileDisk = fileRepository.findById(id).orElseThrow(()->
                new FileNotFoundException("id = " + id ));
        return fileDisk;
    }



}
