package com.gdy.springjwt.Config;

import com.gdy.springjwt.JWT.JWTFilter;
import com.gdy.springjwt.JWT.JWTUtil;
import com.gdy.springjwt.JWT.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration //config로 관리되는 어노테이션
@EnableWebSecurity //security로 관리되는 어노테이션
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager가 인자로 받음 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;


    //비밀번호는 암호화 하여 관리한다. 
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록 (addFilterAT에 사용하기 위해)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .cors((cors)->cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); //허용할 front 서버 주소
                                configuration.setAllowedMethods(Collections.singletonList("*")); //허용할 메서드 ex)get post 등등
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*")); //허용할 헤더
                                configuration.setMaxAge(3600L); //시간

                                configuration.setExposedHeaders(Collections.singletonList("Authorization")); //front에 Authorization으로 jwt 전달

                                return configuration;
                            }
                        }));


        //csrf disable
        // 세션 방식은 세션이 고정되있기 때문에 csrf 공격을 위해 방어해줘야 한다.
        // jwt 방식은 csrf 공격을 방어하지 않아도 되기때문에 disable 해준다.
        http
                .csrf((auth)->auth.disable());

        //Form 로그인 방식 disable
        http
                .formLogin((auth)->auth.disable());

        //http basic 인증 방식 disable, 로그인 인증창이 뜨지 않게 비활성화
        http
                .httpBasic((auth)->auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/login", "/", "/join").permitAll() // /login, / , /join은 모든 사용자가 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN") //admin 권한을 가진 사람만 /admin 주소 가능 가능
                        .anyRequest().authenticated()); //위 외에 경로는 로그인한 사용자만 접근 가능

        //JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        /*
        at : 원하는 자리에 등록 (등록할 필터 객체, 원하는 필터 위치)
        before : 해당 필터 전에 등록
        after : 해당 필터 후에 등록
         */
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
