package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.dao.FileRepository;
import com.tymchenko.mydisk.domain.FileDisk;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.exeption.DuplicateNameException;
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
        FileDisk delFile = fileRepository.findById(idFile).orElseThrow(() ->
                new FileNotFoundException("id = " + idFile));
        boolean isBascket = delFile.getBasket();
        if (!isBascket) {
            delFile.setBasket(true);
        } else {
            fileRepository.deleteById(idFile);
        }
    }

    @Override
    @Transactional
    public void updateFile(long id, String newName, Folder newFolder) {
        FileDisk updateFile = fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException("id = " + id));
        //check rename
        if (newFolder == null && updateFile.getFileName().equalsIgnoreCase(newName)) return;
        //check remove
        if (newName.equals("") && updateFile.getFolder().equals(newFolder)) return;
        FileDisk checkFile = null;
        //rename
        if (!newName.equals("")) {
            checkFile = fileRepository.findByFileNameAndFolderAndBasket(newName, updateFile.getFolder(), false);
            if (checkFile == null) updateFile.setFileName(newName);
        }
        //remove
        if (newFolder != null){
            checkFile = fileRepository.findByFileNameAndFolderAndBasket(updateFile.getFileName(), newFolder, false);
            if (checkFile == null) updateFile.setFolder(newFolder);
        }

        if (checkFile != null) {
            String typeObject = "file";
            String errMessage = "A " + typeObject + " with the name " + checkFile.getFileName() + " already exists!";
            throw new DuplicateNameException(errMessage, typeObject, checkFile.getFileName());
        }
    }

    @Override
    @Transactional
    public void changeStatusStar(long id) {
        FileDisk fileDisk = fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException("id = " + id));
        fileDisk.setStar(!fileDisk.getStar());
    }

    @Override
    @Transactional
    public void addFile(FileDisk fileDisk) {
        FileDisk checkFile = fileRepository.findByFileNameAndFolderAndBasket(fileDisk.getFileName(), fileDisk.getFolder(), false);
        if (checkFile != null) {
            String checkExpansion = getFileExpansion(checkFile.getFileName());
            String checkFileName = checkFile.getFileName().split(checkExpansion)[0];
            List<FileDisk> listFiles = fileRepository.findAllByFileNameStartingWithAndFileNameEndsWithAndFolderAndBasket(
                    checkFileName, checkExpansion, checkFile.getFolder(), false);
            String newName = changeNameFile(fileDisk.getFileName(), listFiles);
            fileDisk.setFileName(newName);
        }
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
    public long getSizeFolderByFullPath(String fullPathFolder, DiskUser currentUser) {
        Long folderSize;
        String name;
        String path;
        String[] pathArray = fullPathFolder.split("/");
        name = pathArray[pathArray.length - 1];
        path = fullPathFolder.substring(0, fullPathFolder.lastIndexOf(name));
        folderSize = fileRepository.sizeFilesInFolder(path, name, currentUser);
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
        FileDisk recFile = fileRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Не удалось восстановить файл"));
        // check name
        FileDisk checkFile = fileRepository.findByFileNameAndFolderAndBasket(recFile.getFileName(),recFile.getFolder(),false);
        if (checkFile != null) throw new DuplicateNameException("","file",checkFile.getFileName());

        recFile.setBasket(false);
        return recFile;
    }

    @Override
    @Transactional
    public List<FileDisk> getFilesBySearch(String searchStr, DiskUser currentUser) {
        return fileRepository.getFilesBySearch(searchStr, currentUser);
    }

    @Override
    @Transactional
    public FileDisk getFileId(Long id) {
        FileDisk fileDisk = fileRepository.findById(id).orElseThrow(() ->
                new FileNotFoundException("id = " + id));
        return fileDisk;
    }

    private String changeNameFile(String fileNameFull, List<FileDisk> listFiles) {
        String fileName = fileNameFull;
        String fileExpansion = getFileExpansion(fileNameFull);

        if (fileExpansion != null) {
            fileName = fileNameFull.split(fileExpansion)[0];
        }
        int indexInsert = fileName.length() + 2;
        String newFileName = fileName + " ()" + fileExpansion;
        StringBuilder sb = new StringBuilder();
        int n = 2;
        boolean isUseName = false;
        while (true) {
            sb.append(newFileName).insert(indexInsert, n);
            for (FileDisk fileDisk : listFiles) {
                if (sb.toString().equals(fileDisk.getFileName())) {
                    isUseName = true;
                    break;
                }
            }
            if (isUseName) {
                n++;
                sb = new StringBuilder();
                isUseName = false;
            } else break;
        }
        return sb.toString();
    }

    private String getFileExpansion(String fullNameFile) {
        int indexExpansion = fullNameFile.lastIndexOf(".");
        if (indexExpansion != -1) {
            return fullNameFile.substring(indexExpansion);
        }
        return null;
    }


}
