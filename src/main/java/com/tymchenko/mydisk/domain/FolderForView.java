package com.tymchenko.mydisk.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class FolderForView {
    @NonNull
    private Long folderId;
    @NonNull
    private String name;
    @NonNull
    private String folderPath;
    @NonNull
    private String folderDate;
    @NonNull
    private String FolderSize;
    @NonNull
    private boolean star;
    @NonNull
    private boolean basket;
}
