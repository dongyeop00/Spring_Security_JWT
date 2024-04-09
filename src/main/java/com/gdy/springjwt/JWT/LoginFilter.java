package com.gdy.springjwt.JWT;

import com.gdy.springjwt.DTO.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
//로그인 검증을 위한 커스텀 UsernamePasswordAuthentication 필터 작성
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    /*
    1. /login에 post로 요청
    2. AuthenticationFilter가 username, password를 사용하여 Authentication Manager에 넘김
    3. DB로부터 회원 검증 (UserDetails 사용)
    4. 검증이 완료되면 Succeessful Auth가 동작, 동작 성공 시 jwt 토큰 생성, 동작 실패 시 404
     */

    private final AuthenticationManager authenticationManager;
    //JWTUtile 주입
    //SecurityConfig에서 JWTUitl도 주입해줘야한다.
    private final JWTUtil jwtUtil;

    /*
    @RequiredArgsConstructor 없을 시 기본생성자 해줘야함

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil
    }

     */


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{

        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(username);

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token(dto역할)에 담아야함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메서드 (여기서 jwt 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal(); //특정 유저를 가져올 수 있음

        //이름 추출
        String username = customUserDetails.getUsername();

        //권한 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(username, role, 600*600*10L);

        //key : Authorization
        //인증 방식 : Bearer(접두사를 필수로 붙여야한다.) + token
        //인증 헤더 형태 : Authorization : Bearer 인증토큰(String)
        response.addHeader("Authorization", "Bearer " + token);

        System.out.println("성공");
    }


    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
        System.out.println("실패");
    }

}