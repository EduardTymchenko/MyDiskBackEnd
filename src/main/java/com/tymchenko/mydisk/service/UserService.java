package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.domain.DiskUser;

import java.util.List;

public interface UserService {
    DiskUser getUserByLogin(String login);

    DiskUser addUser(String login, String password, String role);

    List<DiskUser> setDefaultUsers();


}
