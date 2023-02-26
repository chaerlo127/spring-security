package security.springsecurity.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import security.springsecurity.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
