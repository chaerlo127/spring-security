package security.springsecurity.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import security.springsecurity.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
