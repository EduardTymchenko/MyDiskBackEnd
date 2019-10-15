package com.tymchenko.mydisk.exeption;

public class DuplicateNameException extends RuntimeException {
    private String name;
    private String typeObject;
    public DuplicateNameException(String message, String typeObject,String name){
        super(message);
        this.typeObject = typeObject;
        this.name = name;
    }

    public String getTypeObject() {
        return typeObject;
    }

    public String getName() {
        return name;
    }
}
