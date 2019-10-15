package com.tymchenko.mydisk.dao;

import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<DiskUser, Long> {
//    @Query("SELECT u FROM CustomUser u where u.login = :login")
    DiskUser findByLogin(@Param("login") String login);
//
//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM CustomUser u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);


}
