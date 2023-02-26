package security.springsecurity.user.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import security.springsecurity.Util.exception.BaseException;
import security.springsecurity.Util.exception.BaseResponseStatus;
import security.springsecurity.user.DAO.TokenDto;

import javax.servlet.ServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분 - 1000 * 60 * 30
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일 - 1000 * 60 * 60 * 24 * 7

    private final Key key;
    private final UserDetailsService userDetailsService;

    public TokenProvider(@Value("${jwt.secret}") String secret, UserDetailsService userDetailsService){
        // Decoders, Keys는 jwt 의존성 주입해야지 import 가능
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }

    /**
     * 토큰 생성
     * @param authentication
     * @return TokenDto
     */
    public TokenDto createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();


        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // payload "sub" : "name"
                .claim(AUTHORITIES_KEY, authorities) // payload "auth": "ROLE_USER"
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // payload
                .signWith(key, SignatureAlgorithm.HS512) // signature
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS512) // signature
                .compact();

        return new TokenDto(accessToken, refreshToken);
    }

    /**
     * 토큰 복호화
     * @param accessToken
     * @return Authentication
     * @throws BaseException
     */
    public Authentication getAuthentication(String accessToken) throws BaseException {
        // Claims: 사용자에 대한 프로퍼티나 속성
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null){
            throw new BaseException(BaseResponseStatus.EXPIRED_JWT_TOKEN);
        }

        // Claims에서 권한 정보 가져오기

        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, ", ", principal.getAuthorities());
    }

    /**
     * 만료된 토큰이라도, 재발급을 위해 정보 return
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token, ServletRequest request) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // header에 exception이라는 이름으로 에러 전달
            log.info("잘못된 JWT 서명입니다.");
            request.setAttribute("exception", "MalformedJwtException");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            request.setAttribute("exception", "ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            request.setAttribute("exception", "UnsupportedJwtException");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            request.setAttribute("exception", "IllegalArgumentException");
        }
        return false;
    }

}
