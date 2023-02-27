package security.springsecurity.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import security.springsecurity.Util.exception.BaseException;
import security.springsecurity.Util.exception.BaseResponseStatus;
import security.springsecurity.user.DAO.TokenDto;
import security.springsecurity.user.DAO.UserDto;
import security.springsecurity.user.Role;
import security.springsecurity.user.entity.UserEntity;
import security.springsecurity.user.jwt.TokenProvider;
import security.springsecurity.user.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenDto signUp(UserDto userDto) throws BaseException {
        if(this.userRepository.existsByUserId(userDto.getUserId())){
            throw new BaseException(BaseResponseStatus.EXIST_EXCEPTION);
        }
        String password = userDto.getPassword();
        try{
            String encodedPwd = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPwd);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.EXIST_EXCEPTION);
        }
        UserEntity user = UserEntity.builder()
                .userId(userDto.getUserId())
                .password(userDto.getPassword())
                .role(Role.ROLE_USER)
                .refreshToken("null")
                .build();
        this.userRepository.save(user);
        return createToken(user, password);
    }

    private TokenDto createToken(UserEntity user, String password) {
        /**
         * 에러 발생:
         * BadCredentialsException: 자격 증명에 실패하였습니다. 라는 에러 발생
         *
         *
         * 에러 해결:
         * 암호화된 비밀번호 값이 아니라, 실제 사용자가 입력하는 비밀번호로 authentication를 받아와야 함.
         * loadUserByUsername로 userName과 password를 비교하기 때문
         */
        // userIdx 와 비밀번호를 바탕으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(String.valueOf(user.getUserIdx()), password);
        // 실제로 검증
        // CustomUserDetailsService의 loadUserByUsername 함수가 실행되어 사용자 체크
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // token 생성
        TokenDto tokenDto = this.tokenProvider.createToken(authentication);
        user.setRefreshToken(tokenDto.getRefreshToken());

        return tokenDto;
    }
}
