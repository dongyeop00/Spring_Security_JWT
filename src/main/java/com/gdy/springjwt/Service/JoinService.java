package com.gdy.springjwt.Service;

import com.gdy.springjwt.DTO.JoinDTO;
import com.gdy.springjwt.Entity.UserEntity;
import com.gdy.springjwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDTO joinDTO){
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if(isExist){
            return;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(username);
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        userEntity.setRole("ROLE_ADMIN"); //앞단에 ROLE_이라는 접두사를 붙여야함

        userRepository.save(userEntity);
    }
}
