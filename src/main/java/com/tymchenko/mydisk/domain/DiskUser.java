package com.tymchenko.mydisk.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class DiskUser {
    @Id
    @GeneratedValue
    private long id;

    private String login;
    private String password;

    @Enumerated(EnumType.STRING)
    private DiskUserRole role;

    @OneToMany(mappedBy = "diskUser", cascade = CascadeType.ALL)
    private Set<Folder> folders = new HashSet<>();


    public DiskUser(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = DiskUserRole.USER;
    }
    public DiskUser(String login, String password, DiskUserRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }


    public DiskUser() {}

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DiskUserRole getRole() {
        return role;
    }

    public void setRole(DiskUserRole role) {
        this.role = role;
    }

}
