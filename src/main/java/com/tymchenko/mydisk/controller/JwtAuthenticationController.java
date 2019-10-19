package com.tymchenko.mydisk.controller;

import com.tymchenko.mydisk.jwt.JwtRequest;
import com.tymchenko.mydisk.jwt.JwtResponse;
import com.tymchenko.mydisk.jwt.JwtTokenUtil;
import com.tymchenko.mydisk.domain.DiskUser;
import com.tymchenko.mydisk.security.JwtUserDetailsService;

import com.tymchenko.mydisk.service.UserService;
import com.tymchenko.mydisk.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


//аутентифицируем пользователя по имени и паролю
@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private FolderService folderService;


    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authRequest) throws Exception {

        switch (authRequest.getStatus().toLowerCase()) {
            case "add":
                DiskUser diskUser = userService.addUser(authRequest.getUsername(),
                        authRequest.getPassword(), authRequest.getRole());

                if (diskUser == null)
                    throw  new ResponseStatusException(HttpStatus.CONFLICT,"Пользователь с таким именем уже существует");
                folderService.createRootDir(diskUser);
                break;
            case "auth":
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);//???
        }
        authenticate(authRequest.getUsername(), authRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
