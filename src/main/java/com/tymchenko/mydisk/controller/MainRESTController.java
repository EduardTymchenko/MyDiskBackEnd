package com.tymchenko.mydisk.controller;

import com.tymchenko.mydisk.domain.*;
import com.tymchenko.mydisk.exeption.ApiError;
import com.tymchenko.mydisk.service.FileService;
import com.tymchenko.mydisk.service.FolderService;
import com.tymchenko.mydisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainRESTController {
    @Value("${disksize}")
    private long fullSizeDisk ;

    @Autowired
    private FileService fileService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserService userService;


    //****** folder
    @GetMapping("/rest/getAllFolders")
    public ResponseEntity<List<FolderForView>> getAllFolders() {
        DiskUser currentUser = getRegisteredUser();
        List<FolderForView> outList = (listFoldersToView(folderService.getAllActiveFolders(currentUser)));
        return new ResponseEntity<>(outList, HttpStatus.OK);
    }

    @GetMapping("/rest/getFolders")
    public ResponseEntity<List<FolderForView>> getFolders(@RequestParam("typeSideMenu") String menuName,
                                                          @RequestParam("path") String path) {
        DiskUser currentUser = getRegisteredUser();
        List<FolderForView> outList;
        switch (menuName) {
            case "folders":
                outList = (listFoldersToView(folderService.getAllByPath(path, currentUser)));
                break;
            case "basket":
                outList = (listFoldersToView(folderService.getAllIsBasket(currentUser)));
                //clear base when basket is empty
                if (outList.size() == 0 && fileService.getAllFilesIsBasket(currentUser).size() == 0) {
                    folderService.clearBasket(currentUser);
                    fileService.clearBasket(currentUser);
                }
                break;
            case "star":
                outList = (listFoldersToView(folderService.getAllIsStar(currentUser)));
                break;
            default:
                outList = new ArrayList<>();
        }
        return new ResponseEntity<>(outList, HttpStatus.OK);
    }

    @GetMapping("/rest/addFolders")
    public ResponseEntity<?> addFolder(@RequestParam("newPath") String pathAddFolder,
                                       @RequestParam("newName") String nameAddFolder) {
        DiskUser currentUser = getRegisteredUser();
        if (pathAddFolder.equals("")) {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка в параметрах запроса");
            apiError.setMessage("Параметр папки \"newPath\"  пустой");
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        if (nameAddFolder.equals("")) {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка в параметрах запроса");
            apiError.setMessage("Параметр имени папки \"newName\"  пустой");
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        folderService.addFolder(nameAddFolder, pathAddFolder, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/delFolder")
    public ResponseEntity<?> delFolder(@RequestParam("id") long idDelFolder) {
        DiskUser currentUser = getRegisteredUser();
        folderService.deleteFolderById(idDelFolder, currentUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/updateFolder")
    public ResponseEntity<?> updateFolder(@RequestParam("id") long currentFolderId,
                                          @RequestParam("newPath") String newPath,
                                          @RequestParam("newName") String newName) {
        if (newName.equals("") && newPath.equals("")) {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Ошибка в параметрах запроса");
            apiError.setMessage("Параметры \"newPath\" и \"newName\" пустые");
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        DiskUser currentUser = getRegisteredUser();
        folderService.updateFolder(currentFolderId, newName, newPath, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//****** files

    @GetMapping("/rest/getFiles")
    public ResponseEntity<List<FileDiskForView>> getFiles(@RequestParam("typeSideMenu") String typeSideMenu,
                                                          @RequestParam("path") String fullFolderPath) {
        DiskUser currentUser = getRegisteredUser();
        List<FileDiskForView> outList;
        switch (typeSideMenu) {
            case "folders":
                Folder folder = folderService.getFolderByFullName(fullFolderPath, currentUser);
                outList = listFilesToView(fileService.getAllFilesInFolder(folder));
                break;
            case "basket":
                outList = listFilesToView(fileService.getAllFilesIsBasket(currentUser));
                break;
            case "star":
                outList = listFilesToView(fileService.getAllFilesIsStar(currentUser));
                break;
            default:
                outList = new ArrayList<>();
        }
        return new ResponseEntity<>(outList, HttpStatus.OK);
    }

    @GetMapping("/rest/delFile")
    public ResponseEntity<?> delFile(@RequestParam("id") long idFile) {
        fileService.delFile(idFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/rest/uploadMultiFiles")
    public ResponseEntity<?> uploadFileMulti(@RequestParam("myfiles") MultipartFile[] files,
                                             @RequestParam("folderPath") String fullNameFolder) throws Exception {
        DiskUser currentUser = getRegisteredUser();
        Folder uploadFolder = folderService.getFolderByFullName(fullNameFolder, currentUser);
        for (MultipartFile file : files) {
            long sizeFile = file.getSize();
            String nameFile = file.getOriginalFilename();
            String mediaType = file.getContentType();
            byte[] bodyFile = file.getBytes();
            fileService.addFile(new FileDisk(nameFile, mediaType, sizeFile, bodyFile, uploadFolder));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/rest/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) {
        FileDisk dbFile = fileService.getFileId(fileId);
        String fileName = dbFile.getFileName();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getTypeFile()))
                .contentLength(dbFile.getSizeFile())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(dbFile.getBodyFile()));
    }


    @GetMapping("/rest/updateFile")
    public ResponseEntity<?> updateFile(@RequestParam("id") long idFile,
                                        @RequestParam("newName") String newName,
                                        @RequestParam("newPath") String newPath) {
        if (newName.equals("") && newPath.equals(""))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не могут быть пустыми параметры \"newName\" и \"newPath\"");
        Folder newFolder = null;
        if (!newPath.equals("")) {
            DiskUser currentUser = getRegisteredUser();
            newFolder = folderService.getFolderByFullName(newPath, currentUser);
        }

        fileService.updateFile(idFile, newName, newFolder);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//****** global

    @GetMapping("/rest/changeStar")
    public ResponseEntity<?> changeStatusStar(@RequestParam("typeMenu") String typeMenu,
                                              @RequestParam("id") long id) {
        switch (typeMenu) {
            case "folder":
                folderService.changeStatusStar(id);
                break;
            case "file":
                fileService.changeStatusStar(id);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "В запросе неверно указан параметр \"typeMenu\"");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/rest/getSize")
    public ArrayList<String> getSize(@RequestParam("pathFolder") String fullPathFolder) {
        DiskUser currentUser = getRegisteredUser();
        ArrayList<String> sizeDisk = new ArrayList<>();

        if (fullPathFolder.equals("")) {
            sizeDisk.add(Long.toString(fullSizeDisk));
            sizeDisk.add(Long.toString(fileService.getCurrentSize(currentUser)));
        } else {
            sizeDisk.add(Long.toString(fileService.getSizeFolderByFullPath(fullPathFolder, currentUser)));

        }
        return sizeDisk;
    }

    @GetMapping("/rest/search")
    public List<Object> search(@RequestParam("searchString") String searchString) {
        DiskUser currentUser = getRegisteredUser();
        List<Object> resultSearch = new ArrayList<>();
        List<FolderForView> foldersList = listFoldersToView(folderService.getFoldersBySearch(searchString, currentUser));
        List<FileDiskForView> filesList = listFilesToView(fileService.getFilesBySearch(searchString, currentUser));
        resultSearch.add(foldersList);
        resultSearch.add(filesList);
        return resultSearch;
    }

    @GetMapping("/rest/default")
    public ResponseEntity<?> setDefaultSettings() {
        List<DiskUser> listDefaultUsers = userService.setDefaultUsers();
        for (DiskUser user : listDefaultUsers) {
            folderService.createRootDir(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/clearDisk")
    public ResponseEntity<?> clearUserDisk() {
        DiskUser currentUser = getRegisteredUser();
        folderService.clearDisk(currentUser);
        folderService.createRootDir(currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/clearBasket")
    public ResponseEntity<?> clearBasket() {
        DiskUser currentUser = getRegisteredUser();
        folderService.clearBasket(currentUser);
        fileService.clearBasket(currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rest/recover")
    public ResponseEntity<?> recover(@RequestParam("typeObject") String typeObject,
                                     @RequestParam("id") Long id) {
        if (typeObject.equals("folder")) {
            folderService.recoverFolder(id);

        } else if (typeObject.equals("file")) {
            FileDisk recFile = fileService.recoverFile(id);
            //восстановить путь к файлу
            String pathForFile;
            if (recFile.getFolder().getFolderName().equals("")) pathForFile = "/";
            else pathForFile = recFile.getFolder().getFolderPath() + recFile.getFolder().getFolderName() + "/";
            folderService.recoverFullPathFolder(pathForFile, recFile.getFolder().getDiskUser());
            Folder newRecFolder = folderService.getFolderByFullName(pathForFile, recFile.getFolder().getDiskUser());
            fileService.updateFile(recFile.getId(),"",newRecFolder);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "В запросе неверно указан параметр \"typeObject\"");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DiskUser getRegisteredUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String userName = ((UserDetails) principal).getUsername();
            return userService.getUserByLogin(userName);
        }
        return null;
    }

    private List<FolderForView> listFoldersToView(List<Folder> listFolders) {
        List<FolderForView> listFoldersView = new ArrayList<>();
        for (Folder folder : listFolders) {
            if (!folder.getFolderName().equals("")) {
                listFoldersView.add(new FolderForView(folder.getId(), folder.getFolderName(),
                        folder.getFolderPath(), folder.getDate(), "", folder.getStar(), folder.getBasket()));
            }
        }
        return listFoldersView;
    }

    private List<FileDiskForView> listFilesToView(List<FileDisk> listFiles) {
        List<FileDiskForView> listFilesView = new ArrayList<>();
        String filePath;
        for (FileDisk file : listFiles) {
            if (file.getFolder().getFolderName().equals("")) filePath = "/";
            else filePath = file.getFolder().getFolderPath() + file.getFolder().getFolderName() + "/";
            listFilesView.add(new FileDiskForView(file.getId(), file.getFileName(), filePath,
                    file.getSizeFile(), file.getDate(), file.getStar(), file.getBasket()));

        }
        return listFilesView;
    }


}