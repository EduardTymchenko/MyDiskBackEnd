package com.tymchenko.mydisk.jwt;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
//    private final DiskUserRole userRole = DiskUserRole.ADMIN;




    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
//        this.role = role;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
