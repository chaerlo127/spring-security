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
         * ?????? ??????:
         * BadCredentialsException: ?????? ????????? ?????????????????????. ?????? ?????? ??????
         *
         *
         * ?????? ??????:
         * ???????????? ???????????? ?????? ?????????, ?????? ???????????? ???????????? ??????????????? authentication??? ???????????? ???.
         * loadUserByUsername??? userName??? password??? ???????????? ??????
         */
        // userIdx ??? ??????????????? ???????????? AuthenticationToken ??????
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(String.valueOf(user.getUserIdx()), password);
        // ????????? ??????
        // CustomUserDetailsService??? loadUserByUsername ????????? ???????????? ????????? ??????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // token ??????
        TokenDto tokenDto = this.tokenProvider.createToken(authentication);
        user.setRefreshToken(tokenDto.getRefreshToken());

        return tokenDto;
    }
}
