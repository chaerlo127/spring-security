package security.springsecurity.User.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import security.springsecurity.User.Service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
