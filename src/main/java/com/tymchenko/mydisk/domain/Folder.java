package com.tymchenko.mydisk.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "folderSystem")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String folderPath;
    private String folderName;
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM uuuu  HH:mm:ss"));
    private Boolean star = false;
    private Boolean basket = false;
    private Boolean isShowBasket= false;
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private Set<FileDisk> fileDisks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "id_user")
    private DiskUser diskUser;


    public Folder() {
    }


    public Folder(String folderName, String folderPath, DiskUser diskUser) {
        this.folderName = folderName;
        this.folderPath = folderPath;
        this.diskUser = diskUser;
    }

    public Folder(String fullPathFolder, DiskUser diskUser) {
        if (fullPathFolder.equals("/")){
            this.folderName = "";
            this.folderPath = "/";
        } else {
            String[] pathArray = fullPathFolder.split("/");
            this.folderName = pathArray[pathArray.length - 1];
            this.folderPath = fullPathFolder.substring(0, fullPathFolder.lastIndexOf(folderName));
        }
        this.diskUser = diskUser;
    }
//    public Folder(String folderName, String folderPath) {
//        this.folderName = folderName;
//        this.folderPath = folderPath;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public Boolean getStar() {
        return star;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public Boolean getBasket() {
        return basket;
    }

    public void setBasket(Boolean basket) {
        this.basket = basket;
    }

    public Boolean getIsShowBasket() {
        return isShowBasket;
    }

    public void setIsShowBasket(Boolean isShowBasket) {
        this.isShowBasket = isShowBasket;
    }

    public DiskUser getDiskUser() {
        return diskUser;
    }

    public void setDiskUser(DiskUser diskUser) {
        this.diskUser = diskUser;
    }
}
