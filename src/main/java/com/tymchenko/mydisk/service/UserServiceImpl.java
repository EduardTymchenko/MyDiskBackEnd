package com.tymchenko.mydisk.service;

import com.tymchenko.mydisk.domain.DiskUser;
import com.tymchenko.mydisk.dao.UserRepository;
import com.tymchenko.mydisk.domain.DiskUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public DiskUser getUserByLogin(String login) {
        return userRepository.findByLogin(login);

    }


    @Override
    @Transactional
    public DiskUser addUser(String login, String password, String role) {
        if (userRepository.existsByLogin(login)) return null;
        DiskUserRole diskUserRole;
        try {
             diskUserRole = DiskUserRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException ex){
            diskUserRole = DiskUserRole.USER;
        }
        return userRepository.save(new DiskUser(login, passwordEncoder.encode(password), diskUserRole));
    }


    @Override
    @Transactional
    public List<DiskUser> setDefaultUsers(){
        List<DiskUser> defaultUsers = new ArrayList<>();
        defaultUsers.add(new DiskUser("admin",passwordEncoder.encode("admin"), DiskUserRole.ADMIN));
        defaultUsers.add(new DiskUser("test",passwordEncoder.encode("test"), DiskUserRole.USER));
        userRepository.deleteAll();
        return userRepository.saveAll(defaultUsers);
    }
}
