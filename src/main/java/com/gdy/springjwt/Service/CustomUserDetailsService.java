package com.gdy.springjwt.Service;

import com.gdy.springjwt.DTO.CustomUserDetails;
import com.gdy.springjwt.Entity.UserEntity;
import com.gdy.springjwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //DB에서 조회
        UserEntity userEntity = userRepository.findByUsername(username);

        if(userEntity != null){
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(userEntity);
        }

        return null;
    }
}
