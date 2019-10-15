package com.tymchenko.mydisk.domain;

public enum DiskUserRole {
    ADMIN, USER;

    @Override
    public String toString() {
        return name();
    }
}
