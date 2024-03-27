package com.gdy.springjwt.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody //웹 서버로 동작하지 않고 api서버로 동작하기 때문에 특정한 객체 데이터를 응답하기에 @ResponseBody를 선언
public class MainController {

    @GetMapping("/")
    public String main(){

        //사용자 이름
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //사용자 권한
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> collection = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = collection.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        return "main Contrller" + name + role;
    }
}
