package com.tymchenko.mydisk.domain;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "files")
public class FileDisk {
    @Id
    @GeneratedValue
    private Long id;
    private String fileName;
    private String typeFile;
    private long sizeFile;
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM uuuu  HH:mm:ss"));
    private Boolean basket = false;
    private Boolean isShowBasket= false;
    private Boolean star = false;
    @Lob
    private byte[] bodyFile;

    @ManyToOne
    @JoinColumn(name = "id_folder")
    private Folder folder;

    public FileDisk() {
    }


    //Для контроллера
    public FileDisk(String fileName, String typeFile, long sizeFile, byte[] bodyFile, Folder folder) {
        this.fileName = fileName;
        this.typeFile = typeFile;
        this.sizeFile = sizeFile;
        this.bodyFile = bodyFile;
        this.folder = folder;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }

    public Boolean getBasket() {
        return basket;
    }

    public void setBasket(Boolean basket) {
        this.basket = basket;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public byte[] getBodyFile() {
        return bodyFile;
    }

    public void setBodyFile(byte[] bodyFile) {
        this.bodyFile = bodyFile;
    }

    public long getSizeFile() {
        return sizeFile;
    }

    public void setSizeFile(long sizeFile) {
        this.sizeFile = sizeFile;
    }

    public String getDate() {
        return date;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public Boolean getShowBasket() {
        return isShowBasket;
    }

    public void setShowBasket(Boolean showBasket) {
        isShowBasket = showBasket;
    }

}