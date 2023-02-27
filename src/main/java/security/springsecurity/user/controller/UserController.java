package security.springsecurity.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import security.springsecurity.Util.exception.BaseException;
import security.springsecurity.Util.exception.BaseResponse;
import security.springsecurity.Util.exception.BaseResponseStatus;
import security.springsecurity.user.DAO.TokenDto;
import security.springsecurity.user.DAO.UserDto;
import security.springsecurity.user.service.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<TokenDto> signUp(@RequestBody UserDto userDto){
        if(!StringUtils.hasText(userDto.getUserId()) || !StringUtils.hasText(userDto.getPassword())){
            return new BaseResponse<>(BaseResponseStatus.NULL_EXCEPTION);
        }
        try {
            return new BaseResponse<>(this.userService.signUp(userDto));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<String> getUserId(Principal principal){
        return new BaseResponse<>(principal.getName());
    }


}
