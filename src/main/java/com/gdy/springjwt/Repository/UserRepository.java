package com.gdy.springjwt.Repository;

import com.gdy.springjwt.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    //username을 받아 DB테이블에서 회원을 조회
    UserEntity findByUsername(String username);
}
