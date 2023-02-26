package security.springsecurity.User.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import security.springsecurity.User.Repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
