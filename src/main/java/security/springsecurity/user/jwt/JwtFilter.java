package security.springsecurity.user.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * [GenericFilterBean]
 * - 기존 Filter에서 얻어올 수 없던 정보였던 Spring의 설정 정보를 가져올 수 있는 확장된 추상 클래스
 * - 서블릿: 사용자의 요청을 받으면 서블릿을 생성해 메모리에 저장해두고, 같은 클래이언트의 요청을 받으면 생성해둔 객체 재활용
 * - 매 서블릿마다 호출이 됨.
 * [OncePerRequestFilter]
 * - 요청 당 한 번의 실행을 보장하는 것을 목표로 함. (사용자 한 명 당 요청 딱 한 번)
 * - 모든 서블릿에 일관적인 요청을 처리하기 위해 만들어진 필터
 * - 인증, 인가를 거치고 특정 url로 포워딩 하면, 새롭게 인증, 인가를 실행하지 않고(한 번만 실행) 다음 로직으로 진행 가능하도록 함.
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 전처리

        // Header에서 값을 꺼냄
        String jwt = resolveToken(request);
        if(StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt, request)){
            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);

        // 후처리
    }

    /**
     * Header에서 값 꺼냄
     * StringUtils : 문자열 관련 기능을 강화하는 클래스
     * StringUtils.hasText() -> 유효성 검증 유틸 메소드
     * 1. null이 아님
     * 2. 0이 아님
     * 3. 공백이 "", " " 아님
     */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)){
            return token.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
