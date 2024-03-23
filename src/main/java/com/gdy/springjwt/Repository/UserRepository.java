package com.gdy.springjwt.Repository;

import com.gdy.springjwt.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);
}
