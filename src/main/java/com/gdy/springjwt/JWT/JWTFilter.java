package com.gdy.springjwt.JWT;

import com.gdy.springjwt.DTO.CustomUserDetails;
import com.gdy.springjwt.Entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
//JWT 검증 필터
//SecurityConfig에 등록해야됨
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾는다.
        String authorization = request.getHeader("Authorization");
        System.out.println("authorization : " + authorization);

        //Authorization 검증
        if(authorization == null || !authorization.startsWith("Bearer ")){//접두사 검사
            System.out.println("Token null");
            filterChain.doFilter(request, response);

            //Authorization이 없거나 접두사가 없으면 종료
            return;
        }

        System.out.println("authorization now");

        //Bearer 접두사 부분 제거 후 순수 토큰 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            System.out.println("Token expired");
            filterChain.doFilter(request, response);

            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값들을 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword"); //비밀번호 값은 토큰에 담겨있지 않지만 임시값만 강제로 넣어줌
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        System.out.println("customUserDetails : " + customUserDetails);

        //Spring Security 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
