package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.dao.FolderRepository;
import com.tymchenko.mydisk.domain.Folder;
import com.tymchenko.mydisk.exeption.DuplicateNameException;
import com.tymchenko.mydisk.exeption.FolderNotFoundException;
import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderRepository folderRepository;

    @Override
    @Transactional
    public Folder createRootDir(DiskUser diskUser) {
        Folder root = new Folder("", "/", diskUser);
        return folderRepository.save(root);
    }

    @Override
    @Transactional
    public List<Folder> getAllActiveFolders(DiskUser currentUser) {
        return folderRepository.findAllByBasketAndDiskUser(false, currentUser);
    }

    @Override
    @Transactional
    public List<Folder> getAllByPath(String path, DiskUser currentUser) {
        return folderRepository.findAllByFolderPathAndBasketAndDiskUser(path, false, currentUser);
    }

    @Override
    @Transactional
    public List<Folder> getAllIsBasket(DiskUser currentUser) {
        return folderRepository.findAllByBasketAndIsShowBasketAndDiskUser(true, true, currentUser);
    }

    @Override
    @Transactional
    public List<Folder> getAllIsStar(DiskUser currentUser) {
        return folderRepository.findAllByBasketAndStarAndDiskUser(false, true, currentUser);
    }


    @Override
    @Transactional
    public void addFolder(String nameFolder, String pathFolder, DiskUser currentUser) {
        Folder checkFolder = folderRepository.findByFolderNameAndFolderPathAndBasketAndDiskUser(nameFolder, pathFolder, false, currentUser);
        if (checkFolder == null) folderRepository.save(new Folder(nameFolder, pathFolder, currentUser));
        else {
            String patternActive = ".+\\s\\(\\d+\\)";
            String nameClear;
            Pattern pattern = Pattern.compile(patternActive);
            Matcher matcher = pattern.matcher(nameFolder);
            if (matcher.matches()) {
                int start = nameFolder.lastIndexOf(" (");
                nameClear = nameFolder.substring(0, start);
            } else nameClear = nameFolder;

            List<Folder> listWithName = folderRepository.findAllByFolderNameStartsWithAndFolderPathAndBasketAndDiskUser(
                    nameClear + " ", pathFolder, false, currentUser);
            String newName = changeNameFolder(nameClear, listWithName, false);
            folderRepository.save(new Folder(newName, pathFolder, currentUser));
        }
    }

    @Override
    @Transactional
    public void updateFolder(long folderId, String newName, String newPath, DiskUser currentUser) {
        Folder updateFolder = folderRepository.findById(folderId).orElseThrow(() ->
                new FolderNotFoundException("id = " + folderId));
        Folder checkFolder = null;
        if (!newPath.equals("")) {
            checkFolder = getFolderByFullName(newPath, currentUser);
            if (checkFolder == null) throw new FolderNotFoundException("полному имени " + newPath);
            if (checkFolder.getId() == updateFolder.getId())
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Нельзя перенести файл сам в себя");
            checkFolder = null;
        }

        if (!newName.equals("") && !newPath.equals(""))
            checkFolder = folderRepository.findByFolderNameAndFolderPathAndBasketAndDiskUser(
                    newName, newPath, false, currentUser);
        else if (!newName.equals("")) {
            checkFolder = folderRepository.findByFolderNameAndFolderPathAndBasketAndDiskUser(
                    newName, updateFolder.getFolderPath(), false, currentUser);
        } else if (!newPath.equals("")) {
            checkFolder = folderRepository.findByFolderNameAndFolderPathAndBasketAndDiskUser(
                    updateFolder.getFolderName(), newPath, false, currentUser);

        }

        if (checkFolder != null) {
            String nameFolder = checkFolder.getFolderName();
            String errMessage = "A folder with the name " + nameFolder + " already exists!";
            String typeObject = "folder";
            throw new DuplicateNameException(errMessage, typeObject, nameFolder);
        }

        String subfoldersPath = updateFolder.getFolderPath() + updateFolder.getFolderName() + "/";
        List<Folder> updateSubfoldersList = folderRepository.findAllByFolderPathStartsWithAndBasketAndDiskUser(
                subfoldersPath, false, currentUser);
        if (!newName.equals("")) updateFolder.setFolderName(newName);
        if (!newPath.equals("")) updateFolder.setFolderPath(newPath);
        String newSubfoldersPath = updateFolder.getFolderPath() + updateFolder.getFolderName() + "/";
        for (Folder folder : updateSubfoldersList) {
            String newFullPath = newSubfoldersPath + folder.getFolderPath().substring(subfoldersPath.length());
            folder.setFolderPath(newFullPath);
        }
    }

    @Override
    @Transactional
    public void deleteFolderById(long idDelFolder, DiskUser currentUser) {
        Folder delFolder = folderRepository.findById(idDelFolder).orElseThrow(() ->
                new FolderNotFoundException("id = " + idDelFolder));
        boolean isBasket = delFolder.getBasket();
        String folderPath = delFolder.getFolderPath() + delFolder.getFolderName() + "/";
        List<Folder> subfoldersList = folderRepository.findAllByFolderPathStartsWithAndDiskUser(folderPath, currentUser);

        if (!isBasket) {
            delFolder.setBasket(true);
            delFolder.setIsShowBasket(true);
            for (Folder folder : subfoldersList) {
                folder.setBasket(true);
            }
        } else {
            delFolder.setIsShowBasket(false);

        }
    }


    @Override
    @Transactional
    public List<Folder> getFoldersBySearch(String searchStr, DiskUser currentUser) {
        return folderRepository.findAllByFolderNameContainingAndBasketAndDiskUser(searchStr, false, currentUser);
    }

    //ok "/rest/getFiles"
    @Override
    @Transactional
    public Folder getFolderByFullName(String fullNameFolder, DiskUser currentUser) {
        if (fullNameFolder.equals(""))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Полное имя папки не может быть пустым");
        String name;
        String path;
        if (fullNameFolder.equals("/")) {
            name = "";
            path = "/";
        } else {
            String[] pathArray = fullNameFolder.split("/");
            name = pathArray[pathArray.length - 1];
            path = fullNameFolder.substring(0, fullNameFolder.lastIndexOf(name));
        }
        return folderRepository.findByFolderNameAndFolderPathAndBasketAndDiskUser(name, path, false, currentUser);
    }


    @Override
    @Transactional
    public void changeStatusStar(long id) {
        Folder folder = folderRepository.findById(id).orElseThrow(() ->
                new FolderNotFoundException("id = " + id));
        folder.setStar(!folder.getStar());
    }

    @Override
    @Transactional
    public void clearDisk(DiskUser currentUser) {
        folderRepository.deleteAllByDiskUser(currentUser);
    }

    @Override
    @Transactional
    public void clearBasket(DiskUser currentUser) {
        folderRepository.deleteAllByDiskUserAndBasket(currentUser, true);
    }

    @Override
    @Transactional
    public void recoverFolder(Long id) {
        Folder recFolder = folderRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Не удалось восстановить папку"));
        recFolder.setIsShowBasket(false);
        recFolder.setBasket(false);
        Folder parentFolder;
        String fullNameParentFolder = recFolder.getFolderPath();
        String folderName;
        String folderPath;
        while (true) {
            if (fullNameParentFolder.equals("/")) break;
            String[] pathArray = fullNameParentFolder.split("/");
            folderName = pathArray[pathArray.length - 1];
            folderPath = fullNameParentFolder.substring(0, fullNameParentFolder.lastIndexOf(folderName));
            parentFolder = folderRepository.findByFolderNameAndFolderPathAndDiskUser(folderName,folderPath,recFolder.getDiskUser());
            if (parentFolder == null) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Не удалось восстановить папку");
            parentFolder.setBasket(false);
            parentFolder.setIsShowBasket(false);
            fullNameParentFolder = parentFolder.getFolderPath();
        }
    }


    // TODO
    private String changeNameFolder(String name, List<Folder> folderList, boolean isBasket) {
        StringBuilder sb = new StringBuilder(name);
        if (isBasket) sb.append("_bin (2)");
        else sb.append(" (2)");
        int index = 2;
        boolean isUseName;
        do {
            isUseName = false;
            for (Folder folder : folderList) {
                if (folder.getFolderName().equals(sb.toString())) {
                    isUseName = true;
                    break;
                }
            }
            if (isUseName) {
                index++;
                int lengthDel = Integer.toString(index).length();
                int startDel = name.length() + 2;
                sb.delete(startDel, startDel + lengthDel + 1);
                sb.append(index).append(")");
            } else break;

        } while (true);
        return sb.toString();
    }
}
