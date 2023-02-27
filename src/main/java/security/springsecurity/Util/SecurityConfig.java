package security.springsecurity.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import security.springsecurity.user.jwt.TokenProvider;

/**
 * WebSecurityConfigurerAdapter: 스프링 시큐리티의 웹 보안 기능의 초기화 및 설정들을 담당하는
 * 담겨있으며 내부적으로 getHttp() 매서드가 실행될 때 HTTPSecurity(인증/인가 API 설정 제공) 클래스를 생성
 *
 * @EnableWebSecurity: Spring Security 활성화
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable() // login 화면 나오지 않도록
                .csrf().disable()
                .cors().disable()
                .sessionManagement() // 세션 관련 strategy 설정 가능
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // jwt를 이용하여 세션이 필요 없기 때문에 세션 정보를 담아두지 않음
                .and()
                .authorizeRequests()
                .antMatchers("users/sign-up").permitAll()
                .antMatchers("users/login").permitAll()
                .and()
                .apply(new JwtSecurityConfig(tokenProvider))
        ;

    }
}
