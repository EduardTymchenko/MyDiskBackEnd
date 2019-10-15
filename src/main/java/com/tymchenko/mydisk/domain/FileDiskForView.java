package com.tymchenko.mydisk.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class FileDiskForView {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String filePath;
    @NonNull
    private long size;
    @NonNull
    private String date;
    @NonNull
    private boolean star;
    @NonNull
    private boolean basket;


}
