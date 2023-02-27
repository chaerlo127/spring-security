package security.springsecurity.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import security.springsecurity.user.jwt.JwtFilter;
import security.springsecurity.user.jwt.TokenProvider;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    @Override
    public void configure(HttpSecurity builder){
        JwtFilter jwtFilter = new JwtFilter(tokenProvider);
        /**
         * UsernamePasswordAuthenticationFilter: 설정된 로그인 URL로 오는 요청을 감시하여 유저 인증 처리
         */
        // security filter가 실행되기 전에 jwtFilter가 먼저 실행되도록 jwtfilter를 SecurityFilter 앞에 추가
        builder.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
