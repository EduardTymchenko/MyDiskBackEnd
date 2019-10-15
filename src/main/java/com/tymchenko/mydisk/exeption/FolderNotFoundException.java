package com.tymchenko.mydisk.exeption;

public class FolderNotFoundException extends RuntimeException {

    public FolderNotFoundException(String message) {
        super(message);
    }
}