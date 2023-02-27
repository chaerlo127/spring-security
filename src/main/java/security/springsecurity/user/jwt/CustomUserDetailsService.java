package security.springsecurity.user.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import security.springsecurity.user.entity.User;
import security.springsecurity.user.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * authenticationManagerBuilder.getObject().authenticate(authenticationToken) 메소드 실행 시
     * loadUserByUsername 함수가 실행 됨.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findById(Long.parseLong(username))
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(username + "의 id를 찾을 수 없습니다."));
    }

    private UserDetails createUser(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().toString());
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUserIdx()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
