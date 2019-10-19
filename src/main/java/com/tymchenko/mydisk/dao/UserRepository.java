package com.tymchenko.mydisk.dao;

import com.tymchenko.mydisk.domain.DiskUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<DiskUser, Long> {
    DiskUser findByLogin(@Param("login") String login);

    boolean existsByLogin(@Param("login") String login);


}
